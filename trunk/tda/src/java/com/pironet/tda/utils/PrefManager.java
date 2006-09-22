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
 * $Id: PrefManager.java,v 1.8 2006-09-22 11:30:44 irockel Exp $
 */
package com.pironet.tda.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.ListModel;

/**
 * Singleton class for accessing system preferences.
 * Window sizes, and positions are stored here and also the last accessed path
 * is stored here.
 *
 * @author irockel
 */
public class PrefManager {
    
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
        return(toolPrefs.get("dateParsingRegex", ""));
    }
    
    public void setDateParsingRegex(String dateRegex) {
        toolPrefs.put("dateParsingRegex", dateRegex);
    }
    
    public String[] getDateParsingRegexs() {
        String elems = toolPrefs.get("dateParsingRegexs", "");
        if(elems.equals("")) {
            elems = getDateParsingRegex();
        }
        return(elems.split("§§§§"));
    }
    
    public void setDateParsingRegexs(ListModel regexs) {
        toolPrefs.put("dateParsingRegexs", regexsToString(regexs));
    }
    
    private String regexsToString(ListModel regexs) {
        StringBuffer elems = new StringBuffer();
        for(int i = 0; i < regexs.getSize(); i++) {
            elems.append(regexs.getElementAt(i));
            if(i+1 < regexs.getSize()) {
                elems.append("§§§§");
            }
        }
        return(elems.toString());
    }
    
    public void addToRecentFiles(String file) {
        String[] currentFiles = getRecentFiles();
        int start = currentFiles.length == 10 ? 1 : 0;
        StringBuffer recentFiles = new StringBuffer();
        
        for(int i = start; i < currentFiles.length; i++) {
            recentFiles.append(currentFiles[i]);
            recentFiles.append("§§§§");
        }
        
        // append new files
        recentFiles.append(file);
        toolPrefs.put("recentFiles", recentFiles.toString());
    }
    
    public String[] getRecentFiles() {
        return(toolPrefs.get("recentFiles", "").split("§§§§"));
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
    
    public void flush() {
        try {
            toolPrefs.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
}
