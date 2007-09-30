/*
 * PrefManager.java
 *
 * Thread Dump Analysis Tool, parses Thread Dump input and displays it as tree
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
 * $Id: PrefManager.java,v 1.20 2007-09-30 10:37:32 irockel Exp $
 */
package com.pironet.tda.utils;

import com.pironet.tda.filter.Filter;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 * Singleton class for accessing system preferences.
 * Window sizes, and positions are stored here and also the last accessed path
 * is stored here.
 *
 * @author irockel
 */
public class PrefManager {
    public static final String PARAM_DELIM = "\u00A7\u00A7\u00A7\u00A7";
    
    public static final String FILTER_SEP = "\u00ac\u00ac\u00ac\u00ac";

    
    private static PrefManager prefManager = null;
    
    private Preferences toolPrefs = null;
    
    /** Creates a new instance of PrefManager */
    private PrefManager() {
        toolPrefs = Preferences.userNodeForPackage(this.getClass());
    }
    
    public static PrefManager get() {
        if(prefManager == null) {
            prefManager = new PrefManager();
        }
        return(prefManager);
    }

    public long getMaxLogfileSize() {
        return(toolPrefs.getInt("maxlogfilesize", 1024));
    }
    
    public int getWindowState() {
        return(toolPrefs.getInt("windowState", -1));
    }
    
    public void setWindowState(int windowState) {
        toolPrefs.putInt("windowState", windowState);
    }
    
    public File getSelectedPath() {
        return(new File(toolPrefs.get("selectedPath", "")));
    }
    
    public void setSelectedPath(File directory) {
        toolPrefs.put("selectedPath", directory.getAbsolutePath());
    }
    
    public Dimension getPreferredSize() {
        return(new Dimension(toolPrefs.getInt("windowWidth", 800),
               toolPrefs.getInt("windowHeight", 600)));
    }
    
    public void setPreferredSize(Dimension size) {
        toolPrefs.putInt("windowHeight", size.height);
        toolPrefs.putInt("windowWidth", size.width);
    }
    
    public Dimension getPreferredSizeFileChooser() {
        return(new Dimension(toolPrefs.getInt("fileChooser.windowWidth", 0),
               toolPrefs.getInt("fileChooser.windowHeight", 0)));
    }
    
    public void setPreferredSizeFileChooser(Dimension size) {
        toolPrefs.putInt("fileChooser.windowHeight", size.height);
        toolPrefs.putInt("fileChooser.windowWidth", size.width);
    }
    
    public void setMaxLogfileSize(int size) {
        toolPrefs.putInt("maxlogfilesize", size);
    }
    
    public Point getWindowPos() {
        Point point = new Point(toolPrefs.getInt("windowPosX", 0),
                toolPrefs.getInt("windowPosY", 0));
        return(point);
    }
    
    public void setWindowPos(int x, int y) {
        toolPrefs.putInt("windowPosX", x);
        toolPrefs.putInt("windowPosY", y);
    }
    
    public int getMaxRows() {
        return(toolPrefs.getInt("maxRowsForChecking", 10));
    }
    
    public void setMaxRows(int rows) {
        toolPrefs.putInt("maxRowsForChecking", rows);
    }
    
    public int getTopDividerPos() {
        return(toolPrefs.getInt("top.dividerPos", 0));
    }
    
    public void setTopDividerPos(int pos) {
        toolPrefs.putInt("top.dividerPos", pos);
    }

    public int getDividerPos() {
        return(toolPrefs.getInt("dividerPos", 0));
    }
    
    public void setDividerPos(int pos) {
        toolPrefs.putInt("dividerPos", pos);
    }

    public int getStreamResetBuffer() {
        return(toolPrefs.getInt("streamResetBuffer", 16384));
    }
    
    public void setStreamResetBuffer(int buffer) {
        toolPrefs.putInt("streamResetBuffer", buffer);
    }
    
    public boolean getForceLoggcLoading() {
        return(toolPrefs.getBoolean("forceLoggcLoading", false));
    }
    
    public void setForceLoggcLoading(boolean force) {
        toolPrefs.putBoolean("forceLoggcLoading", force);
    }
    
    public String getDateParsingRegex() {
        return(toolPrefs.get("dateParsingRegex", "(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d).*"));
    }
    
    public void setDateParsingRegex(String dateRegex) {
        toolPrefs.put("dateParsingRegex", dateRegex);
    }
    
    public String[] getDateParsingRegexs() {
        String elems = toolPrefs.get("dateParsingRegexs", "(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d).*");
        if(elems.equals("")) {
            elems = getDateParsingRegex();
        }
        return(elems.split(PARAM_DELIM));
    }
    
    public void setDateParsingRegexs(ListModel regexs) {
        toolPrefs.put("dateParsingRegexs", regexsToString(regexs));
    }
    
    private String regexsToString(ListModel regexs) {
        StringBuffer elems = new StringBuffer();
        for(int i = 0; i < regexs.getSize(); i++) {
            elems.append(regexs.getElementAt(i));
            if(i+1 < regexs.getSize()) {
                elems.append(PARAM_DELIM);
            }
        }
        return(elems.toString());
    }
    
