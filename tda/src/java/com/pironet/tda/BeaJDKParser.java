/*
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
 * $Id: BeaJDKParser.java,v 1.1 2007-11-27 09:42:20 irockel Exp $
 */

package com.pironet.tda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Parses Bea/JRockit Thread Dumps
 * 
 * @author irockel
 */
public class BeaJDKParser implements DumpParser {

    /**
     * constructs a new instance of a bea jdk parser
     * @param dumpFileStream the dump file stream to read.
     * @param threadStore the thread store to store the thread informations in.
     */
    public BeaJDKParser(BufferedReader bis, Map threadStore, int lineCounter) {
    }
    
    public boolean hasMoreDumps() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MutableTreeNode parseNext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void close() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void findLongRunningThreads(DefaultMutableTreeNode root, Map dumpStore, TreePath[] paths, int minOccurence, String regex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mergeDumps(DefaultMutableTreeNode root, Map dumpStore, TreePath[] dumps, int minOccurence, String regex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isFoundClassHistograms() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void parseLoggcFile(InputStream loggcFileStream, DefaultMutableTreeNode root) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDumpHistogramCounter(int value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * check if the passed logline contains the beginning of a Bea jdk thread
     * dump.
     * @param logLine the line of the logfile to test
     * @return true, if the start of a bea thread dump is detected.
     */
    public static boolean checkForSupportedThreadDump(String logLine) {
        return (logLine.trim().contains("===== FULL THREAD DUMP ==============="));
    }
}
