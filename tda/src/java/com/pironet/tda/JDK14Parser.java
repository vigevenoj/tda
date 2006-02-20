/*
 * JDK14Parser.java
 *
 * This file is part of TDA - Thread Dump Analysis Tool.
 *
 * Foobar is free software; you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: JDK14Parser.java,v 1.2 2006-02-20 09:47:43 irockel Exp $
 */

package com.pironet.tda;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Parses JDK14 Thread Dumps.
 * Needs to be closed after use (so inner stream is closed).
 *
 * @author irockel
 */
public class JDK14Parser implements DumpParser {
    String dumpFile = null;
    MutableTreeNode nextDump = null;
    BufferedReader bis = null;
    Map threadStore = null;
    
    int counter = 1;
    
    int lineCounter = 0;
    
    /** Creates a new instance of JDK14Parser */
    public JDK14Parser(String dumpFile, Map threadStore) {
        this.dumpFile = dumpFile;
        this.threadStore = threadStore;
    }
    
    public boolean hasMoreDumps() {
        nextDump = parseNext();
        return(nextDump != null);
    }
    
    public MutableTreeNode parseNext() {
        if (nextDump != null) {
            MutableTreeNode tmpDump = nextDump;
            nextDump = null;
            return(tmpDump);
        }
        
        DefaultMutableTreeNode threadDump = null;
        ThreadDumpInfo overallTDI = null;
        DefaultMutableTreeNode catMonitors = null;
        DefaultMutableTreeNode catThreads = null;
        DefaultMutableTreeNode catLocking = null;
        DefaultMutableTreeNode catSleeping = null;
        DefaultMutableTreeNode catWaiting = null;
        DefaultMutableTreeNode threadInfo = null;
        
        try {
            Map threads = new HashMap();
            if(bis == null) {
                bis = new BufferedReader(new FileReader(dumpFile));
            }
            overallTDI = new ThreadDumpInfo("Full Thread Dump No. " + counter++, "");
            threadDump = new DefaultMutableTreeNode(overallTDI);
            
            catThreads = new DefaultMutableTreeNode("Threads");
            threadDump.add(catThreads);
            
            catWaiting = new DefaultMutableTreeNode("Threads waiting for Monitors");
            threadDump.add(catWaiting);
            
            catSleeping = new DefaultMutableTreeNode("Threads sleeping on Monitors");
            threadDump.add(catSleeping);

            catLocking = new DefaultMutableTreeNode("Threads locking Monitors");
            threadDump.add(catLocking);
            
            catMonitors = new DefaultMutableTreeNode("Monitors");
            threadDump.add(catMonitors);
            
            String title = null;
            String dumpKey = null;
            StringBuffer content = null;
            StringBuffer lContent = null;
            StringBuffer sContent = null;
            StringBuffer wContent = null;
            int threadCount = 0;
            int waiting = 0;
            int locking = 0;
            int sleeping = 0;
            boolean locked = true;
            boolean finished = false;
            MonitorMap mmap = new MonitorMap();
            Stack monitorStack = new Stack();
            long startTime = 0;
            
            while(bis.ready() && !finished) {
                String line = bis.readLine();
                lineCounter++;
                if(locked) {
                    if(line.startsWith("Full thread dump")) {
                        locked = false;
                        overallTDI.threadName += " at line " + lineCounter;
                        if(startTime != 0) {
                            overallTDI.threadName += " around " + new Date(startTime*1000);
                            startTime = 0;
                        }
                        dumpKey = overallTDI.threadName;
                    } else if(line.startsWith("timeStamp=")) {
                        try {
                            startTime = Long.parseLong(line.substring(line.indexOf('=')+1));
                        } catch (NumberFormatException nfe) {
                            startTime = 0;
                        }
                    }
                } else {
                    if(line.startsWith("\"")) {
                        if(title != null) {
                            threads.put(title, content.toString());
                            createNode(catThreads, title, content);
                            threadCount++;
                        }
                        if(wContent != null) {
                            wContent.append("\n\n" + "-----------------------------------\n\n");
                            wContent.append(content);
                            createNode(catWaiting, title, wContent);
                            wContent = null;
                            waiting++;
                        }
                        if(sContent != null) {
                            sContent.append("\n\n" + "-----------------------------------\n\n");
                            sContent.append(content);
                            createNode(catSleeping, title, sContent);
                            sContent = null;
                            sleeping++;
                        }
                        if(lContent != null) {
                            lContent.append("\n\n" + "-----------------------------------\n\n");
                            lContent.append(content);
                            createNode(catLocking, title, lContent);
                            lContent = null;
                            locking++;
                        }
                        while(!monitorStack.empty()) {
                            mmap.parseAndAddThread((String)monitorStack.pop(), title, content.toString());
                        }
                        
                        title = line;
                        content = new StringBuffer(line);
                        content.append("\n");
                    } else if (line.trim().startsWith("at ")) {
                        content.append(line);
                        content.append("\n");
                    } else if (line.trim().startsWith("- waiting on")) {
                        content.append(line);
                        if(sContent == null) {
                            sContent = new StringBuffer(line);
                        } else {
                            sContent.append(line);
                        }
                        monitorStack.push(line);
                        sContent.append("\n");
                        content.append("\n");
                    } else if (line.trim().startsWith("- waiting to")) {
                        content.append(line);
                        if(wContent == null) {
                            wContent = new StringBuffer(line);
                        } else {
                            wContent.append(line);
                        }
                        monitorStack.push(line);
                        wContent.append("\n");
                        content.append("\n");
                    } else if (line.trim().startsWith("- locked")) {
                        content.append(line);
                        if(lContent == null) {
                            lContent = new StringBuffer(line);
                        } else {
                            lContent.append(line);
                        }
                        monitorStack.push(line);
                        lContent.append("\n");
                        content.append("\n");
                    }
                    
                    // last thread reached?
                    if(line.startsWith("\"Suspend Checker Thread\"")) {
                        finished = true;
                    }
                }
            }
            StringBuffer statData = new StringBuffer("Overall Thread Count is ");
            statData.append(threadCount);
            statData.append("\n\nNumber of threads waiting for a monitor is ");
            statData.append(waiting);
            statData.append("\n\nNumber of threads locking a monitor is ");
            statData.append(locking);
            statData.append("\n\nNumber of threads sleeping on a monitor is ");
            statData.append(sleeping);
            overallTDI.content = statData.toString();
            
            // last thread
            if(title != null) {
                createNode(catThreads, title, content);
            }
            if(wContent != null) {
                createNode(catLocking, title, wContent);
                wContent = null;
            }
            if(lContent != null) {
                createNode(catLocking, title, lContent);
                lContent = null;
            }
            
            // dump monitors 
            if(mmap.size() > 0) {
                dumpMonitors(catMonitors, mmap);
            }
            
            // add thread dump to passed dump store.
            if((threadCount > 0) && (dumpKey != null)) {
                System.out.println("adding dump " + dumpKey);
                threadStore.put(dumpKey.trim(), threads);
            }
            return(threadCount > 0? threadDump : null);
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return(null);
    }
    
    private void dumpMonitors(DefaultMutableTreeNode catMonitors, MonitorMap mmap) {
        Iterator iter = mmap.iterOfKeys();
        while(iter.hasNext()) {
            String monitor = (String) iter.next();
            Set[] threads = mmap.getFromMonitorMap(monitor);
            ThreadDumpInfo mi = new ThreadDumpInfo(monitor, "");
            
            DefaultMutableTreeNode monitorNode = new DefaultMutableTreeNode(mi);
            
            // first the locks
            Iterator iterLocks = threads[0].iterator();
            int locks = 0;
            while(iterLocks.hasNext()) {
                String[] thread = (String[]) iterLocks.next();
                createNode(monitorNode, "locked by " + thread[0], thread[1]);
                locks++;
            }
            
            // first the locks
            Iterator iterSleeps = threads[2].iterator();
            int sleeps = 0;
            while(iterSleeps.hasNext()) {
                String[] thread = (String[]) iterSleeps.next();
                createNode(monitorNode, "sleeps on lock " + thread[0], thread[1]);
                sleeps++;
            }
            
            // now the waits
            Iterator iterWaits = threads[1].iterator();
            int waits = 0;
            while(iterWaits.hasNext()) {
                String[] thread = (String[]) iterWaits.next();
                createNode(monitorNode, "waiting " + thread[0], thread[1]);
                waits++;
            }
            StringBuffer statData = new StringBuffer ("Threads locking monitor: ");
            statData.append(locks);
            statData.append("\n\n");
            statData.append("Threads sleeping on monitor: ");
            statData.append(sleeps);
            statData.append("\n\n");
            statData.append("Threads waiting to lock monitor: ");
            statData.append(waits);
            statData.append("\n\n");
            mi.content = statData.toString();
            
            catMonitors.add(monitorNode);
        }
    }
    
    private static void createNode(DefaultMutableTreeNode category, String title, StringBuffer content) {
        createNode(category, title, content.toString());
    }
    
    private static void createNode(DefaultMutableTreeNode category, String title, String content) {
        DefaultMutableTreeNode threadInfo = null;
        threadInfo = new DefaultMutableTreeNode(new ThreadDumpInfo(title, content));
        category.add(threadInfo);
    }
    
    private static String getDumpStringFromTreePath(TreePath path) {
        String[] elems = path.toString().split(",");
        if(elems.length > 1) {
            return(elems[1].substring(0, elems[1].lastIndexOf(']')).trim());
        } else {
            return null;
        }
    }
    
    public static void mergeDumps(DefaultMutableTreeNode root, Map dumpStore, TreePath firstDump, TreePath secondDump) {
        String firstDumpKey = getDumpStringFromTreePath(firstDump);
        String secondDumpKey = getDumpStringFromTreePath(secondDump);
        
        Map firstThreads = (Map) dumpStore.get(firstDumpKey);
        Map secondThreads = (Map) dumpStore.get(secondDumpKey);
        
        DefaultMutableTreeNode catMerge = new DefaultMutableTreeNode("Merge between " + firstDumpKey + " and " + secondDumpKey);
        root.add(catMerge);
        
        if(firstThreads != null) {
            Iterator dumpIter = firstThreads.keySet().iterator();
            
            while(dumpIter.hasNext()) {
                String threadKey = ((String) dumpIter.next()).trim();
                if(secondThreads.containsKey(threadKey)) {
                    StringBuffer content = new StringBuffer((String) firstThreads.get(threadKey));
                    content.append("\n\n---------------------------------\n\n");
                    content.append((String) secondThreads.get(threadKey));
                    System.out.println("found: " + threadKey);
                    createNode(catMerge, threadKey, content);
                }
            }
        }
        
    }
    
    public void close() throws IOException {
        if(bis != null) {
            bis.close();
        }
    }
}
