/*
 * DumpParserFactoryTest.java
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
 * $Id: DumpParserFactoryTest.java,v 1.2 2007-10-29 19:43:11 irockel Exp $
 */
package com.pironet.tda;

import junit.framework.*;
import java.io.InputStream;
import java.util.Map;

/**
 *
 * @author irockel
 */
public class DumpParserFactoryTest extends TestCase {
    
    public DumpParserFactoryTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(DumpParserFactoryTest.class);
        
        return suite;
    }

    /**
     * Test of get method, of class com.pironet.tda.DumpParserFactory.
     */
    public void testGet() {
        System.out.println("get");
        
        DumpParserFactory result = DumpParserFactory.get();
        assertNotNull(result);                
    }

    /**
     * Test of getDumpParserForVersion method, of class com.pironet.tda.DumpParserFactory.
     */
    public void testGetDumpParserForVersion() {
        System.out.println("getDumpParserForVersion");
        
        String javaVersion = "1.4";
        InputStream dumpFileStream = null;
        Map threadStore = null;
        DumpParserFactory instance = DumpParserFactory.get();
        
        DumpParser result = instance.getDumpParserForVersion(javaVersion, dumpFileStream, threadStore, false);
        assertNotNull(result);
    }

    /**
     * Test of getCurrentDumpParser method, of class com.pironet.tda.DumpParserFactory.
     */
    public void testGetCurrentDumpParser() {
        System.out.println("getCurrentDumpParser");
        
        DumpParserFactory instance = DumpParserFactory.get();
        
        DumpParser result = instance.getCurrentDumpParser();
        assertNotNull(result);        
    }
    
}
