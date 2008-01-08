/*
 * SunJDKParser.java
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
 * $Id: SunJDKParser.java,v 1.23 2008-01-08 08:37:03 irockel Exp $
 */

package com.pironet.tda;

import com.pironet.tda.utils.HistogramTableModel;
import com.pironet.tda.utils.IconFactory;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * Parses SunJDK Thread Dumps.
 * Needs to be closed after use (so inner stream is closed).
 *
 * @author irockel
 */
public class SunJDKParser extends AbstractDumpParser {
    private MutableTreeNode nextDump = null;
    private Map threadStore = null;
    private int counter = 1;
    private int lineCounter = 0;
    private boolean foundClassHistograms = false;
    private boolean withCurrentTimeStamp = false;
    
    /** 
     * Creates a new instance of SunJDKParser 
     */
    public SunJDKParser(BufferedReader bis, Map threadStore, int lineCounter, boolean withCurrentTimeStamp) {
        super(bis);
        this.threadStore = threadStore;
        this.withCurrentTimeStamp = withCurrentTimeStamp;
        this.lineCounter = lineCounter;
    }
    
    /**
     * returns true if at least one more dump available, already loads it
     * (this will be returned on next call of parseNext)
     */
    public boolean hasMoreDumps() {
        nextDump = parseNext();
        return(nextDump != null);
    }
    
    /**
     * @returns true, if a class histogram was found and added during parsing.
     */
    public boolean isFoundClassHistograms() {
        return(foundClassHistograms);
    }
    
