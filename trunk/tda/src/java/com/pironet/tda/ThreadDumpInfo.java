/*
 * ThreadDumpInfo.java
 *
 * Created on 9. Februar 2006, 13:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.pironet.tda;

/**
 *
 * @author irockel
 */
public class ThreadDumpInfo {
    public String threadName;
    public String content;
    
    public ThreadDumpInfo(String name, String content) {
        threadName = name;
        this.content = content;
    }
    
    public String toString() {
        return threadName;
    }
}
