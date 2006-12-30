/*
 * FilterChecker.java
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
 * $Id: FilterChecker.java,v 1.4 2006-12-30 10:03:13 irockel Exp $
 */
package com.pironet.tda.filter;

import com.pironet.tda.ThreadInfo;
import com.pironet.tda.utils.PrefManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ListModel;

/**
 * has a list of filters and checks for a given thread if it matches any of the filters.
 * @author irockel
 */
public class FilterChecker {
    /**
     * filters checked by this checker instance.
     */
    private Map filters = null;
    
    private static Map generalFilters = null;
    
    /** 
     * Creates a new instance of FilterChecker 
     */
    public FilterChecker(Map checkFilters) {
        filters = checkFilters;
    }
    
    /**
     * return a filter checker for all general filters
     */
    public static FilterChecker getFilterChecker() {
        if(generalFilters == null) {
            generalFilters = new HashMap();
            ListModel filters = PrefManager.get().getFilters();
            for(int i = 0; i < filters.getSize(); i++) {
                Filter currentFilter = (Filter) filters.getElementAt(i);
                if(currentFilter.isEnabled() && currentFilter.isGeneralFilter()) {
                    generalFilters.put(currentFilter.getName(), currentFilter);
                }
            }
        }
        
        return(new FilterChecker(generalFilters));
    }
    
    /**
     * add the given filter to the lists of filters
     */
    public void addToFilters(Filter filter) {
        if(filters == null) {
            filters = new HashMap();
        }
        
        filters.put(filter.getName(), filter);
    }
    
    /**
     * checks if the given thread info passes the filters of
     * this filter checker instance
     */
    public boolean check(ThreadInfo ti) {
        boolean result = true;
        Iterator filterIter = filters.values().iterator();
        while(result && filterIter.hasNext()) {
            Filter filter = (Filter) filterIter.next();
            result = filter.matches(ti);
        }
        return(result);
    }
    
}
