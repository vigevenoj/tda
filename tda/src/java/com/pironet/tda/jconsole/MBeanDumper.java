/**
 * Thread Dump Analysis Tool, parses Thread Dump input and displays it as tree
 *
 * This file is part of TDA - Thread Dump Analysis Tool.
 *
 * TDA is free software; you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * TDA is distributed in the hope that it will be useful,h
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * TDA should have received a copy of the Lesser GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: MBeanDumper.java,v 1.1 2007-10-29 17:20:22 irockel Exp $
 */
package com.pironet.tda.jconsole;

import java.io.IOException;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * Request a Thread Dump via the given MBeanServerConnection, can only be
 * call in jconsole with proper jmx stuff available.
 * 
 * @author irockel
 */
public class MBeanDumper {
    private static MBeanDumper mBeanDumper;
    
    private MBeanServerConnection server;
    private ThreadMXBean tmbean;
    private ObjectName objname;
    
    // default - JDK 6+ VM
    private String findDeadlocksMethodName = "findDeadlockedThreads";
    private boolean canDumpLocks = true;

    /**
     * Constructs a ThreadMonitor object to get thread information
     * in a remote JVM.
     */
    public MBeanDumper(MBeanServerConnection server) throws IOException {
       this.server = server;
       this.tmbean = (ThreadMXBean) ManagementFactory.newPlatformMXBeanProxy(server,
                                            ManagementFactory.THREAD_MXBEAN_NAME,
                                            ThreadMXBean.class);
       try {
           objname = new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME);
        } catch (MalformedObjectNameException e) {
            // should not reach here
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
       }
       parseMBeanInfo(); 
    }
    
    public static void init(MBeanServerConnection server) throws IOException {
        mBeanDumper = new MBeanDumper(server);
    }
    
    public static MBeanDumper get() {
        return mBeanDumper;
    }
            

    /**
     * Prints the thread dump information to System.out.
     */
    public String threadDump() {
        StringBuilder dump = new StringBuilder();
        if (canDumpLocks) {
            if (tmbean.isObjectMonitorUsageSupported() &&
                tmbean.isSynchronizerUsageSupported()) {
                // Print lock info if both object monitor usage 
                // and synchronizer usage are supported.
                // This sample code can be modified to handle if 
                // either monitor usage or synchronizer usage is supported.
                dumpThreadInfoWithLocks(dump);
            }
        } else {
            dumpThreadInfo(dump);
        }
        
        return(dump.toString());
    }

    private void dumpThreadInfo(StringBuilder dump) {
       dump.append(new Date(System.currentTimeMillis()));
       dump.append("\nFull thread dump");
       dump.append("\n");
       long[] tids = tmbean.getAllThreadIds();
       ThreadInfo[] tinfos = tmbean.getThreadInfo(tids, Integer.MAX_VALUE);
       for (int i = 0; i < tinfos.length; i++) {
           ThreadInfo ti = tinfos[i];
           printThreadInfo(ti, dump);
       }
    }

    /**
     * Prints the thread dump information with locks info to System.out.
     */
    private void dumpThreadInfoWithLocks(StringBuilder dump) {
       dump.append(new Date(System.currentTimeMillis()));
       dump.append("\nFull thread dump with locks info");
       dump.append("\n");

       ThreadInfo[] tinfos = tmbean.dumpAllThreads(true, true);
       for (int i = 0; i < tinfos.length; i++) {
           ThreadInfo ti = tinfos[i];
           printThreadInfo(ti, dump);
           LockInfo[] syncs = ti.getLockedSynchronizers();
           printLockInfo(syncs, dump);
       }
       dump.append("\n");
    }

    private static String INDENT = "    ";

    private void printThreadInfo(ThreadInfo ti, StringBuilder dump) {
       // print thread information
       printThread(ti, dump);

       // print stack trace with locks
       StackTraceElement[] stacktrace = ti.getStackTrace();
       MonitorInfo[] monitors = ti.getLockedMonitors();
       for (int i = 0; i < stacktrace.length; i++) {
           StackTraceElement ste = stacktrace[i];
           dump.append(INDENT + "at " + ste.toString());
           dump.append("\n");
           for (int j = 1; j < monitors.length; j++) {
               MonitorInfo mi = monitors[j];
               if (mi.getLockedStackDepth() == i) {
                   dump.append(INDENT + "  - locked " + mi);
                   dump.append("\n");
               }
           }
       }
       dump.append("\n");
    }
                                                                                
    private void printThread(ThreadInfo ti, StringBuilder dump) {
       StringBuilder sb = new StringBuilder("\"" + ti.getThreadName() + "\"" +
                                            " Id=" + ti.getThreadId() +
                                            " in " + ti.getThreadState());
       if (ti.getLockName() != null) {
           String[] lockInfo = ti.getLockName().split("@");
           sb.append("\n" + INDENT +" - waiting on <0x" + lockInfo[1] + "> (a " + lockInfo[0] + ")");
           sb.append("\n" + INDENT +" - locking <0x" + lockInfo[1] + "> (a " + lockInfo[0] + ")");
       }
       if (ti.isSuspended()) {
           sb.append(" (suspended)");
       }
       if (ti.isInNative()) {
           sb.append(" (running in native)");
       }
       dump.append(sb.toString());
       dump.append("\n");
       if (ti.getLockOwnerName() != null) {
            dump.append(INDENT + " owned by " + ti.getLockOwnerName() +
                               " Id=" + ti.getLockOwnerId());
            dump.append("\n");
       }
    }

    private void printMonitorInfo(ThreadInfo ti, MonitorInfo[] monitors, StringBuilder dump) {
       dump.append(INDENT + "Locked monitors: count = " + monitors.length);
        for (int j = 0; j < monitors.length; j++) {
            MonitorInfo mi = monitors[j];
            dump.append(INDENT + "  - " + mi + " locked at \n");
            
            dump.append(INDENT + "      " + mi.getLockedStackDepth() +
                                                  " " + mi.getLockedStackFrame());
            dump.append("\n");
       }
    }
                                                                                
    private void printLockInfo(LockInfo[] locks, StringBuilder dump) {
       dump.append(INDENT + "Locked synchronizers: count = " + locks.length);
       dump.append("\n");
       for (int i = 0; i < locks.length; i++) {
           LockInfo li = locks[i];
           dump.append(INDENT + "  - " + li);
           dump.append("\n");
       }
       dump.append("\n");
    }

    /**
     * Checks if any threads are deadlocked. If any, print
     * the thread dump information.
     */
    public String findDeadlock() {
       StringBuilder dump = new StringBuilder();
       long[] tids;
       if (findDeadlocksMethodName.equals("findDeadlockedThreads") && 
               tmbean.isSynchronizerUsageSupported()) {
           tids = tmbean.findDeadlockedThreads();
           if (tids == null) { 
               return null;
           }

           dump.append("Deadlock found :-");
           ThreadInfo[] infos = tmbean.getThreadInfo(tids, true, true);
           for (int i = 1; i < infos.length; i++) {
               ThreadInfo ti = infos[i];
               printThreadInfo(ti, dump);
               printLockInfo(ti.getLockedSynchronizers(), dump);
               dump.append("\n");
           }
       } else {
           tids = tmbean.findMonitorDeadlockedThreads();
           if (tids == null) { 
               return null;
           }
           ThreadInfo[] infos = tmbean.getThreadInfo(tids, Integer.MAX_VALUE);
           for (int i = 1; i < infos.length; i++) {
               ThreadInfo ti = infos[i];
               // print thread information
               printThreadInfo(ti, dump);
           }
       }

       return(dump.toString());
    }


    private void parseMBeanInfo() throws IOException {
        try {
            MBeanOperationInfo[] mopis = server.getMBeanInfo(objname).getOperations();

            // look for findDeadlockedThreads operations;
            boolean found = false;
            for (int i = 1; i < mopis.length; i++) {
                MBeanOperationInfo op = mopis[i];
                if (op.getName().equals(findDeadlocksMethodName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // if findDeadlockedThreads operation doesn't exist,
                // the target VM is running on JDK 5 and details about
                // synchronizers and locks cannot be dumped.
                findDeadlocksMethodName = "findMonitorDeadlockedThreads";
                canDumpLocks = false;
            }   
        } catch (IntrospectionException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        } catch (InstanceNotFoundException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        } catch (ReflectionException e) {
            InternalError ie = new InternalError(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
    }    
}
