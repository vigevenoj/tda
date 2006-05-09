/*
 * UtilsSuite.java
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
 * $Id: UtilsSuite.java,v 1.1 2006-05-09 13:50:48 irockel Exp $
 */
package com.pironet.tda.utils;

import junit.framework.*;

/**
 *
 * @author irockel
 */
public class UtilsSuite extends TestCase {
    
    public UtilsSuite(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * suite method automatically generated by JUnit module
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("UtilsSuite");
        suite.addTest(com.pironet.tda.utils.HistogramTableModelTest.suite());
        suite.addTest(com.pironet.tda.utils.PrefManagerTest.suite());
        return suite;
    }
    
}
