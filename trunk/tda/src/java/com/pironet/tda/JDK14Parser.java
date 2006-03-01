/*
 * JDK14Parser.java
 *
 * This file is part of TDA - Thread Dump Analysis Tool.
 *
 * TDA is free software; you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * TDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with TDA; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: JDK14Parser.java,v 1.5 2006-03-01 19:19:37 irockel Exp $
 */

package com.pironet.tda;

import com.pironet.tda.utils.HistogramTableModel;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
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
    private static int MARK_SIZE = 16384;
    private static int MAX_CHECK_LINES = 10;
    
    InputStream dumpFileStream = null;
    MutableTreeNode nextDump = null;
    BufferedReader bis = null;
    Map threadStore = null;
    
    int counter = 1;
    
    int lineCounter = 0;
    
    /** Creates a new instance of JDK14Parser */
    public JDK14Parser(InputStream dumpFileStream, Map threadStore) {
        this.dumpFileStream = dumpFileStream;
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
        ThreadInfo overallTDI = null;
        DefaultMutableTreeNode catMonitors = null;
        DefaultMutableTreeNode catThreads = null;
        DefaultMutableTreeNode catLocking = null;
        DefaultMutableTreeNode catSleeping = null;
        DefaultMutableTreeNode catWaiting = null;
        DefaultMutableTreeNode threadInfo = null;
        
        try {
            Map threads = new HashMap();
            if(bis == null) {
                bis = new BufferedReader(new InputStreamReader(dumpFileStream));
            }
            overallTDI = new ThreadInfo("Full Thread Dump No. " + counter++, "");
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
            int deadlocks = 0;
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
                        bis.mark(MARK_SIZE);
                        if((deadlocks = checkForDeadlocks(threadDump)) == 0) {
                            // no deadlocks found, set back original position.
                            bis.reset();
                        }
                        
                        bis.mark(MARK_SIZE);
                        if(!checkForClassHistogram(threadDump)) {
                            bis.reset();
                        }
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
            statData.append("\n\nNumber of deadlocks is ");
            statData.append(deadlocks);
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
                //System.out.println("adding dump " + dumpKey);
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
    
    private boolean checkForClassHistogram(DefaultMutableTreeNode threadDump) throws IOException {
        boolean finished = false;
        boolean found = false;
        HistogramTableModel classHistogram = new HistogramTableModel();
        int lineCounter = 0;
        DefaultMutableTreeNode catHistogram;
        
        while(bis.ready() && !finished) {
            String line = bis.readLine();
            if(!found && !line.trim().equals("")) {
                if (line.startsWith("num   #instances    #bytes  class name")) {
                    found = true;
                } else if(lineCounter >= MAX_CHECK_LINES) {
                    finished = true;
                } else {
                    lineCounter++;
                }
            } else if(found) {
                if(line.startsWith("Total ")) {
                    finished = true;
                } else if(!line.startsWith("-------")) {
                    String newLine = line.trim().replaceAll("(\\s)+", ";");
                    String[] elems = newLine.split(";");
                    classHistogram.addEntry(elems[3].trim(),Integer.parseInt(elems[2].trim()),
                            Integer.parseInt(elems[1].trim()));
                }
            }
        }
        if(classHistogram.getRowCount() > 0) {
            HistogramInfo hi = new HistogramInfo("Class Histogram of Dump", classHistogram);
            catHistogram = new DefaultMutableTreeNode(hi);
            threadDump.add(catHistogram);
        }
        
        return(classHistogram.getRowCount() > 0);
    }
    
    private int checkForDeadlocks(DefaultMutableTreeNode threadDump) throws IOException {
        boolean finished = false;
        boolean found = false;
        int deadlocks = 0;
        int lineCounter = 0;
        StringBuffer dContent = new StringBuffer();
        DefaultMutableTreeNode catDeadlocks = new DefaultMutableTreeNode("Deadlocks");
        
        while(bis.ready() && !finished) {
            String line = bis.readLine();
            if(!found && !line.trim().equals("")) {
                if (line.startsWith("Found one Java-level deadlock")) {
                    found = true;
                    dContent.append(line);
                    dContent.append("\n\n");
                } else if(lineCounter < MAX_CHECK_LINES) {
                    finished = true;
                } else {
                    lineCounter++;
                }
            } else if(found) {
                if(line.startsWith("Found one Java-level deadlock")) {
                    if(dContent.length() > 0) {
                        deadlocks++;
                        createNode(catDeadlocks, "Deadlock No. " + (deadlocks), dContent);
                    }
                    dContent = new StringBuffer();
                } else if(line.startsWith("Found") && line.trim().endsWith("deadlock.")) {
                    finished = true;
                } else {
                    dContent.append(line);
                    dContent.append("\n");
                }
            }
        }
        if(dContent.length() > 0) {
            deadlocks++;
            createNode(catDeadlocks, "Deadlock No. " + (deadlocks), dContent);
        }
        
        if(deadlocks > 0) {
            threadDump.add(catDeadlocks);
        }
        
        return(deadlocks);
    }
    
    private void dumpMonitors(DefaultMutableTreeNode catMonitors, MonitorMap mmap) {
        Iterator iter = mmap.iterOfKeys();
        while(iter.hasNext()) {
            String monitor = (String) iter.next();
            Set[] threads = mmap.getFromMonitorMap(monitor);
            ThreadInfo mi = new ThreadInfo(monitor, "");
            
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
    
    private void createNode(DefaultMutableTreeNode category, String title, StringBuffer content) {
        createNode(category, title, content.toString());
    }
    
    private void createNode(DefaultMutableTreeNode category, String title, String content) {
        DefaultMutableTreeNode threadInfo = null;
        threadInfo = new DefaultMutableTreeNode(new ThreadInfo(title, content));
        category.add(threadInfo);
    }
    
    private String getDumpStringFromTreePath(TreePath path) {
        String[] elems = path.toString().split(",");
        if(elems.length > 1) {
            return(elems[1].substring(0, elems[1].lastIndexOf(']')).trim());
        } else {
            return null;
        }
    }
    
    public void mergeDumps(DefaultMutableTreeNode root, Map dumpStore, TreePath firstDump, TreePath secondDump) {
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
                    createNode(catMerge, threadKey, content);
                }
            }
        }
        
    }
    
    public void close() throws IOException {
        if(bis != null) {
            bis.close();
        }
        
        if(dumpFileStream != null) {
            dumpFileStream.close();
        }
    }
}
