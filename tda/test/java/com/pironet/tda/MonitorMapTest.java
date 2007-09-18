/*
 * MonitorMapTest.java
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
 * $Id: MonitorMapTest.java,v 1.2 2007-09-18 09:13:01 irockel Exp $
 */
package com.pironet.tda;

import junit.framework.*;

/**
 *
 * @author irockel
 */
public class MonitorMapTest extends TestCase {
    
    public MonitorMapTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MonitorMapTest.class);
        
        return suite;
    }

    /**
     * Test of addToMonitorMap method, of class com.pironet.tda.MonitorMap.
     */
    public void testAddToMonitorMap() {
        System.out.println("addToMonitorMap");
        
        /*String key = "";
        Set[] objectSet = null;
        MonitorMap instance = new MonitorMap();
        
        instance.addToMonitorMap(key, objectSet);*/
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasInMonitorMap method, of class com.pironet.tda.MonitorMap.
     */
    public void testHasInMonitorMap() {
        System.out.println("hasInMonitorMap");
        
        String key = "";
        MonitorMap instance = new MonitorMap();
        
        boolean expResult = true;
        boolean result = instance.hasInMonitorMap(key);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFromMonitorMap method, of class com.pironet.tda.MonitorMap.
     */
    public void testGetFromMonitorMap() {
        System.out.println("getFromMonitorMap");
        
        /*String key = "";
        MonitorMap instance = new MonitorMap();
        
        Set[] expResult = null;
        Set[] result = instance.getFromMonitorMap(key);
        assertEquals(expResult, result);*/
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addWaitToMonitor method, of class com.pironet.tda.MonitorMap.
     */
    public void testAddWaitToMonitor() {
        System.out.println("addWaitToMonitor");
        
        /*String key = "";
        String[] waitThread = null;
        MonitorMap instance = new MonitorMap();
        
        instance.addWaitToMonitor(key, waitThread);*/
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addLockToMonitor method, of class com.pironet.tda.MonitorMap.
     */
    public void testAddLockToMonitor() {
        System.out.println("addLockToMonitor");
        
        /*String key = "";
        String[] lockThread = null;
        MonitorMap instance = new MonitorMap();
        
        instance.addLockToMonitor(key, lockThread);*/
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addSleepToMonitor method, of class com.pironet.tda.MonitorMap.
     */
    public void testAddSleepToMonitor() {
        System.out.println("addSleepToMonitor");
        
        /*String key = "";
        String[] sleepThread = null;
        MonitorMap instance = new MonitorMap();
        
        instance.addSleepToMonitor(key, sleepThread);*/
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseAndAddThread method, of class com.pironet.tda.MonitorMap.
     */
    public void testParseAndAddThread() {
        System.out.println("parseAndAddThread");
        
        /*String line = "";
        String threadTitle = "";
        String currentThread = "";
        MonitorMap instance = new MonitorMap();
        
        instance.parseAndAddThread(line, threadTitle, currentThread);*/
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of iterOfKeys method, of class com.pironet.tda.MonitorMap.
     */
    public void testIterOfKeys() {
        System.out.println("iterOfKeys");
        
        /*MonitorMap instance = new MonitorMap();
        
        Iterator expResult = null;
        Iterator result = instance.iterOfKeys();
        assertEquals(expResult, result);*/
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of size method, of class com.pironet.tda.MonitorMap.
     */
    public void testSize() {
        System.out.println("size");
        
        /*MonitorMap instance = new MonitorMap();
        
        int expResult = 0;
        int result = instance.size();
        assertEquals(expResult, result);*/
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
