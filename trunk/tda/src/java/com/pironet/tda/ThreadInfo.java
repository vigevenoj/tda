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
 * $Id: ThreadInfo.java,v 1.4 2007-11-22 13:38:29 irockel Exp $
 */

package com.pironet.tda;

import java.io.Serializable;

/**
 * Info (name, content tuple) for thread dump display tree.
 *
 * @author irockel
 */
public class ThreadInfo implements Serializable {
    public String threadName;
    public String content;
    public String info;
    
    public int lineCount;
    
    public ThreadInfo(String name, String info, String content, int lineCount) {
        threadName = name;
        this.info = info;
        this.content = content;
        this.lineCount = lineCount;
    }
    
    public String toString() {
        return threadName;
    }
}
