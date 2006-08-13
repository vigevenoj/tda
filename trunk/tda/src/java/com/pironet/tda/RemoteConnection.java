/*
 * RemoteConnection.java
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
 * $Id: RemoteConnection.java,v 1.3 2006-08-13 19:33:48 irockel Exp $
 */

package com.pironet.tda;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * base node for a thread dump from a remote (jmx) connection.
 *
 * @author irockel
 */
public class RemoteConnection extends DumpsBaseNode {
    String content;
    
    String connection;
    
    /** 
     * Creates a new instance of RemoteConnection 
     */
    public RemoteConnection(String content, String connection) {
        this.content = content;
        this.connection = connection;
    }
    
    /**
     * return the information content
     */
    public Object getContent() {
        return content;
    }
    
    /**
     * return the raw connect string
     */
    public String getConnection() {
        return connection;
    }
    
    /**
     * build connect for remote connection
     */
    public URL getConnectURL() {
        // build rmi connect string
        String urlString = "/jndi/rmi://" + getConnection() + "/jmxrmi";
        URL connectURL = null;
        try {
            connectURL = new URL(urlString);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return(connectURL);
    }
    
    public String toString() {
        return(((String) getContent()) + getConnection());
    }
}
