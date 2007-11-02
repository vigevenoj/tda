/*
 * SunJDKParserTest.java
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
 * $Id: SunJDKParserTest.java,v 1.1 2007-11-02 08:43:05 irockel Exp $
 */
package com.pironet.tda;

import junit.framework.*;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author irockel
 */
public class SunJDKParserTest extends TestCase {
    
    public SunJDKParserTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SunJDKParserTest.class);
        
        return suite;
    }

    /**
     * Test of hasMoreDumps method, of class com.pironet.tda.SunJDKParser.
     */
    public void testHasMoreDumps() {
        System.out.println("hasMoreDumps");
        
        SunJDKParser instance = null;
        
        boolean expResult = true;
        boolean result = instance.hasMoreDumps();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isFoundClassHistograms method, of class com.pironet.tda.SunJDKParser.
     */
    public void testIsFoundClassHistograms() {
        System.out.println("isFoundClassHistograms");
        
        SunJDKParser instance = null;
        
        boolean expResult = true;
        boolean result = instance.isFoundClassHistograms();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseNext method, of class com.pironet.tda.SunJDKParser.
     */
    public void testParseNext() {
        System.out.println("parseNext");
        
        SunJDKParser instance = null;
        
        MutableTreeNode expResult = null;
        MutableTreeNode result = instance.parseNext();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseLoggcFile method, of class com.pironet.tda.SunJDKParser.
     */
    public void testParseLoggcFile() {
        System.out.println("parseLoggcFile");
        
        /*InputStream loggcFileStream = null;
        DefaultMutableTreeNode root = null;
        Map dumpStore = null;
        SunJDKParser instance = null;
        
        instance.parseLoggcFile(loggcFileStream, root, dumpStore);*/
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDumpHistogramCounter method, of class com.pironet.tda.SunJDKParser.
     */
    public void testSetDumpHistogramCounter() {
        System.out.println("setDumpHistogramCounter");
        
        int value = 0;
        SunJDKParser instance = null;
        
        instance.setDumpHistogramCounter(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findLongRunningThreads method, of class com.pironet.tda.SunJDKParser.
     */
    public void testFindLongRunningThreads() {
        System.out.println("findLongRunningThreads");
        
        DefaultMutableTreeNode root = null;
        Map dumpStore = null;
        TreePath[] paths = null;
        int minOccurence = 0;
        String regex = "";
        SunJDKParser instance = null;
        
        instance.findLongRunningThreads(root, dumpStore, paths, minOccurence, regex);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mergeDumps method, of class com.pironet.tda.SunJDKParser.
     */
    public void testMergeDumps() {
        System.out.println("mergeDumps");
        
        DefaultMutableTreeNode root = null;
        Map dumpStore = null;
        TreePath[] dumps = null;
        int minOccurence = 0;
        String regex = "";
        SunJDKParser instance = null;
        
        instance.mergeDumps(root, dumpStore, dumps, minOccurence, regex);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of close method, of class com.pironet.tda.SunJDKParser.
     */
    public void testClose() throws Exception {
        System.out.println("close");
        
        SunJDKParser instance = null;
        
        instance.close();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
