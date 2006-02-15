/*
 * DumpParserFactory.java
 *
 * Created on 9. Februar 2006, 13:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.pironet.tda;

import java.util.Map;

/**
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
