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
 * $Id: LogFileContent.java,v 1.2 2007-10-03 12:50:27 irockel Exp $
 */

package com.pironet.tda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.SoftReference;

/**
 * logfile content info object of log file thread dump information.
 * @author irockel
 */
public class LogFileContent {
    
    private String logFile;
    
    /**
     * stored as soft reference, as this content might get quite big.
     */
    private SoftReference content;
    
    /** 
     * Creates a new instance of LogFileContent 
     */
    public LogFileContent(String logFile) {
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
    
    /**
     * get the content as string, it is stored as soft reference,
     * so it might be loaded from disk again, as the vm needed memory
     * after the last access to it.
     */
    public String getContent() {
        if(content == null || content.get() == null) {
            readContent();
        }
        
        return((String) content.get());
    }

    /**
     * read the content in the soft reference object, currently used
     * StringBuffer to maintain 1.4 compability. Should be switched 
     * to StringReader if switched to 1.5 for better performance as 
     * synchronization is not needed here.
     */
    private void readContent() {

        BufferedReader br = null;
        try {
            File contentFile = new File(getLogfile());
            br = new BufferedReader(new FileReader(getLogfile()));
            StringBuffer contentReader = new StringBuffer();
            while(br.ready()) {
                contentReader.append(br.readLine());
                contentReader.append("\n");
            }
            content = new SoftReference(contentReader.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
