/*
 * Logfile.java
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: Logfile.java,v 1.3 2006-09-23 15:15:36 irockel Exp $
 */

package com.pironet.tda;

/**
 * root node info object of log file thread dump information
 * @author irockel
 */
public class Logfile extends DumpsBaseNode {
    private String content;
    
    /** 
     * Creates a new instance of Logfile 
     */
    public Logfile(String value) {
        content = value;
    }
    
    /**
     * returns the text content of this node
     */
    public Object getContent() {
        return content;
    }
    
    public String toString() {
        return((String) getContent());
    }
}
