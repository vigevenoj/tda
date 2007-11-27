/*
 * DumpParserFactory.java
 *
 * This file is part of TDA - Thread Dump Analysis Tool.
 *
 * TDA is free software; you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * TDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with TDA; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: DumpParserFactory.java,v 1.7 2007-11-27 09:42:20 irockel Exp $
 */

package com.pironet.tda;

import com.pironet.tda.utils.PrefManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Provides Factory Interface for dump parsers.
 *
 * @author irockel
 */
public class DumpParserFactory {
    private static DumpParserFactory instance = null;
    
    private DumpParser currentDumpParser = null;
    
    /** singleton private constructor */
    private DumpParserFactory() {
    }
    
    public static DumpParserFactory get() {
        if(instance == null) {
            instance = new DumpParserFactory();
        }
        
        return(instance);
    }
    
    public DumpParser getDumpParserForLogfile(InputStream dumpFileStream, Map threadStore, boolean withCurrentTimeStamp) {
        BufferedReader bis = null;
        int readAheadLimit = PrefManager.get().getStreamResetBuffer();
        int lineCounter = 0;
        try {
            bis = new BufferedReader(new InputStreamReader(dumpFileStream));
            
            // reset current dump parser
            currentDumpParser = null;
            while (bis.ready() && (currentDumpParser == null)) {
                bis.mark(readAheadLimit);
                String line = bis.readLine();
                if(SunJDKParser.checkForSupportedThreadDump(line)) {
                    currentDumpParser = new SunJDKParser(bis, threadStore, lineCounter, withCurrentTimeStamp);
                } else if(BeaJDKParser.checkForSupportedThreadDump(line)) {
                    currentDumpParser = new BeaJDKParser(bis, threadStore, lineCounter);
                }
                lineCounter++;
            }
            //System.out.println("Selected Dump Parser: " + currentDumpParser.getClass().getName());
            if ((currentDumpParser != null) && (bis != null)) {
                bis.reset();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return currentDumpParser;
    }
    
    public DumpParser getCurrentDumpParser() {
        return(currentDumpParser);
    }
}
