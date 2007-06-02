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
 * $Id: JDK14Parser.java,v 1.46 2007-06-02 09:45:27 irockel Exp $
 */

package com.pironet.tda;

import com.pironet.tda.utils.HistogramTableModel;
import com.pironet.tda.utils.PrefManager;
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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JOptionPane;
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
    private int markSize = 16384;
    private int maxCheckLines = 10;
    
    private InputStream dumpFileStream = null;
    private MutableTreeNode nextDump = null;
    private BufferedReader bis = null;
    private Map threadStore = null;
    private Pattern regexPattern = null;
    private boolean millisTimeStamp = false;
    
    private int counter = 1;
    
    private int lineCounter = 0;
    
    private boolean patternError = false;
    
    private boolean foundClassHistograms = false;
    
    /** 
     * Creates a new instance of JDK14Parser 
     */
    public JDK14Parser(InputStream dumpFileStream, Map threadStore) {
        this.dumpFileStream = dumpFileStream;
        this.threadStore = threadStore;
        this.regexPattern = regexPattern;
        this.millisTimeStamp = millisTimeStamp;
        maxCheckLines = PrefManager.get().getMaxRows();
        markSize = PrefManager.get().getStreamResetBuffer();
        millisTimeStamp = PrefManager.get().getMillisTimeStamp();
        
        if((PrefManager.get().getDateParsingRegex() != null) && !PrefManager.get().getDateParsingRegex().trim().equals("")) {
            try {
                regexPattern = Pattern.compile(PrefManager.get().getDateParsingRegex());
                patternError = false;
            } catch (PatternSyntaxException pe) {
                JOptionPane.showMessageDialog(null,
                        "Error during parsing line for timestamp regular expression!\n" +
                        "Please check regular expression in your preferences. Deactivating\n" +
                        "parsing for the rest of the file! Error Message is " + pe.getMessage() + " \n",
                        "Error during Parsing", JOptionPane.ERROR_MESSAGE);
                
                //System.out.println("Failed parsing! " + pe.getMessage());
                //pe.printStackTrace();
                patternError = true;
            }
        }        
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
        ThreadInfo overallTDI = null;
        DefaultMutableTreeNode catMonitors = null;
        DefaultMutableTreeNode catMonitorsLocks = null;
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
            overallTDI = new ThreadInfo("Full Thread Dump No. " + counter++, null, "");
            threadDump = new DefaultMutableTreeNode(overallTDI);
            
            catThreads = new DefaultMutableTreeNode(new Category("Threads", TDA.createImageIcon("Threads.gif")));
            threadDump.add(catThreads);
            
            catWaiting = new DefaultMutableTreeNode(new Category("Threads waiting for Monitors", TDA.createImageIcon("ThreadsWaiting.gif")));
            
            catSleeping = new DefaultMutableTreeNode(new Category("Threads sleeping on Monitors", TDA.createImageIcon("ThreadsSleeping.gif")));

            catLocking = new DefaultMutableTreeNode(new Category("Threads locking Monitors", TDA.createImageIcon("ThreadsLocking.gif")));
            
            // create category for monitors with disabled filtering.
            catMonitors = new DefaultMutableTreeNode(new Category("Monitors", TDA.createImageIcon("Monitors.gif"), false));
            catMonitorsLocks = new DefaultMutableTreeNode(new Category("Monitors without locking thread", TDA.createImageIcon("Monitors-nolocks.gif"), false));
            
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
            Matcher matched = null;
            
            while(bis.ready() && !finished) {
                String line = bis.readLine();
                lineCounter++;
                if(locked) {
                    if(line.indexOf("Full thread dump") >= 0) {
                        locked = false;
                        overallTDI.threadName += " at line " + lineCounter;
                        if(startTime != 0) {
                            startTime = 0;
                        } else if(matched != null && matched.matches()) {
                            
                            String parsedStartTime = matched.group(1);
                            if(millisTimeStamp) {
                                try {
                                    // the factor is a hack for a bug in oc4j timestamp printing (pattern timeStamp=2342342340)
                                    if(parsedStartTime.length() < 13) {
                                        startTime = Long.parseLong(parsedStartTime) * (long)Math.pow(10, 13-parsedStartTime.length());
                                    } else {
                                        startTime = Long.parseLong(parsedStartTime);
                                    }
                                } catch (NumberFormatException nfe) {
                                    startTime = 0;
                                }
                                overallTDI.threadName += " around " + new Date(startTime);
                            } else {
                                overallTDI.threadName += " around " + parsedStartTime;
                            }
                            parsedStartTime = null;
                        }
                        dumpKey = overallTDI.threadName;
                    } else if(!patternError && (regexPattern != null)) {
                        try {
                            Matcher m = regexPattern.matcher(line.trim());
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
                            patternError = true;
                        }
                    }
                } else {
                    if(line.startsWith("\"")) {
                        if(title != null) {
                            threads.put(title, content.toString());
                            content.append("</pre></pre>");
                            createCategoryNode(catThreads, title, null, content);
                            threadCount++;
                        }
                        if(wContent != null) {
                            wContent.append("</b><hr>");
                            createCategoryNode(catWaiting, title, wContent, content);
                            wContent = null;
                            waiting++;
                        }
                        if(sContent != null) {
                            sContent.append("</b><hr>");
                            createCategoryNode(catSleeping, title, sContent, content);
                            sContent = null;
                            sleeping++;
                        }
                        if(lContent != null) {
                            lContent.append("</b><hr>");
                            createCategoryNode(catLocking, title, lContent, content);
                            lContent = null;
                            locking++;
                        }
                        while(!monitorStack.empty()) {
                            mmap.parseAndAddThread((String)monitorStack.pop(), title, content.toString());
                        }
                        
                        title = line.trim();
                        content = new StringBuffer("<body bgcolor=\"ffffff\"><pre><font size=" + TDA.getFontSizeModifier(-1) + ">");
                        content.append(line);
                        content.append("\n");
                    } else if (line.trim().startsWith("at ")) {
                        content.append(line);
                        content.append("\n");
                    } else if (line.trim().startsWith("java.lang.Thread.State")) {
                        content.append(line);
                        content.append("\n");
                    } else if (line.trim().startsWith("Locked ownable synchronizers:")) {
                        content.append(line);
                        content.append("\n");
                    } else if (line.trim().startsWith("- waiting on")) {
                        String newLine = linkifyMonitor(line);
                        content.append(newLine);
                        if(sContent == null) {
                            sContent = new StringBuffer("<body bgcolor=\"ffffff\"><font size=" + TDA.getFontSizeModifier(-1) + "><b>");
                        }
                        sContent.append(newLine.trim());
                        monitorStack.push(line);
                        sContent.append("\n");
                        content.append("\n");
                    } else if (line.trim().startsWith("- waiting to")) {
                        String newLine = linkifyMonitor(line);
                        content.append(newLine);
                        if(wContent == null) {
                            wContent = new StringBuffer("<body bgcolor=\"ffffff\"><font size=" + TDA.getFontSizeModifier(-1) + "><b>");
                        }
                        wContent.append(newLine.trim());
                        monitorStack.push(line);
                        wContent.append("\n");
                        content.append("\n");
                    } else if (line.trim().startsWith("- locked")) {
                        String newLine = linkifyMonitor(line);
                        content.append(newLine);
                        if(lContent == null) {
                            lContent = new StringBuffer("<body bgcolor=\"ffffff\"><font size=" + TDA.getFontSizeModifier(-1) + "><b>");
                        }
                        lContent.append(newLine.trim());
                        monitorStack.push(line);
                        lContent.append("\n");
                        content.append("\n");
                    } else if (line.trim().startsWith("- ")) {
                        content.append(line);
                        content.append("\n");
                    }
                    
                    // last thread reached?
                    if(line.startsWith("\"Suspend Checker Thread\"") ||
                       line.startsWith("\"VM Periodic Task Thread\"")) {
                        finished = true;
                        bis.mark(markSize);
                        if((deadlocks = checkForDeadlocks(threadDump)) == 0) {
                            // no deadlocks found, set back original position.
                            bis.reset();
                        }
                        
                        bis.mark(markSize);
                        if(!(foundClassHistograms = checkForClassHistogram(threadDump))) {
                            bis.reset();
                        }
                    }
                }
            }
            // last thread
            if(title != null) {
                threads.put(title, content.toString());
                content.append("</pre></pre>");
                createCategoryNode(catThreads, title, null, content);
                threadCount++;
            }
            if(wContent != null) {
                wContent.append("</b><hr>");
                createCategoryNode(catWaiting, title, null, wContent);
                wContent = null;
                waiting++;
            }
            if(sContent != null) {
                sContent.append("</b><hr>");
                createCategoryNode(catSleeping, title, sContent, content);
                sContent = null;
                sleeping++;
            }
            if(lContent != null) {
                lContent.append("</b><hr>");
                createCategoryNode(catLocking, title, null, lContent);
                lContent = null;
                locking++;
            }
            
            int monitorCount = mmap.size();
            StringBuffer statData = new StringBuffer("<body bgcolor=\"#ffffff\"><font face=System size=" +
                    TDA.getFontSizeModifier(-1) + "><table border=0><tr bgcolor=\"#dddddd\"><td><font face=System size="+ TDA.getFontSizeModifier(-1) + 
                    ">Overall Thread Count</td><td width=\"150\"></td><td><b><font face=System size=" + TDA.getFontSizeModifier(-1) + ">");
            statData.append(threadCount);
            statData.append("</b></td></tr>\n\n<tr bgcolor=\"#eeeeee\"><td><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                    ">Overall Monitor Count</td><td></td><td><b><font face=System size=" + TDA.getFontSizeModifier(-1) + ">");
            statData.append(monitorCount);
            statData.append("</b></td></tr>\n\n<tr bgcolor=\"#dddddd\"><td><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                    ">Number of threads waiting for a monitor</td><td></td><td><b><font face=System size=" + TDA.getFontSizeModifier(-1) + ">");
            statData.append(waiting);
            statData.append("</b></td></tr>\n\n<tr bgcolor=\"#eeeeee\"><td><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                    ">Number of threads locking a monitor</td><td></td><td><b><font face=System size="+ TDA.getFontSizeModifier(-1) + ">");
            statData.append(locking);
            statData.append("</b></td></tr>\n\n<tr bgcolor=\"#dddddd\"><td><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                    ">Number of threads sleeping on a monitor</td><td></td><td><b><font face=System size=" + TDA.getFontSizeModifier(-1) + ">");
            statData.append(sleeping);
            statData.append("</b></td></tr>\n\n<tr bgcolor=\"#eeeeee\"><td><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                    ">Number of deadlocks</td><td></td><td><b><font face=System size=" + TDA.getFontSizeModifier(-1) + ">");
            statData.append(deadlocks);
            
            ((Category) catThreads.getUserObject()).setName(((Category) catThreads.getUserObject()) + " (" + threadCount + " Threads overall)");
            ((Category) catWaiting.getUserObject()).setName(((Category) catWaiting.getUserObject()) + " (" + waiting + " Threads waiting)");
            ((Category) catSleeping.getUserObject()).setName(((Category) catSleeping.getUserObject()) + " (" + sleeping + " Threads sleeping)");
            ((Category) catLocking.getUserObject()).setName(((Category) catLocking.getUserObject()) + " (" + locking + " Threads locking)");
            ((Category) catMonitors.getUserObject()).setName(((Category) catMonitors.getUserObject()) + " (" + monitorCount + " Monitors)");
            
            
            int monitorsWithoutLocksCount = 0;
            int overallThreadsWaitingWithoutLocks = 0; 
            // dump monitors 
            if(mmap.size() > 0) {
                int[] result = dumpMonitors(catMonitors, catMonitorsLocks, mmap);
                monitorsWithoutLocksCount = result[0];
                overallThreadsWaitingWithoutLocks = result[1];
            }
            
            // display nodes with stuff to display
            if(waiting > 0) {
                threadDump.add(catWaiting);
            }
            
            if(sleeping > 0) {
                threadDump.add(catSleeping);
            }

            if(locking > 0) {
                threadDump.add(catLocking);
            }
            
            if(monitorCount > 0) {
                threadDump.add(catMonitors);
            }
            
            if(monitorsWithoutLocksCount > 0) {
                threadDump.add(catMonitorsLocks);
            }
            
            statData.append("</b></td></tr>\n\n<tr bgcolor=\"#dddddd\"><td><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                    ">Number of Monitors without locking threads</td><td></td><td><b><font face=System size=" + TDA.getFontSizeModifier(-1) + ">");
            statData.append(monitorsWithoutLocksCount);
            statData.append("</b></td></tr>");
            ((Category) catMonitorsLocks.getUserObject()).setName(((Category) catMonitorsLocks.getUserObject()) + " (" + monitorsWithoutLocksCount + 
                    " Monitors)");
            // add thread dump to passed dump store.
            if((threadCount > 0) && (dumpKey != null)) {
                threadStore.put(dumpKey.trim(), threads);
            }
            
            // check for possible hot spots concerning this thread dump
            
            // check if a lot of threads are in state "waiting"
            if((threadCount > 0) && ((waiting / (threadCount / 100.0)) > 10.0)) {
                statData.append("<tr bgcolor=\"#ffffff\"<td></td></tr>");
                statData.append("<tr bgcolor=\"#cccccc\"><td colspan=2><font face=System size=" + TDA.getFontSizeModifier(-1) +
                        "><p>More than 10% of all threads are waiting for a monitor to become available again.</p><br>");
                statData.append("This might indicate a congestion or even a deadlock. If a monitor doesn't have a locking thread, it might be<br>");
                statData.append("hold by some external resource or system thread. You should check the waiting threads.<br></td></tr>");
            }
            
            // display an info if there are monitors without locking threads
            if(monitorsWithoutLocksCount > 0) {
                statData.append("<tr bgcolor=\"#ffffff\"<td></td></tr>");
                statData.append("<tr bgcolor=\"#cccccc\"><td colspan=2><font face=System size=" + TDA.getFontSizeModifier(-1) +
                        "><p>This thread dump contains monitors without a locking thread information.<br>");
                statData.append("This means, the monitor is hold by a system thread or some external resource.</p<br>");
                statData.append("You should check the monitors without locking threads for more information.<br></td></tr>");
            }
            
            // check for indications for running garbage collector
            if((threadCount > 0) && (overallThreadsWaitingWithoutLocks / (threadCount / 100.0) > 50.0)) {
                statData.append("<tr bgcolor=\"#ffffff\"<td></td></tr>");
                statData.append("<tr bgcolor=\"#cccccc\"><td colspan=2><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                        "<p>More than 50% of all threads are waiting for a monitor without a application");
                statData.append("thread holding it.<br> This indicates a congestion. It is very likely the garbage collector is running");
                statData.append("and is blocking the monitors.</p<br>");
                statData.append("You should check the monitors without locking threads for more information on the blocked threads.<br>");
                statData.append("You also should analyze the garbage collector behaviour. Go to the ");
                statData.append("<a href=\"http://www.tagtraum.com/gcviewer.html\">GCViewer-Homepage</a> for more<br>");
                statData.append(" information on how to do this.</td></tr>");
            }
            statData.append("</table>");
            
            overallTDI.content = statData.toString();
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
        String begin = line.substring(0, line.indexOf('<'));
        String monitor = line.substring(line.indexOf('<'),line.indexOf('>')+1);
        String end = line.substring(line.indexOf('>')+1);
        monitor = monitor.replaceAll("<", "<a href=\"monitor://"+ monitor + "\">&lt;");
        monitor = monitor.substring(0, monitor.length()-1) + "&gt;</a>";
        //System.out.println("link="+ begin + monitor + end);
        return(begin + monitor + end);
    }
    
    /**
     * checks for the next class histogram and adds it to the tree node passed
     * @param threadDump which tree node to add the histogram.
     */
    private boolean checkForClassHistogram(DefaultMutableTreeNode threadDump) throws IOException {
        HistogramTableModel classHistogram = parseNextClassHistogram(bis);
        
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
        int lineCounter = 0;
        
        while(bis.ready() && !finished) {
            String line = bis.readLine();
            if(!found && !line.trim().equals("")) {
                if (line.startsWith("num   #instances    #bytes  class name")) {
                    found = true;
                } else if(lineCounter >= maxCheckLines) {
                    finished = true;
                } else {
                    lineCounter++;
                }
            } else if(found) {
                if(line.startsWith("Total ")) {
                    String newLine = line.trim().replaceAll("(\\s)+", ";");
                    String[] elems = newLine.split(";");
                    classHistogram.setBytes(Long.parseLong(elems[2]));
                    classHistogram.setInstances(Long.parseLong(elems[1]));
                    finished = true;
                } else if(!line.startsWith("-------")) {
                    String newLine = line.trim().replaceAll("(\\s)+", ";");
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
        DefaultMutableTreeNode catDeadlocks = new DefaultMutableTreeNode(new Category("Deadlocks", TDA.createImageIcon("Deadlock.gif")));
        
        while(bis.ready() && !finished) {            
            String line = bis.readLine();
            if(!found && !line.trim().equals("")) {
                if (line.startsWith("Found one Java-level deadlock")) {
                    found = true;
                    dContent.append("<body bgcolor=\"ffffff\"><pre>");
                    dContent.append(line);
                    dContent.append("\n");
                } else if(lineCounter < maxCheckLines) {
                    finished = true;
                } else {
                    lineCounter++;
                }
            } else if(found) {                
                if(line.startsWith("Found one Java-level deadlock")) {
                    if(dContent.length() > 0) {
                        deadlocks++;
                        createCategoryNode(catDeadlocks, "Deadlock No. " + (deadlocks), null, dContent);
                    }
                    dContent = new StringBuffer();
                } else if(line.startsWith("Found") && (line.trim().endsWith("deadlocks.") || line.trim().endsWith("deadlock."))) {
                    finished = true;
                } else {
                    dContent.append(line);
                    dContent.append("\n");
                }
            }
        }
        if(dContent.length() > 0) {
            deadlocks++;
            createCategoryNode(catDeadlocks, "Deadlock No. " + (deadlocks), null, dContent);
        }
        
        if(deadlocks > 0) {
            threadDump.add(catDeadlocks);
        }
        
        return(deadlocks);
    }
    
    private int[] dumpMonitors(DefaultMutableTreeNode catMonitors, DefaultMutableTreeNode catMonitorsLocks, MonitorMap mmap) {
        Iterator iter = mmap.iterOfKeys();
        int monitorsWithoutLocksCount = 0;
        int overallThreadsWaiting = 0;
        while(iter.hasNext()) {
            String monitor = (String) iter.next();
            Set[] threads = mmap.getFromMonitorMap(monitor);
            ThreadInfo mi = new ThreadInfo(monitor, null, "");
            
            DefaultMutableTreeNode monitorNode = new DefaultMutableTreeNode(mi);
            
            // first the locks
            Iterator iterLocks = threads[0].iterator();
            int locks = 0;
            while(iterLocks.hasNext()) {
                String[] thread = (String[]) iterLocks.next();
                createNode(monitorNode, "locked by " + thread[0], null, thread[1]);
                locks++;
            }
            
            // now the sleeps
            Iterator iterSleeps = threads[2].iterator();
            int sleeps = 0;
            while(iterSleeps.hasNext()) {
                String[] thread = (String[]) iterSleeps.next();
                createNode(monitorNode, "sleeps on lock " + thread[0], null, thread[1]);
                sleeps++;
            }
            
            // now the waits
            Iterator iterWaits = threads[1].iterator();
            int waits = 0;
            while(iterWaits.hasNext()) {
                String[] thread = (String[]) iterWaits.next();
                createNode(monitorNode, "waiting " + thread[0], null, thread[1]);
                waits++;
            }
            StringBuffer statData = new StringBuffer ("<body bgcolor=\"ffffff\"><table border=0 bgcolor=\"#dddddd\"><tr><td><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                    ">Threads locking monitor</td><td><b><font face=System size=" + TDA.getFontSizeModifier(-1) + ">");
            statData.append(locks);
            statData.append("</b></td></tr>\n\n<tr bgcolor=\"#eeeeee\"><td>");
            statData.append("<font face=System size=" + TDA.getFontSizeModifier(-1) + ">Threads sleeping on monitor</td><td><b><font face=System size=" + 
                    TDA.getFontSizeModifier(-1) +">");
            statData.append(sleeps);
            statData.append("</b></td></tr>\n\n<tr><td>");
            statData.append("<font face=System size=" + TDA.getFontSizeModifier(-1) + ">Threads waiting to lock monitor</td><td><b><font face=System size=" + 
                    TDA.getFontSizeModifier(-1) + ">");
            statData.append(waits);
            statData.append("</b></td></tr>\n\n");
            if(locks == 0) {
                statData.append("<tr bgcolor=\"#ffffff\"<td></td></tr>");
                statData.append("<tr bgcolor=\"#cccccc\"><td><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                        "<p>This monitor doesn't have a thread locking it. This means a VM Thread is holding it.</p><br>");
                statData.append("If you see many monitors having no locking thread, this usually means, the garbage collector is running.<br>");
                statData.append("In this case you should consider analyzing the Garbage Collector output. If the dump has many monitors with no locking thread<br>");
                statData.append("a click on the <a href=\"dump://\">dump node</a> will give you additional information.<br></td></tr>");
            }
            if(waits > 5) {
                statData.append("<tr bgcolor=\"#ffffff\"<td></td></tr>");
                statData.append("<tr bgcolor=\"#cccccc\"><td><font face=System size=" + TDA.getFontSizeModifier(-1) + 
                        "<p>A lot of threads are waiting for this monitor to become available again.</p><br>");
                statData.append("This might indicate a congestion. You also should analyze other locks blocked by threads waiting<br>");
                statData.append("for this monitor as there might be much more threads waiting for it.<br></td></tr>");                
            }
            statData.append("</table>");
            mi.content = statData.toString();
                        
            ((Category)catMonitors.getUserObject()).addToCatTree(monitorNode);
            if(locks == 0) {
                monitorsWithoutLocksCount++;
                overallThreadsWaiting+=waits;
                ((Category)catMonitorsLocks.getUserObject()).addToCatTree(monitorNode);
            }
        }
        return new int[]{monitorsWithoutLocksCount, overallThreadsWaiting};
    }
    
    /**
     * create a tree node with the provided information
     * @param top the parent node the new node should be added to.
     * @param title the title of the new node
     * @param info the info part of the new node
     * @param content the content part of the new node
     * @see ThreadInfo 
     */
    private void createNode(DefaultMutableTreeNode top, String title, String info, String content) {
        DefaultMutableTreeNode threadInfo = null;
        threadInfo = new DefaultMutableTreeNode(new ThreadInfo(title, info, content));
        top.add(threadInfo);
    }
    
    /**
     * create a node for a category (categories are "Monitors", "Threads waiting", e.g.). A ThreadInfo
     * instance will be created with the passed information.
     * @param category the category the node should be added to.
     * @param title the title of the new node
     * @param info the info part of the new node
     * @param content the content part of the new node
     * @see ThreadInfo 
     */
    private void createCategoryNode(DefaultMutableTreeNode category, String title, StringBuffer info, StringBuffer content) {
        DefaultMutableTreeNode threadInfo = null;
        threadInfo = new DefaultMutableTreeNode(new ThreadInfo(title, info != null ? info.toString() : null, content.toString()));
        ((Category)category.getUserObject()).addToCatTree(threadInfo);
    }
    
    private String getDumpStringFromTreePath(TreePath path) {
        String[] elems = path.toString().split(",");
        if(elems.length > 1) {
            return(elems[1].substring(0, elems[1].lastIndexOf(']')).trim());
        } else {
            return null;
        }
    }
    
    /**
     * parses a loggc file stream and reads any found class histograms and adds the to the dump store
     * @param loggcFileStream the stream to read
     * @param root the root node of the dumps.
     */
    public void parseLoggcFile(InputStream loggcFileStream, DefaultMutableTreeNode root) {
        BufferedReader bis = new BufferedReader(new InputStreamReader(loggcFileStream));
        boolean found = true;
        Vector histograms = new Vector();
        
        try {
            while(bis.ready()) {
                bis.mark(markSize);
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
     * this counter counts backwards for adding class histograms to the thread dumpss
     * beginning with the last dump.
     */
    private int dumpHistogramCounter = -1;
    
    private DefaultMutableTreeNode getNextDumpForHistogram(DefaultMutableTreeNode root) {
        if(dumpHistogramCounter == -1) {
            // -1 as index starts with 0.
            dumpHistogramCounter = root.getChildCount()-1;
        }
        DefaultMutableTreeNode result = null;
        
        if(dumpHistogramCounter > 0) {
            result = (DefaultMutableTreeNode) root.getChildAt(dumpHistogramCounter);
            dumpHistogramCounter--;
        }
        
        return result;
    }
    
    /**
     * set the dump histogram counter to the specified value to force to start (bottom to top)
     * from the specified thread dump.
     */
    public void setDumpHistogramCounter(int value) {
       dumpHistogramCounter = value; 
    }
    
    public void findLongRunningThreads(DefaultMutableTreeNode root, Map dumpStore, TreePath[] paths, int minOccurence, String regex) {
        diffDumps("Long running thread detection", root, dumpStore, paths, minOccurence, regex);
    }
    
    public void mergeDumps(DefaultMutableTreeNode root, Map dumpStore, TreePath[] dumps, int minOccurence, String regex) {
        diffDumps("Merge", root, dumpStore, dumps, minOccurence, regex);
    }
    
    private void diffDumps(String prefix, DefaultMutableTreeNode root, Map dumpStore, TreePath[] dumps, int minOccurence, String regex) {
        Vector keys = new Vector(dumps.length);        
        
        for(int i = 0; i < dumps.length; i++) {
            keys.add(getDumpStringFromTreePath(dumps[i]));
        }
           
        String info = prefix + " between " + keys.get(0) + " and " + keys.get(keys.size()-1); 
        DefaultMutableTreeNode catMerge = new DefaultMutableTreeNode(new Category(info, TDA.createImageIcon("DiffDumps.gif")));
        root.add(catMerge);
        
        if(dumpStore.get(keys.get(0)) != null) {
            Iterator dumpIter = ((Map) dumpStore.get(keys.get(0))).keySet().iterator();
            
            while(dumpIter.hasNext()) {
                String threadKey = ((String) dumpIter.next()).trim();
                int occurence = 0;
                
                if(regex == null || regex.equals("") || threadKey.matches(regex)) {
                    for(int i = 1; i < dumps.length; i++) {
                        Map threads = (Map) dumpStore.get(keys.get(i));
                        System.out.println("threadKey: " + threadKey);
                        if(threads.containsKey(threadKey)) {
                            occurence++;
                        }
                    }
                
                    if(occurence >= (minOccurence-1)) {
                        StringBuffer content = new StringBuffer("<body bgcolor=\"ffffff\"><pre><font size=").append(TDA.getFontSizeModifier(-1)).append(">").append((String) keys.get(0)).append("\n\n").append((String) ((Map) dumpStore.get(keys.get(0))).get(threadKey));
                        for(int i = 1; i < dumps.length; i++) {
                            if(((Map)dumpStore.get(keys.get(i))).containsKey(threadKey)) {
                                content.append("\n\n---------------------------------\n\n");
                                content.append(keys.get(i));
                                content.append("\n\n");
                                content.append((String) ((Map)dumpStore.get(keys.get(i))).get(threadKey));
                            }
                        }
                        createCategoryNode(catMerge, threadKey, null, content);
                    }
                }
            }
        }
        
    }
    
    /**
     * close this dump parser, also closes the passed dump stream
     */
    public void close() throws IOException {
        if(bis != null) {
            bis.close();
        }
        
        if(dumpFileStream != null) {
            dumpFileStream.close();
        }
    }
}
