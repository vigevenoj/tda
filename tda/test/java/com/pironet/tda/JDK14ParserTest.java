/*
 * JDK14ParserTest.java
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
 * $Id: JDK14ParserTest.java,v 1.1 2006-05-09 13:50:47 irockel Exp $
 */
package com.pironet.tda;

import junit.framework.*;
import com.pironet.tda.utils.HistogramTableModel;
import com.pironet.tda.utils.PrefManager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author irockel
 */
public class JDK14ParserTest extends TestCase {
    
    public JDK14ParserTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(JDK14ParserTest.class);
        
        return suite;
    }

    /**
     * Test of hasMoreDumps method, of class com.pironet.tda.JDK14Parser.
     */
    public void testHasMoreDumps() {
        System.out.println("hasMoreDumps");
        
        JDK14Parser instance = null;
        
        boolean expResult = true;
        boolean result = instance.hasMoreDumps();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isFoundClassHistograms method, of class com.pironet.tda.JDK14Parser.
     */
    public void testIsFoundClassHistograms() {
        System.out.println("isFoundClassHistograms");
        
        JDK14Parser instance = null;
        
        boolean expResult = true;
        boolean result = instance.isFoundClassHistograms();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseNext method, of class com.pironet.tda.JDK14Parser.
     */
    public void testParseNext() {
        System.out.println("parseNext");
        
        JDK14Parser instance = null;
        
        MutableTreeNode expResult = null;
        MutableTreeNode result = instance.parseNext();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseLoggcFile method, of class com.pironet.tda.JDK14Parser.
     */
    public void testParseLoggcFile() {
        System.out.println("parseLoggcFile");
        
        InputStream loggcFileStream = null;
        DefaultMutableTreeNode root = null;
        Map dumpStore = null;
        JDK14Parser instance = null;
        
        instance.parseLoggcFile(loggcFileStream, root, dumpStore);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDumpHistogramCounter method, of class com.pironet.tda.JDK14Parser.
     */
    public void testSetDumpHistogramCounter() {
        System.out.println("setDumpHistogramCounter");
        
        int value = 0;
        JDK14Parser instance = null;
        
        instance.setDumpHistogramCounter(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findLongRunningThreads method, of class com.pironet.tda.JDK14Parser.
     */
    public void testFindLongRunningThreads() {
        System.out.println("findLongRunningThreads");
        
        DefaultMutableTreeNode root = null;
        Map dumpStore = null;
        TreePath[] paths = null;
        int minOccurence = 0;
        String regex = "";
        JDK14Parser instance = null;
        
        instance.findLongRunningThreads(root, dumpStore, paths, minOccurence, regex);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mergeDumps method, of class com.pironet.tda.JDK14Parser.
     */
    public void testMergeDumps() {
        System.out.println("mergeDumps");
        
        DefaultMutableTreeNode root = null;
        Map dumpStore = null;
        TreePath[] dumps = null;
        int minOccurence = 0;
        String regex = "";
        JDK14Parser instance = null;
        
        instance.mergeDumps(root, dumpStore, dumps, minOccurence, regex);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of close method, of class com.pironet.tda.JDK14Parser.
     */
    public void testClose() throws Exception {
        System.out.println("close");
        
        JDK14Parser instance = null;
        
        instance.close();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
