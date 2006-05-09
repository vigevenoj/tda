/*
 * PrefManagerTest.java
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
 * $Id: PrefManagerTest.java,v 1.1 2006-05-09 13:50:48 irockel Exp $
 */
package com.pironet.tda.utils;

import junit.framework.*;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author irockel
 */
public class PrefManagerTest extends TestCase {
    
    public PrefManagerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PrefManagerTest.class);
        
        return suite;
    }

    /**
     * Test of get method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGet() {
        System.out.println("get");
        
        PrefManager expResult = null;
        PrefManager result = PrefManager.get();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWindowState method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetWindowState() {
        System.out.println("getWindowState");
        
        PrefManager instance = null;
        
        int expResult = 0;
        int result = instance.getWindowState();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setWindowState method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetWindowState() {
        System.out.println("setWindowState");
        
        int windowState = 0;
        PrefManager instance = null;
        
        instance.setWindowState(windowState);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSelectedPath method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetSelectedPath() {
        System.out.println("getSelectedPath");
        
        PrefManager instance = null;
        
        File expResult = null;
        File result = instance.getSelectedPath();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSelectedPath method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetSelectedPath() {
        System.out.println("setSelectedPath");
        
        File directory = null;
        PrefManager instance = null;
        
        instance.setSelectedPath(directory);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPreferredSize method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetPreferredSize() {
        System.out.println("getPreferredSize");
        
        PrefManager instance = null;
        
        Dimension expResult = null;
        Dimension result = instance.getPreferredSize();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPreferredSize method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetPreferredSize() {
        System.out.println("setPreferredSize");
        
        Dimension size = null;
        PrefManager instance = null;
        
        instance.setPreferredSize(size);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWindowPos method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetWindowPos() {
        System.out.println("getWindowPos");
        
        PrefManager instance = null;
        
        Point expResult = null;
        Point result = instance.getWindowPos();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setWindowPos method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetWindowPos() {
        System.out.println("setWindowPos");
        
        int x = 0;
        int y = 0;
        PrefManager instance = null;
        
        instance.setWindowPos(x, y);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxRows method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetMaxRows() {
        System.out.println("getMaxRows");
        
        PrefManager instance = null;
        
        int expResult = 0;
        int result = instance.getMaxRows();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMaxRows method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetMaxRows() {
        System.out.println("setMaxRows");
        
        int rows = 0;
        PrefManager instance = null;
        
        instance.setMaxRows(rows);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStreamResetBuffer method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetStreamResetBuffer() {
        System.out.println("getStreamResetBuffer");
        
        PrefManager instance = null;
        
        int expResult = 0;
        int result = instance.getStreamResetBuffer();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setStreamResetBuffer method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetStreamResetBuffer() {
        System.out.println("setStreamResetBuffer");
        
        int buffer = 0;
        PrefManager instance = null;
        
        instance.setStreamResetBuffer(buffer);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getForceLoggcLoading method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetForceLoggcLoading() {
        System.out.println("getForceLoggcLoading");
        
        PrefManager instance = null;
        
        boolean expResult = true;
        boolean result = instance.getForceLoggcLoading();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setForceLoggcLoading method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetForceLoggcLoading() {
        System.out.println("setForceLoggcLoading");
        
        boolean force = true;
        PrefManager instance = null;
        
        instance.setForceLoggcLoading(force);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDateParsingRegex method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetDateParsingRegex() {
        System.out.println("getDateParsingRegex");
        
        PrefManager instance = null;
        
        String expResult = "";
        String result = instance.getDateParsingRegex();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDateParsingRegex method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetDateParsingRegex() {
        System.out.println("setDateParsingRegex");
        
        String dateRegex = "";
        PrefManager instance = null;
        
        instance.setDateParsingRegex(dateRegex);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMillisTimeStamp method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetMillisTimeStamp() {
        System.out.println("setMillisTimeStamp");
        
        boolean value = true;
        PrefManager instance = null;
        
        instance.setMillisTimeStamp(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMillisTimeStamp method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetMillisTimeStamp() {
        System.out.println("getMillisTimeStamp");
        
        PrefManager instance = null;
        
        boolean expResult = true;
        boolean result = instance.getMillisTimeStamp();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setShowHotspotClasses method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testSetShowHotspotClasses() {
        System.out.println("setShowHotspotClasses");
        
        boolean value = true;
        PrefManager instance = null;
        
        instance.setShowHotspotClasses(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getShowHotspotClasses method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testGetShowHotspotClasses() {
        System.out.println("getShowHotspotClasses");
        
        PrefManager instance = null;
        
        boolean expResult = true;
        boolean result = instance.getShowHotspotClasses();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of flush method, of class com.pironet.tda.utils.PrefManager.
     */
    public void testFlush() {
        System.out.println("flush");
        
        PrefManager instance = null;
        
        instance.flush();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
