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
 * $Id: FilterChecker.java,v 1.3 2006-12-28 17:34:21 irockel Exp $
 */
package com.pironet.tda.filter;

import com.pironet.tda.ThreadInfo;
import com.pironet.tda.utils.PrefManager;
import java.util.HashMap;
import java.util.Map;

/**
 * has a list of filters and checks for a given thread if it matches any of the filters.
 * @author irockel
 */
public class FilterChecker {
    /**
     * filters checked by this checker instance.
     */
    private Map filters = null;
    
    /** 
     * Creates a new instance of FilterChecker 
     */
    public FilterChecker(Map checkFilters) {
        filters = checkFilters;
    }
    
    /**
     * return a filter checker for all general filters
     */
    public static FilterChecker getGeneralFilterChecker() {
        Map filters = null; //PrefManager.get().getGeneralFilters();
        
        return(new FilterChecker(filters));
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
        return(true);
    }
    
}
