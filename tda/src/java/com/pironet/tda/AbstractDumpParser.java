/*
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
 * $Id: AbstractDumpParser.java,v 1.11 2008-01-08 19:37:30 irockel Exp $
 */
package com.pironet.tda;

import com.pironet.tda.utils.IconFactory;
import com.pironet.tda.utils.PrefManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * abstract dump parser class, contains all generic dump parser
 * stuff, which doesn't have any jdk specific parsing code.
 * 
 * All Dump Parser should extend from this class as it already provides
 * a basic parsing interface.
 * 
 * @author irockel
 */
public abstract class AbstractDumpParser implements DumpParser {
    private BufferedReader bis = null;
    
    private int markSize = 16384;
    private int maxCheckLines = 10;
    private boolean millisTimeStamp = false;
    private Pattern regexPattern = null;
    private boolean patternError = false;


    protected AbstractDumpParser(BufferedReader bis) {
        maxCheckLines = PrefManager.get().getMaxRows();
        markSize = PrefManager.get().getStreamResetBuffer();   
        millisTimeStamp = PrefManager.get().getMillisTimeStamp();
        setBis(bis);
        
        // set date parsing pattern.
        if((PrefManager.get().getDateParsingRegex() != null) && !PrefManager.get().getDateParsingRegex().trim().equals("")) {
            try {
                regexPattern = Pattern.compile(PrefManager.get().getDateParsingRegex().trim());
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
     * strip the dump string from a given path
     * @param path the treepath to check
     * @return dump string, if proper tree path, null otherwise.
     */
    protected String getDumpStringFromTreePath(TreePath path) {
        String[] elems = path.toString().split(",");
        if (elems.length > 1) {
            return (elems[elems.length-1].substring(0, elems[elems.length-1].lastIndexOf(']')).trim());
        } else {
            return null;
        }
    }
    
    /**
     * find long running threads.
     * @param root the root node to use for the result.
     * @param dumpStore the dump store to use
     * @param paths paths to the dumps to check
     * @param minOccurence the min occurrence of a long running thread
     * @param regex regex to be applied to the thread titles.
     */
    public void findLongRunningThreads(DefaultMutableTreeNode root, Map dumpStore, TreePath[] paths, int minOccurence, String regex) {
        diffDumps("Long running thread detection", root, dumpStore, paths, minOccurence, regex);
    }
    
    /**
     * merge the given dumps.
     * @param root the root node to use for the result.
     * @param dumpStore the dump store tu use
     * @param dumps paths to the dumps to check
     * @param minOccurence the min occurrence of a long running thread
     * @param regex regex to be applied to the thread titles.
     */
    public void mergeDumps(DefaultMutableTreeNode root, Map dumpStore, TreePath[] dumps, int minOccurence, String regex) {
        diffDumps("Merge", root, dumpStore, dumps, minOccurence, regex);
    }
    
    protected void diffDumps(String prefix, DefaultMutableTreeNode root, Map dumpStore, TreePath[] dumps, int minOccurence, String regex) {
        Vector keys = new Vector(dumps.length);        
        
        for(int i = 0; i < dumps.length; i++) {
            String dumpName = getDumpStringFromTreePath(dumps[i]);
            if(dumpName.indexOf(" at") > 0) {
                dumpName = dumpName.substring(0, dumpName.indexOf(" at"));
            } else if(dumpName.indexOf(" around") > 0) {
                dumpName = dumpName.substring(0, dumpName.indexOf(" around"));
            }
            keys.add(dumpName);
        }
           
        String info = prefix + " between " + keys.get(0) + " and " + keys.get(keys.size()-1); 
        DefaultMutableTreeNode catMerge = new DefaultMutableTreeNode(new TableCategory(info, IconFactory.DIFF_DUMPS));
        root.add(catMerge);
        int threadCount = 0;
        
        if(dumpStore.get(keys.get(0)) != null) {
            Iterator dumpIter = ((Map) dumpStore.get(keys.get(0))).keySet().iterator();
            
            while(dumpIter.hasNext()) {
                String threadKey = ((String) dumpIter.next()).trim();
                int occurence = 0;
                
                if(regex == null || regex.equals("") || threadKey.matches(regex)) {
                    for(int i = 1; i < dumps.length; i++) {
                        Map threads = (Map) dumpStore.get(keys.get(i));
                        if(threads.containsKey(threadKey)) {
                            occurence++;
                        }
                    }
                
                    if(occurence >= (minOccurence-1)) {
                        threadCount++;
                        StringBuffer content = new StringBuffer("<body bgcolor=\"ffffff\"><b><font size=").append(TDA.getFontSizeModifier(-1)).
                                append(">").append((String) keys.get(0)).append("</b></font><hr/><pre><font size=").
                                append(TDA.getFontSizeModifier(-1)).append(">").
                                append(fixMonitorLinks((String) ((Map) dumpStore.get(keys.get(0))).get(threadKey), (String) keys.get(0)));
                        for(int i = 1; i < dumps.length; i++) {
                            if(((Map)dumpStore.get(keys.get(i))).containsKey(threadKey)) {
                                content.append("\n\n</pre><b><font size=");
                                content.append(TDA.getFontSizeModifier(-1));
                                content.append(">");
                                content.append(keys.get(i));
                                content.append("</font></b><hr/><pre><font size=");
                                content.append(TDA.getFontSizeModifier(-1));
                                content.append(">");
                                content.append(fixMonitorLinks((String) ((Map)dumpStore.get(keys.get(i))).get(threadKey), (String) keys.get(i)));
                            }
                        }
                        addToCategory(catMerge, threadKey, null, content, 0);
                    }
                }
            }
        }
        
        StringBuffer statData = new StringBuffer("<body bgcolor=\"#ffffff\"><font face=System " +
                "><b><font face=System> " + 
                ((Category) catMerge.getUserObject()).getName() + " </b></td></tr><hr/><br/>" +
                "<table border=0><tr bgcolor=\"#dddddd\"><td><font face=System " +
                ">Overall Thread Count</td><td width=\"150\"></td><td><b><font face=System>");
        statData.append(threadCount);
        statData.append("</b></td></tr>");
        
        if(threadCount == 0) {
            statData.append("<tr bgcolor=\"#ffffff\"<td></td></tr>");
            statData.append("<tr bgcolor=\"#cccccc\"><td colspan=2><font face=System " +
                    "><p>No threads were found which occured at least " + minOccurence + " times.<br>" +
                    "You should check your dumps for long running threads " +
                    "or adjust the minimum occurence.</p>");
        }
                
        statData.append("</table>");
        
        ((Category) catMerge.getUserObject()).setInfo(statData.toString());
        
    }
    
    /**
     * fix the monitor links for proper navigation to the monitor in the right dump.
     * @param fixString the string to fix
     * @param dumpName the dump name to reference
     * @return the fixed string.
     */
    private String fixMonitorLinks(String fixString, String dumpName) {
        if(fixString.indexOf("monitor://") > 0) {
           fixString = fixString.replaceAll("monitor://", "monitor:/" + dumpName + "/");
        }
        return(fixString);
    }
    
    /**
     * create a tree node with the provided information
     * @param top the parent node the new node should be added to.
     * @param title the title of the new node
     * @param info the info part of the new node
     * @param content the content part of the new node
     * @see ThreadInfo 
     */
    protected void createNode(DefaultMutableTreeNode top, String title, String info, String content, int lineCount) {
        DefaultMutableTreeNode threadInfo = null;
        threadInfo = new DefaultMutableTreeNode(new ThreadInfo(title, info, content, lineCount, getThreadTokens(title)));
        top.add(threadInfo);
    }
    
    /**
     * create a category entry for a category (categories are "Monitors", "Threads waiting", e.g.). A ThreadInfo
     * instance will be created with the passed information.
     * FIXME: this method needs rework for creating a JXTable for the categories, except monitors
     * @param category the category the node should be added to.
     * @param title the title of the new node
     * @param info the info part of the new node
     * @param content the content part of the new node
     * @param lineCount the line count of the thread stack, 0 if not applicable for this element.
     * @see ThreadInfo 
     */
    protected void addToCategory(DefaultMutableTreeNode category, String title, StringBuffer info, StringBuffer content, int lineCount) {
        DefaultMutableTreeNode threadInfo = null;
        threadInfo = new DefaultMutableTreeNode(new ThreadInfo(title, info != null ? info.toString() : null, content.toString(), lineCount, 
                getThreadTokens(title)));
        ((Category)category.getUserObject()).addToCatNodes(threadInfo);
    }

    /**
     * get the stream to parse
     * @return stream or null if none is set up
     */
    protected BufferedReader getBis() {
        return bis;
    }

    /**
     * parse the thread tokens for table display.
     * @param title
     */
    protected abstract String[] getThreadTokens(String title);

    /**
     * set the stream to parse
     * @param bis the stream
     */
    protected void setBis(BufferedReader bis) {
        this.bis = bis;
    }
    
    /**
     * this counter counts backwards for adding class histograms to the thread dumpss
     * beginning with the last dump.
     */
    private int dumpHistogramCounter = -1;
    
    /**
     * set the dump histogram counter to the specified value to force to start (bottom to top)
     * from the specified thread dump.
     */
    public void setDumpHistogramCounter(int value) {
        dumpHistogramCounter = value;
    }
    
        
    /**
     * retrieve the next node for adding histogram information into the tree.
     * @param root the root to use for search.
     * @return node to use for append.
     */
    protected DefaultMutableTreeNode getNextDumpForHistogram(DefaultMutableTreeNode root) {
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
     * close this dump parser, also closes the passed dump stream
     */
    public void close() throws IOException {
        if(getBis() != null) {
            getBis().close();
        }        
    }

    /**
     * get the maximum size for the mark buffer while reading 
     * the log file stream.
     * @return size, default is 16KB.
     */
    protected int getMarkSize() {
        return markSize;
    }

    /**
     * set the maximum mark size.
     * @param markSize the size to use, default is 16KB.
     */
    protected void setMarkSize(int markSize) {
        this.markSize = markSize;
    }

    /**
     * specifies the maximum amounts of lines to check if the dump is followed
     * by a class histogram or a deadlock.
     * @return the amount of lines to check, defaults to 10.
     */
    protected int getMaxCheckLines() {
        return maxCheckLines;
    }

    public void setMaxCheckLines(int maxCheckLines) {
        this.maxCheckLines = maxCheckLines;
    }

    /**
     * @return true, if the time stamp is in milliseconds.
     */
    public boolean isMillisTimeStamp() {
        return millisTimeStamp;
    }

    public void setMillisTimeStamp(boolean millisTimeStamp) {
        this.millisTimeStamp = millisTimeStamp;
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(Pattern regexPattern) {
        this.regexPattern = regexPattern;
    }

    public boolean isPatternError() {
        return patternError;
    }

    public void setPatternError(boolean patternError) {
        this.patternError = patternError;
    }

}
