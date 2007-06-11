/*
 * LogFileContent.java
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
 * along with TDA; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: LogFileContent.java,v 1.1 2007-06-11 20:14:43 irockel Exp $
 */

package com.pironet.tda;

/**
 * logfile content info object of log file thread dump information.
 * @author irockel
 */
public class LogFileContent {
    
    private String logFile;
    
    /** 
     * Creates a new instance of LogFileContent 
     */
    public LogFileContent(String logFile) {
        if(!logFile.startsWith("file://")) {
            logFile = "file://" + logFile; 
        }
        setLogFile(logFile);
    }
    
    public String getLogfile() {
        return(logFile);
    }
    
    public void setLogFile(String value) {
        logFile = value;
    }
    
    public String toString() {
        return("Logfile");
    }
    
}
