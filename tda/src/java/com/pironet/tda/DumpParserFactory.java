/*
 * DumpParserFactory.java
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
 * $Id: DumpParserFactory.java,v 1.2 2006-02-20 09:47:42 irockel Exp $
 */

package com.pironet.tda;

import java.util.Map;

/**
 * Provides Factory Interface for dump parsers.
 *
 * @author irockel
 */
public class DumpParserFactory {
    private static DumpParserFactory instance = null;
    
    /** singleton private constructor */
    private DumpParserFactory() {
    }
    
    public static DumpParserFactory get() {
        if(instance == null) {
            instance = new DumpParserFactory();
        }
        
        return(instance);
    }
    
    public DumpParser getDumpParserForVersion(String javaVersion, String dumpFile, Map threadStore) {
        // currently only one parser supported.
        return(new JDK14Parser(dumpFile, threadStore));
    }
    
}
