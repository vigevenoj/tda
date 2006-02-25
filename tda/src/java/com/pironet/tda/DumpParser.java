/*
 * DumpParser.java
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
 * $Id: DumpParser.java,v 1.3 2006-02-25 08:15:21 irockel Exp $
 */

package com.pironet.tda;

import java.io.IOException;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Dump Parser Interface, defines base methods for all dump parsers.
 *
 * @author irockel
 */
public interface DumpParser {
    public boolean hasMoreDumps();
    
    public MutableTreeNode parseNext();
    
    public void close() throws IOException;
    
    public void mergeDumps(DefaultMutableTreeNode root, Map dumpStore, TreePath firstDump, TreePath secondDump);
}
