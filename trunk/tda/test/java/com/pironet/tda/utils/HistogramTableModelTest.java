/*
 * HistogramTableModelTest.java
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
 * $Id: HistogramTableModelTest.java,v 1.1 2006-05-09 13:50:48 irockel Exp $
 */
package com.pironet.tda.utils;

import junit.framework.*;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author irockel
 */
public class HistogramTableModelTest extends TestCase {
    
    public HistogramTableModelTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(HistogramTableModelTest.class);
        
        return suite;
    }

    /**
     * Test of addEntry method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testAddEntry() {
        System.out.println("addEntry");
        
        String className = "";
        int instanceCount = 0;
        int bytes = 0;
        HistogramTableModel instance = new HistogramTableModel();
        
        instance.addEntry(className, instanceCount, bytes);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValueAt method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testGetValueAt() {
        System.out.println("getValueAt");
        
        int rowIndex = 0;
        int columnIndex = 0;
        HistogramTableModel instance = new HistogramTableModel();
        
        Object expResult = null;
        Object result = instance.getValueAt(rowIndex, columnIndex);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColumnName method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testGetColumnName() {
        System.out.println("getColumnName");
        
        int col = 0;
        HistogramTableModel instance = new HistogramTableModel();
        
        String expResult = "";
        String result = instance.getColumnName(col);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowCount method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testGetRowCount() {
        System.out.println("getRowCount");
        
        HistogramTableModel instance = new HistogramTableModel();
        
        int expResult = 0;
        int result = instance.getRowCount();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColumnCount method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testGetColumnCount() {
        System.out.println("getColumnCount");
        
        HistogramTableModel instance = new HistogramTableModel();
        
        int expResult = 0;
        int result = instance.getColumnCount();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColumnClass method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testGetColumnClass() {
        System.out.println("getColumnClass");
        
        int c = 0;
        HistogramTableModel instance = new HistogramTableModel();
        
        Class expResult = null;
        Class result = instance.getColumnClass(c);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isOOM method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testIsOOM() {
        System.out.println("isOOM");
        
        HistogramTableModel instance = new HistogramTableModel();
        
        boolean expResult = true;
        boolean result = instance.isOOM();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBytes method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testSetBytes() {
        System.out.println("setBytes");
        
        long value = 0L;
        HistogramTableModel instance = new HistogramTableModel();
        
        instance.setBytes(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBytes method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testGetBytes() {
        System.out.println("getBytes");
        
        HistogramTableModel instance = new HistogramTableModel();
        
        long expResult = 0L;
        long result = instance.getBytes();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setInstances method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testSetInstances() {
        System.out.println("setInstances");
        
        long value = 0L;
        HistogramTableModel instance = new HistogramTableModel();
        
        instance.setInstances(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstances method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testGetInstances() {
        System.out.println("getInstances");
        
        HistogramTableModel instance = new HistogramTableModel();
        
        long expResult = 0L;
        long result = instance.getInstances();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIncomplete method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testSetIncomplete() {
        System.out.println("setIncomplete");
        
        boolean value = true;
        HistogramTableModel instance = new HistogramTableModel();
        
        instance.setIncomplete(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isIncomplete method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testIsIncomplete() {
        System.out.println("isIncomplete");
        
        HistogramTableModel instance = new HistogramTableModel();
        
        boolean expResult = true;
        boolean result = instance.isIncomplete();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFilter method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testSetFilter() {
        System.out.println("setFilter");
        
        String value = "";
        HistogramTableModel instance = new HistogramTableModel();
        
        instance.setFilter(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setShowHotspotClasses method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testSetShowHotspotClasses() {
        System.out.println("setShowHotspotClasses");
        
        boolean value = true;
        HistogramTableModel instance = new HistogramTableModel();
        
        instance.setShowHotspotClasses(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFilter method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testGetFilter() {
        System.out.println("getFilter");
        
        HistogramTableModel instance = new HistogramTableModel();
        
        String expResult = "";
        String result = instance.getFilter();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIgnoreCase method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testSetIgnoreCase() {
        System.out.println("setIgnoreCase");
        
        boolean value = true;
        HistogramTableModel instance = new HistogramTableModel();
        
        instance.setIgnoreCase(value);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isIgnoreCase method, of class com.pironet.tda.utils.HistogramTableModel.
     */
    public void testIsIgnoreCase() {
        System.out.println("isIgnoreCase");
        
        HistogramTableModel instance = new HistogramTableModel();
        
        boolean expResult = true;
        boolean result = instance.isIgnoreCase();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
