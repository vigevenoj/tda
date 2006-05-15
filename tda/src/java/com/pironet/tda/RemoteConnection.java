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
 * $Id: RemoteConnection.java,v 1.1 2006-05-15 20:24:03 irockel Exp $
 */

package com.pironet.tda;

/**
 * base node for a thread dump from a remote (jmx) connection.
 *
 * @author irockel
 */
public class RemoteConnection extends DumpsBaseNode {
    
    /** Creates a new instance of RemoteConnection */
    public RemoteConnection() {
    }
    
    public Object getContent() {
        return null;
    }
}
