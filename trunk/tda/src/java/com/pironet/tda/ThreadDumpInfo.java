/*
 * ThreadDumpInfo.java
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
 * $Id: ThreadDumpInfo.java,v 1.5 2007-12-08 09:58:34 irockel Exp $
 */
package com.pironet.tda;

/**
 * stores structural data about a thread dump.
 * 
 * @author irockel
 */
public class ThreadDumpInfo extends AbstractInfo {
    private int logLine;
    private String startTime;
    private String overview;
    
    ThreadDumpInfo(String name, int lineCount) {
        setName(name);
        this.logLine = lineCount;
    }
    
    /**
     * get the log line where to find the starting
     * point of this thread dump in the log file
     * @return starting point of thread dump in logfile, 0 if none set.
     */
    public int getLogLine() {
        return logLine;
    }

    /**
     * set the log line where to find the dump in the logfile.
     * @param logLine
     */
    public void setLogLine(int logLine) {
        this.logLine = logLine;
    }
    
    /**
     * get the approx. start time of the dump represented by this
     * node.
     * @return start time as string, format may differ as it is just
     *         parsed from the log file.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * set the start time as string, can be of any format.
     * @param startTime the start time as string.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * get the overview information of this thread dump.
     * @return overview information.
     */
    public String getOverview() {
        return overview;
    }
    
    /**
     * set the overview information of this thread dump.
     * @param overview the infos to be displayed (in html)
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * string representation of this node, is used to displayed the node info
     * in the tree.
     * @return the thread dump information (one line).
     */
    public String toString() {
        StringBuffer postFix = new StringBuffer();
        if(logLine > 0) {
            postFix.append(" at line " + getLogLine());
        }
        if(startTime != null) {
            postFix.append(" around " + startTime);
        }
        return(getName() +  postFix);
    }

}