    /**
     * parse the next thread dump from the stream passed with the constructor.
     * @returns null if no more thread dumps were found.
     */
    public MutableTreeNode parseNext() {
        if (nextDump != null) {
            MutableTreeNode tmpDump = nextDump;
            nextDump = null;
            return(tmpDump);
        }
        
        DefaultMutableTreeNode threadDump = null;
        ThreadDumpInfo overallTDI = null;
        DefaultMutableTreeNode catMonitors = null;
        DefaultMutableTreeNode catMonitorsLocks = null;
        DefaultMutableTreeNode catThreads = null;
        DefaultMutableTreeNode catLocking = null;
        DefaultMutableTreeNode catSleeping = null;
        DefaultMutableTreeNode catWaiting = null;
        
        try {
            Map threads = new HashMap();
            if(withCurrentTimeStamp) {
                overallTDI = new ThreadDumpInfo("Dump at " + new Date(System.currentTimeMillis()), 0);
            } else {
                overallTDI = new ThreadDumpInfo("Dump No. " + counter++, 0);
            }
            threadDump = new DefaultMutableTreeNode(overallTDI);
            
            catThreads = new DefaultMutableTreeNode(new TableCategory("Threads", IconFactory.THREADS));
            threadDump.add(catThreads);
            
            catWaiting = new DefaultMutableTreeNode(new TableCategory("Threads waiting for Monitors", IconFactory.THREADS_WAITING));
            
            catSleeping = new DefaultMutableTreeNode(new TableCategory("Threads sleeping on Monitors", IconFactory.THREADS_SLEEPING));

            catLocking = new DefaultMutableTreeNode(new TableCategory("Threads locking Monitors", IconFactory.THREADS_LOCKING));
            
            // create category for monitors with disabled filtering.
            catMonitors = new DefaultMutableTreeNode(new TreeCategory("Monitors", IconFactory.MONITORS, false));
            catMonitorsLocks = new DefaultMutableTreeNode(new TreeCategory("Monitors without locking thread", IconFactory.MONITORS_NOLOCKS, false));
            
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
            int singleLineCounter = 0;
            Matcher matched = null;
            
            while(getBis().ready() && !finished) {
                String line = getBis().readLine();
                lineCounter++;
                singleLineCounter++;
                if(locked) {
                    if(line.indexOf("Full thread dump") >= 0) {
                        locked = false;
                        if(!withCurrentTimeStamp) {
                            overallTDI.setLogLine(lineCounter);
                            
                            if (startTime != 0) {
                                startTime = 0;
                            } else if (matched != null && matched.matches()) {

                                String parsedStartTime = matched.group(1);
                                if (isMillisTimeStamp()) {
                                    try {
                                        // the factor is a hack for a bug in oc4j timestamp printing (pattern timeStamp=2342342340)
                                        if (parsedStartTime.length() < 13) {
                                            startTime = Long.parseLong(parsedStartTime) * (long) Math.pow(10, 13 - parsedStartTime.length());
                                        } else {
                                            startTime = Long.parseLong(parsedStartTime);
                                        }
                                    } catch (NumberFormatException nfe) {
                                        startTime = 0;
                                    }
                                    overallTDI.setStartTime((new Date(startTime)).toString());
                                } else {
                                    overallTDI.setStartTime(parsedStartTime);
                                }
                                parsedStartTime = null;
                            }
                        }
                        dumpKey = overallTDI.getName();
                    } else if(!isPatternError() && (getRegexPattern() != null)) {
                        try {
                            Matcher m = getRegexPattern().matcher(line);
                            if(m.matches()) {
                                matched = m;
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Error during parsing line for timestamp regular expression!\n" +
                                    "Please check regular expression in your preferences. Deactivating\n" +
                                    "parsing for the rest of the file! Error Message is " + ex.getMessage() + " \n",
                                    "Error during Parsing", JOptionPane.ERROR_MESSAGE);
                            
                            //System.out.println("Failed parsing! " + ex.getMessage());
                            //ex.printStackTrace();
                            setPatternError(true);
                        }
                    }
                } else {
                    if(line.startsWith("\"")) {
                        if(title != null) {
                            threads.put(title, content.toString());
                            content.append("</pre></pre>");
                            addToCategory(catThreads, title, null, content, singleLineCounter);
                            threadCount++;
                        }
                        if(wContent != null) {
                            wContent.append("</b><hr>");
                            addToCategory(catWaiting, title, wContent, content, singleLineCounter);
                            wContent = null;
                            waiting++;
                        }
                        if(sContent != null) {
                            sContent.append("</b><hr>");
                            addToCategory(catSleeping, title, sContent, content, singleLineCounter);
                            sContent = null;
                            sleeping++;
                        }
                        if(lContent != null) {
                            lContent.append("</b><hr>");
                            addToCategory(catLocking, title, lContent, content, singleLineCounter);
                            lContent = null;
                            locking++;
                        }
                        singleLineCounter = 0;
                        while(!monitorStack.empty()) {
                            mmap.parseAndAddThread((String)monitorStack.pop(), title, content.toString());
                        }
                        
                        title = line;
                        content = new StringBuffer("<body bgcolor=\"ffffff\"><pre><font size=" + TDA.getFontSizeModifier(-1) + ">");
                        content.append(line);
                        content.append("\n");
                    } else if (line.indexOf("at ") >= 0) {
                        content.append(line);
                        content.append("\n");
                    } else if (line.indexOf("java.lang.Thread.State") >= 0) {
                        content.append(line);
                        content.append("\n");
                    } else if (line.indexOf("Locked ownable synchronizers:") >= 0) {
                        content.append(line);
                        content.append("\n");
                    } else if (line.indexOf("- waiting on") >= 0) {
                        String newLine = linkifyMonitor(line);
                        content.append(newLine);
                        if(sContent == null) {
                            sContent = new StringBuffer("<body bgcolor=\"ffffff\"><font size=" + TDA.getFontSizeModifier(-1) + "><b>");
                        }
                        sContent.append(newLine);
                        monitorStack.push(line);
                        sContent.append("\n");
                        content.append("\n");
                    } else if (line.indexOf("- waiting to") >= 0) {
                        String newLine = linkifyMonitor(line);
                        content.append(newLine);
                        if(wContent == null) {
                            wContent = new StringBuffer("<body bgcolor=\"ffffff\"><font size=" + TDA.getFontSizeModifier(-1) + "><b>");
                        }
                        wContent.append(newLine);
                        monitorStack.push(line);
                        wContent.append("\n");
                        content.append("\n");
                    } else if (line.indexOf("- locked <") >= 0) {
                        String newLine = linkifyMonitor(line);
                        content.append(newLine);
                        if(lContent == null) {
                            lContent = new StringBuffer("<body bgcolor=\"ffffff\"><font size=" + TDA.getFontSizeModifier(-1) + "><b>");
                        }
                        lContent.append(newLine);
                        monitorStack.push(line);
                        lContent.append("\n");
                        content.append("\n");
                    } else if (line.indexOf("- ") >= 0) {
                        content.append(line);
                        content.append("\n");
                    }
                    
                    // last thread reached?
                    if((line.indexOf("\"Suspend Checker Thread\"") >= 0) ||
                       (line.indexOf("\"VM Periodic Task Thread\"") >= 0) ||
                       (line.indexOf("<EndOfDump>") >= 0)) {
                        finished = true;
                        getBis().mark(getMarkSize());
                        if((checkForDeadlocks(threadDump)) == 0) {
                            // no deadlocks found, set back original position.
                            getBis().reset();
                        }
                        
                        getBis().mark(getMarkSize());
                        if(!(foundClassHistograms = checkForClassHistogram(threadDump))) {
                            getBis().reset();
                        }
                    }
                }
            }
            // last thread
            if(title != null) {
                threads.put(title, content.toString());
                content.append("</pre></pre>");
                addToCategory(catThreads, title, null, content, singleLineCounter);
                threadCount++;
            }
            if(wContent != null) {
                wContent.append("</b><hr>");
                addToCategory(catWaiting, title, null, wContent, singleLineCounter);
                wContent = null;
                waiting++;
            }
            if(sContent != null) {
                sContent.append("</b><hr>");
                addToCategory(catSleeping, title, sContent, content, singleLineCounter);
                sContent = null;
                sleeping++;
            }
            if(lContent != null) {
                lContent.append("</b><hr>");
                addToCategory(catLocking, title, null, lContent, singleLineCounter);
                lContent = null;
                locking++;
            }
            
            int monitorCount = mmap.size();
                        
            int monitorsWithoutLocksCount = 0;
            // dump monitors 
            if(mmap.size() > 0) {
                int[] result = dumpMonitors(catMonitors, catMonitorsLocks, mmap);
                monitorsWithoutLocksCount = result[0];
                overallTDI.setOverallThreadsWaitingWithoutLocksCount(result[1]);
            }
            
            // display nodes with stuff to display
            if(waiting > 0) {
                overallTDI.setWaitingThreads((Category) catWaiting.getUserObject());
                threadDump.add(catWaiting);
            }
            
            if(sleeping > 0) {
                overallTDI.setSleepingThreads((Category) catSleeping.getUserObject());
                threadDump.add(catSleeping);
            }

            if(locking > 0) {
                overallTDI.setLockingThreads((Category) catLocking.getUserObject());
                threadDump.add(catLocking);
            }
            
            if(monitorCount > 0) {
                overallTDI.setMonitors((Category) catMonitors.getUserObject());
                threadDump.add(catMonitors);
            }
            
            if(monitorsWithoutLocksCount > 0) {
                overallTDI.setMonitorsWithoutLocks((Category) catMonitorsLocks.getUserObject());
                threadDump.add(catMonitorsLocks);
            }
            overallTDI.setThreads((Category) catThreads.getUserObject());
            
            ((Category) catThreads.getUserObject()).setName(((Category) catThreads.getUserObject()) + " (" + threadCount + " Threads overall)");
            ((Category) catWaiting.getUserObject()).setName(((Category) catWaiting.getUserObject()) + " (" + waiting + " Threads waiting)");
            ((Category) catSleeping.getUserObject()).setName(((Category) catSleeping.getUserObject()) + " (" + sleeping + " Threads sleeping)");
            ((Category) catLocking.getUserObject()).setName(((Category) catLocking.getUserObject()) + " (" + locking + " Threads locking)");
            ((Category) catMonitors.getUserObject()).setName(((Category) catMonitors.getUserObject()) + " (" + monitorCount + " Monitors)");
            ((Category) catMonitorsLocks.getUserObject()).setName(((Category) catMonitorsLocks.getUserObject()) + " (" + monitorsWithoutLocksCount + 
                    " Monitors)");
            // add thread dump to passed dump store.
            if((threadCount > 0) && (dumpKey != null)) {
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
    
    /**
     * add a monitor link for monitor navigation
     * @param line containing monitor
     */
    private String linkifyMonitor(String line) {
        if(line != null && line.indexOf('<') >= 0) {
            String begin = line.substring(0, line.indexOf('<'));
            String monitor = line.substring(line.indexOf('<'), line.indexOf('>') + 1);
            String end = line.substring(line.indexOf('>') + 1);
            monitor = monitor.replaceAll("<", "<a href=\"monitor://" + monitor + "\">&lt;");
            monitor = monitor.substring(0, monitor.length() - 1) + "&gt;</a>";
            return(begin + monitor + end);
        } else {
            return(line);
        }
    }
    
    /**
     * add a monitor link for monitor navigation
     * @param line containing monitor
     */
    private String linkifyDeadlockInfo(String line) {
        if(line != null && line.indexOf("Ox") >= 0) {
            String begin = line.substring(0, line.indexOf("0x"));
            int objectBegin = line.lastIndexOf("0x");
            int monitorBegin = line.indexOf("0x");
            String monitorHex = line.substring(monitorBegin, monitorBegin + 10);

            String monitor = line.substring(objectBegin, objectBegin + 10);
            String end = line.substring(line.indexOf("0x") + 10);

            monitor = "<a href=\"monitor://<" + monitor + ">\">" + monitorHex + "</a>";
            return(begin + monitor + end);
        } else {
            return(line);
        }
    }
    
    /**
     * checks for the next class histogram and adds it to the tree node passed
     * @param threadDump which tree node to add the histogram.
     */
    private boolean checkForClassHistogram(DefaultMutableTreeNode threadDump) throws IOException {
        HistogramTableModel classHistogram = parseNextClassHistogram(getBis());
        
        if(classHistogram.getRowCount() > 0) {
            addHistogramToDump(threadDump, classHistogram);            
        }
        
        return(classHistogram.getRowCount() > 0);
    }
    
    private void addHistogramToDump(DefaultMutableTreeNode threadDump, HistogramTableModel classHistogram) {
        DefaultMutableTreeNode catHistogram;
        HistogramInfo hi = new HistogramInfo("Class Histogram of Dump", classHistogram);
        catHistogram = new DefaultMutableTreeNode(hi);
        threadDump.add(catHistogram);
    }
    
    /**
     * parses the next class histogram found in the stream, uses the max check lines option to check
     * how many lines to parse in advance.
     * @param bis the stream to read.
     */
    private HistogramTableModel parseNextClassHistogram(BufferedReader bis) throws IOException {
        boolean finished = false;
        boolean found = false;
        HistogramTableModel classHistogram = new HistogramTableModel();
        int maxLinesCounter = 0;
        
        while(bis.ready() && !finished) {
            String line = bis.readLine().trim();
            if(!found && !line.equals("")) {
                if (line.startsWith("num   #instances    #bytes  class name")) {
                    found = true;
                } else if(maxLinesCounter >= getMaxCheckLines()) {
                    finished = true;
                } else {
                    maxLinesCounter++;
                }
            } else if(found) {
                if(line.startsWith("Total ")) {                    
                    // split string.
                    String newLine = line.replaceAll("(\\s)+", ";");
                    String[] elems = newLine.split(";");
                    classHistogram.setBytes(Long.parseLong(elems[2]));
                    classHistogram.setInstances(Long.parseLong(elems[1]));
                    finished = true;
                } else if(!line.startsWith("-------")) {
                    // removed blank, breaks splitting using blank...
                    String newLine = line.replaceAll("<no name>", "<no-name>");
                    
                    // split string.
                    newLine = newLine.replaceAll("(\\s)+", ";");
                    String[] elems = newLine.split(";");
                    
                    if(elems.length == 4) {
                        classHistogram.addEntry(elems[3].trim(),Integer.parseInt(elems[2].trim()),
                                Integer.parseInt(elems[1].trim()));
                    } else {
                        classHistogram.setIncomplete(true);
                        finished = true;
                    }
                    
                }
            }
        }
        
        return(classHistogram);
    }
    
    /**
     * check if any dead lock information is logged in the stream
     * @param threadDump which tree node to add the histogram.
     */
    private int checkForDeadlocks(DefaultMutableTreeNode threadDump) throws IOException {
        boolean finished = false;
        boolean found = false;
        int deadlocks = 0;
        int lineCounter = 0;
        StringBuffer dContent = new StringBuffer();
        TreeCategory deadlockCat = new TreeCategory("Deadlocks", IconFactory.DEADLOCKS);
        DefaultMutableTreeNode catDeadlocks = new DefaultMutableTreeNode(deadlockCat);
        boolean first = true;
        
        while(getBis().ready() && !finished) {            
            String line = getBis().readLine();
            
            if(!found && !line.equals("")) {
                if (line.trim().startsWith("Found one Java-level deadlock")) {
                    found = true;
                    dContent.append("<body bgcolor=\"ffffff\"><font size=" + TDA.getFontSizeModifier(-1) + "><b>");
                    dContent.append("Found one Java-level deadlock");
                    dContent.append("</b><hr></font><pre>\n");
                } else if(lineCounter >= getMaxCheckLines()) {
                    finished = true;
                } else {
                    lineCounter++;
                }
            } else if(found) {                
                if(line.startsWith("Found one Java-level deadlock")) {
                    if(dContent.length() > 0) {
                        deadlocks++;
                        addToCategory(catDeadlocks, "Deadlock No. " + (deadlocks), null, dContent, 0);
                    }
                    dContent = new StringBuffer();
                    dContent.append("</pre><b><font size=" + TDA.getFontSizeModifier(-1) + ">");
                    dContent.append("Found one Java-level deadlock");
                    dContent.append("</b><hr></font><pre>\n");
                    first = true;
                } else if((line.indexOf("Found") >= 0) && (line.endsWith("deadlocks.") || line.endsWith("deadlock."))) {
                    finished = true;
                } else if(line.startsWith("=======")) {                    
                    // ignore this line
                } else if(line.indexOf(" monitor 0x") >= 0) {
                    dContent.append(linkifyDeadlockInfo(line));
                    dContent.append("\n");
                } else if(line.indexOf("Java stack information for the threads listed above") >= 0) {
                    dContent.append("</pre><br><font size=" + TDA.getFontSizeModifier(-1) + "><b>");
                    dContent.append("Java stack information for the threads listed above");
                    dContent.append("</b><hr></font><pre>");
                    first = true;
                } else if ((line.indexOf("- waiting on") >= 0) ||
                           (line.indexOf("- waiting to") >= 0) ||
                           (line.indexOf("- locked") >= 0)) {
                    
                    dContent.append(linkifyMonitor(line));
                    dContent.append("\n");
                    
                } else if(line.trim().startsWith("\"")) {
                    dContent.append("</pre>");
                    if(first) {
                        first = false;
                    } else {
                        dContent.append("<br>");
                    }
                    dContent.append("<b><font size=" + TDA.getFontSizeModifier(-1) + "><code>");
                    dContent.append(line);
                    dContent.append("</font></code></b><pre>");                    
                } else {
                    dContent.append(line);
                    dContent.append("\n");
                }
            }
        }
        if(dContent.length() > 0) {
            deadlocks++;
            addToCategory(catDeadlocks, "Deadlock No. " + (deadlocks), null, dContent, 0);
        }
        
        if(deadlocks > 0) {
            threadDump.add(catDeadlocks);
            ((ThreadDumpInfo) threadDump.getUserObject()).setDeadlocks((TreeCategory) catDeadlocks.getUserObject());
            deadlockCat.setName("Deadlocks (" + deadlocks + (deadlocks == 1 ? " deadlock)" : " deadlocks)"));
        }
        
        return(deadlocks);
    }
    
    /**
     * dump the monitor information
     * @param catMonitors
     * @param catMonitorsLocks
     * @param mmap
     * @return
     */
    private int[] dumpMonitors(DefaultMutableTreeNode catMonitors, DefaultMutableTreeNode catMonitorsLocks, MonitorMap mmap) {
        Iterator iter = mmap.iterOfKeys();
        int monitorsWithoutLocksCount = 0;
        int overallThreadsWaiting = 0;
        while(iter.hasNext()) {
            String monitor = (String) iter.next();
            Map[] threads = mmap.getFromMonitorMap(monitor);
            ThreadInfo mi = new ThreadInfo(monitor, null, "", 0);
            
            DefaultMutableTreeNode monitorNode = new DefaultMutableTreeNode(mi);
            
            // first the locks
            Iterator iterLocks = threads[0].keySet().iterator();
            int locks = 0;
            int sleeps = 0;
            int waits = 0;
            while(iterLocks.hasNext()) {
                String thread = (String) iterLocks.next();
                if(threads[2].containsKey(thread)) {
                    createNode(monitorNode, "locks and sleeps on monitor: " + thread, null, (String) threads[0].get(thread), 0);
                    sleeps++;
                } else if(threads[1].containsKey(thread)) {
                    createNode(monitorNode, "locks and waits on monitor: " + thread, null, (String) threads[0].get(thread), 0);
                    sleeps++;
                } else {
                    createNode(monitorNode, "locked by " + thread, null, (String) threads[0].get(thread), 0);
                }
                locks++;
            }
            
            Iterator iterWaits = threads[1].keySet().iterator();
            while(iterWaits.hasNext()) {
                String thread = (String) iterWaits.next();
                if(!threads[0].containsKey(thread)) {
                    createNode(monitorNode, "waits on monitor: " + thread, null, (String) threads[1].get(thread), 0);
                    waits++;
                }
            }
            
            
            mi.setContent(ThreadDumpInfo.getMonitorInfo(locks, waits, sleeps));
            mi.setName(mi.getName() + ":    " + (sleeps) + " Thread(s) sleeping, " + (waits) + " Thread(s) waiting, " + (locks) + " Thread(s) locking");
                        
            ((Category)catMonitors.getUserObject()).addToCatNodes(monitorNode);
            if(locks == 0) {
                monitorsWithoutLocksCount++;
                overallThreadsWaiting+=waits;
                ((Category)catMonitorsLocks.getUserObject()).addToCatNodes(monitorNode);
            }
        }
        return new int[]{monitorsWithoutLocksCount, overallThreadsWaiting};
    }
    
        
    /**
     * parses a loggc file stream and reads any found class histograms and adds the to the dump store
     * @param loggcFileStream the stream to read
     * @param root the root node of the dumps.
     */
    public void parseLoggcFile(InputStream loggcFileStream, DefaultMutableTreeNode root) {
        BufferedReader bis = new BufferedReader(new InputStreamReader(loggcFileStream));
        Vector histograms = new Vector();
        
        try {
            while(bis.ready()) {
                bis.mark(getMarkSize());
                String nextLine = bis.readLine();
                if(nextLine.startsWith("num   #instances    #bytes  class name")) {
                    bis.reset();
                    histograms.add(parseNextClassHistogram(bis));
                }
            }
            
            // now add the found histograms to the tree.
            for(int i = histograms.size()-1; i >= 0; i--) {
                DefaultMutableTreeNode dump = getNextDumpForHistogram(root);
                if(dump != null) {
                    addHistogramToDump(dump, (HistogramTableModel) histograms.get(i));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * check if the passed logline contains the beginning of a sun jdk thread
     * dump.
     * @param logLine the line of the logfile to test
     * @return true, if the start of a sun thread dump is detected.
     */
    public static boolean checkForSupportedThreadDump(String logLine) {
        return (logLine.trim().indexOf("Full thread dump Java HotSpot(TM)") >= 0);
    }
}