    public void addToRecentFiles(String file) {
        String[] currentFiles = getRecentFiles();
        
        // only add files already in it
        if(!hasInRecentFiles(file, currentFiles)) {
            int start = currentFiles.length == 10 ? 1 : 0;
            StringBuffer recentFiles = new StringBuffer();
            
            for(int i = start; i < currentFiles.length; i++) {
                recentFiles.append(currentFiles[i]);
                recentFiles.append(PARAM_DELIM);
            }
            
            // append new files
            recentFiles.append(file);
            toolPrefs.put("recentFiles", recentFiles.toString());
        }
    }
    
    public String[] getRecentFiles() {
        return(toolPrefs.get("recentFiles", "").split(PARAM_DELIM));
    }
    
    public void setUseGTKLF(boolean value) {
        toolPrefs.putBoolean("useGTKLF", value);
    }
    
    public boolean isUseGTKLF() {
        return(toolPrefs.getBoolean("useGTKLF", System.getProperty("java.version").startsWith("1.6") && 
                System.getProperty("os.name").startsWith("Linux") ? true : false));
    }
    
    public void setMillisTimeStamp(boolean value) {
        toolPrefs.putBoolean("millisTimeStamp", value);
    }
    
    public boolean getMillisTimeStamp() {
        return(toolPrefs.getBoolean("millisTimeStamp", false));
    }
    
    public void setShowHotspotClasses(boolean value) {
        toolPrefs.putBoolean("showHotspotClasses", value);
    }
    
    public boolean getShowHotspotClasses() {
        return(toolPrefs.getBoolean("showHotspotClasses", false));
    }
    
    /**
     * temporary storage for filters to not to have them be parsed again
     */
    private DefaultListModel cachedFilters = null;
    
    
    public ListModel getFilters() {
        DefaultListModel filters = null;
        if(cachedFilters == null) {
            String filterString = toolPrefs.get("filters", "");
            if(filterString.length() > 0) {
                filters = new DefaultListModel();
                String[] sFilters = filterString.split(PARAM_DELIM);
                filters.ensureCapacity(sFilters.length);
                try {
                    for(int i = 0; i < sFilters.length; i++) {
                        String[] filterData = sFilters[i].split(FILTER_SEP);
                        Filter newFilter = new Filter(filterData[0],
                                filterData[1], Integer.parseInt(filterData[2]),
                                filterData[3].equals("true"), filterData[4].equals("true"), filterData[5].equals("true"));
                        filters.add(i, newFilter);
                    }
                } catch (ArrayIndexOutOfBoundsException aioob) {
                    // fall back to default filters
                    filters = getPredefinedFilters();
                }
            } else {
                filters = getPredefinedFilters();
            }
        } else {
            filters = cachedFilters;
        }
        return(filters);
    }
    
    /**
     * generate the default filter set.
     */
    private DefaultListModel getPredefinedFilters() {
        Filter newFilter = new Filter("System Thread Exclusion Filter", ".*at\\s.*", Filter.HAS_IN_STACK_RULE, true, false, false);
        DefaultListModel filters = new DefaultListModel();
        filters.ensureCapacity(2);
        filters.add(0, newFilter);
        newFilter = new Filter("Idle Threads Filter", "", Filter.SLEEPING_RULE, true, true, false);
        filters.add(1, newFilter);
        return(filters);
    }
    
    public void setFilters(DefaultListModel filters) {
        // store into cache
        cachedFilters = filters;
        StringBuffer filterString = new StringBuffer();
        for(int i = 0; i < filters.getSize(); i++) {
            if(i > 0) {
                filterString.append(PARAM_DELIM);
            }
            filterString.append(((Filter)filters.getElementAt(i)).getName());
            filterString.append(FILTER_SEP);
            filterString.append(((Filter)filters.getElementAt(i)).getFilterExpression());
            filterString.append(FILTER_SEP);
            filterString.append(((Filter)filters.getElementAt(i)).getFilterRule());
            filterString.append(FILTER_SEP);
            filterString.append(((Filter)filters.getElementAt(i)).isGeneralFilter());
            filterString.append(FILTER_SEP);
            filterString.append(((Filter)filters.getElementAt(i)).isExclusionFilter());
            filterString.append(FILTER_SEP);
            filterString.append(((Filter)filters.getElementAt(i)).isEnabled());
        }
        toolPrefs.put("filters", filterString.toString());
        setFilterLastChanged();
    }
    
    private long filterLastChanged = -1;
    
    /**
     * return time stamp of last change time stamp of filter settings
     */
    public long getFiltersLastChanged(){
        return(filterLastChanged);
    }
    
    private void setFilterLastChanged() {
        filterLastChanged = System.currentTimeMillis();
    }
        
    public void flush() {
        try {
            toolPrefs.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * check if new file is in given recent file list
     */
    private boolean hasInRecentFiles(String file, String[] currentFiles) {
        boolean found = false;
        
        for(int i = 0; i < currentFiles.length; i++) {
           if(file.equals(currentFiles[i])) {
               found = true;
               break;
           } 
        }
        return found;
    }
}
