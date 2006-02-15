/*
 * DumpParser.java
 *
 * Created on 9. Februar 2006, 13:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.pironet.tda;

import java.io.IOException;
import java.util.Map;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author irockel
 */
public interface DumpParser {
    public boolean hasMoreDumps();
    
    public MutableTreeNode parseNext();
    
    public void close() throws IOException;
}
