/*
 * MonitorMap.java
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
 * $Id: MonitorMap.java,v 1.2 2006-02-20 09:47:43 irockel Exp $
 */

package com.pironet.tda;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

/**
 * map for saving monitor-thread relation in a thread dump.
 *
 * @author irockel
 */
public class MonitorMap {
    
    public final int LOCK_THREAD_POS = 0;
    public final int WAIT_THREAD_POS = 1;
    public final int SLEEP_THREAD_POS = 2;
    
    private Map monitorMap = null;    
    
    /** 
     * Creates a new instance of MonitorMap 
     */
    public MonitorMap() {
    }
    
    public void addToMonitorMap(String key, Set[] objectSet) {
        if(monitorMap == null) {
            monitorMap = new HashMap();
        }
        
        monitorMap.put(key, objectSet);
    }
    
    public boolean hasInMonitorMap(String key) {
        return(monitorMap != null && monitorMap.containsKey(key));
    }
    
    public Set[] getFromMonitorMap(String key) {
        return(monitorMap != null && hasInMonitorMap(key)? (Set[])monitorMap.get(key) : null);
    }
    
    public void addWaitToMonitor(String key, String[] waitThread) {
        addToMonitorValue(key, WAIT_THREAD_POS, waitThread);
    }
    
    public void addLockToMonitor(String key, String[] lockThread) {
        addToMonitorValue(key, LOCK_THREAD_POS, lockThread);
    }
    
    public void addSleepToMonitor(String key, String[] sleepThread) {
        addToMonitorValue(key, SLEEP_THREAD_POS, sleepThread);
    }
    
    private void addToMonitorValue(String key, int pos, String[] thread) {
        Set[] objectSet = null;

        if(hasInMonitorMap(key)) {
            objectSet = getFromMonitorMap(key);
        } else {
            objectSet = new HashSet[3];
            objectSet[0] = new HashSet();
            objectSet[1] = new HashSet();
            objectSet[2] = new HashSet();
        }
        
        objectSet[pos].add(thread);
        addToMonitorMap(key, objectSet);
    }
    
    public void parseAndAddThread(String line, String threadTitle, String currentThread) {
        String monitor = line.substring(line.indexOf('<'));
        if(line.trim().startsWith("- waiting to lock")) {
            addWaitToMonitor(monitor, new String[] {threadTitle, currentThread});
        } else if (line.trim().startsWith("- waiting on")) {
            addSleepToMonitor(monitor, new String[] {threadTitle, currentThread});
        } else {
            addLockToMonitor(monitor, new String[] {threadTitle, currentThread});
        }
    }
    
    public Iterator iterOfKeys() {
        return(monitorMap == null? null : monitorMap.keySet().iterator());
    }
    
    public int size() {
        return(monitorMap == null? 0: monitorMap.size());
    }
    
    
}
