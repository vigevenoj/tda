/*
 * JMXConnection.java
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
 * $Id: JMXConnection.java,v 1.2 2006-12-28 17:34:03 irockel Exp $
 */

package com.pironet.tda;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * jmx connection is a parser which fetches thread dumps via jmx connection
 *
 * @author irockel
 */
public class JMXConnection implements DumpParser {
    private RemoteConnection jmxConnection;
    
    /** Creates a new instance of JMXConnection */
    public JMXConnection(RemoteConnection value) {
        jmxConnection = value;
    }

    public void mergeDumps(DefaultMutableTreeNode root, Map dumpStore, TreePath[] dumps, int minOccurence, String regex) {
    }

    public void findLongRunningThreads(DefaultMutableTreeNode root, Map dumpStore, TreePath[] paths, int minOccurence, String regex) {
    }

    public void parseLoggcFile(InputStream loggcFileStream, DefaultMutableTreeNode root) {
    }

    public void setDumpHistogramCounter(int value) {
    }

    public MutableTreeNode parseNext() {
        return null;
    }

    /**
     * it is not possible to fetch class histograms via jmx at the moment
     * @return always false
     */
    public boolean isFoundClassHistograms() {
        return false;
    }

    /**
     * only return one dump at a time
     */
    public boolean hasMoreDumps() {
        return false;
    }

    /**
     * close connection to jmx remote agent
     */
    public void close() throws IOException {
    }

    
}
