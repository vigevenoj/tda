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
 * $Id: ThreadInfo.java,v 1.5 2007-11-23 10:12:27 irockel Exp $
 */

package com.pironet.tda;

import java.io.Serializable;

/**
 * Info (name, content tuple) for thread dump display tree.
 *
 * @author irockel
 */
public class ThreadInfo implements Serializable {
    private String threadName;
    private String content;
    private String info;
    
    private int lineCount;
    
    public ThreadInfo(String name, String info, String content, int lineCount) {
        threadName = name;
        this.info = info;
        this.content = content;
        this.lineCount = lineCount;
    }
    
    public String toString() {
        return getThreadName();
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }
}
