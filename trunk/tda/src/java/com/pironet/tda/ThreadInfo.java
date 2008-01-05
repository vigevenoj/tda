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
 * $Id: ThreadInfo.java,v 1.7 2008-01-05 08:55:18 irockel Exp $
 */

package com.pironet.tda;

/**
 * Info (name, content tuple) for thread dump display tree.
 *
 * @author irockel
 */
public class ThreadInfo extends AbstractInfo {
    private String content;
    private String info;
    private int stackLines;
    private String[] tokens;
    
    public ThreadInfo(String name, String info, String content, int stackLines) {
        setName(name);
        this.info = info;
        this.content = content;
    }
    
    public String toString() {
        return getName();
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

    public int getStackLines() {
        return stackLines;
    }

    public void setStackLines(int stackLines) {
        this.stackLines = stackLines;
    }
    
    //FIXME: no clean abstraction of sun thread dump specific code.
    public String[] getTokens() {
        if(tokens == null) {
            tokens = new String[7];
            
            tokens[0] = getName().substring(1, getName().lastIndexOf('"'));
            tokens[1] = getName().indexOf("daemon") > 0 ? "Daemon" : "Task";
            tokens[2] = getName().substring(getName().indexOf("prio=") +5, getName().indexOf("tid=")-1);
            tokens[3] = String.valueOf(Integer.parseInt(getName().substring(getName().indexOf("tid=") +6, getName().indexOf("nid=") -1), 16)); 
            tokens[4] = String.valueOf(Integer.parseInt(getName().substring(getName().indexOf("nid=") +6, 
                    getName().indexOf(" ", getName().indexOf("nid="))), 16));
            if(getName().indexOf('[') > 0) {
                tokens[5] = getName().substring(getName().indexOf(" ", getName().indexOf("nid=")) + 1, getName().indexOf('[',
                        getName().indexOf("nid=")) - 1);
                tokens[6] = getName().substring(getName().indexOf('['));
            } else {
                tokens[5] = getName().substring(getName().indexOf(" ", getName().indexOf("nid=")) + 1);
                tokens[6] = "<no address range>";
            }
        }
        
        return(tokens);
    }
}
