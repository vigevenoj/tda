/*
 * Filter.java
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
 * $Id: Filter.java,v 1.3 2006-11-26 16:31:15 irockel Exp $
 */
package com.pironet.tda.filter;

import java.util.regex.Pattern;

/**
 * represents a filter for filtering threads or monitors to display
 *
 * @author irockel
 */
public class Filter {
    /**
     * name of this filter, just something describing for this filter
     */
    private String name = null;
    
    /**
     * a regular expression of the filter
     */
    private String filterExpression = null;
    
    /**
     * the precompiled pattern.
     */
    private Pattern filterExpressionPattern = null;
    
    /**
     * true, if filter is a general filter, which should be applied
     * to all thread infos
     */
    private boolean generalFilter = false;
    
    /**
     * specifies if this filter is a exclusion filter
     */
    private boolean exclusionFilter = false;
    
    /**
     * specifies the filter rule which the filter expression applies to
     */
    private int filterRule = 0;
    
    /**
     * empty default constructor
     */
    public Filter() {
    }
        
    /** 
     * Creates a new instance of Filter 
     * @param name the name of the filter
     * @param regEx the reg ex of the filter
     * @param gf true, if filter is general filter
     */
    public Filter(String name, String regEx, int fr, boolean gf, boolean exf) {
        setName(name);
        setFilterExpression(regEx);
        setGeneralFilter(gf);
        setExclusionFilter(exf);
        setFilterRule(fr);
    }
    
    /**
     * set the name of this filter
     */
    public void setName(String value) {
        name = value;
    }
    
    /**
     * get filter name
     */
    public String getName() {
        return(name);
    }
    
    /**
     * get the filter expression as string
     */
    public String getFilterExpression() {
        return(filterExpression);
    }
    
    public void setFilterExpression(String regEx) {
        filterExpression = regEx;
    }
    
    /**
     * get the filter expression as precompiled pattern
     */
    public Pattern getFilterExpressionPattern() {
        if(filterExpressionPattern == null) {
            filterExpressionPattern = Pattern.compile(getFilterExpression());
        }
        
        return(filterExpressionPattern);
    }
    
    /**
     * set general filter flag
     */
    public void setGeneralFilter(boolean value) {
        generalFilter = value;
    }
    
    /**
     * @return true, if filter is a general filter
     */
    public boolean isGeneralFilter() {
        return(generalFilter);
    }
    
    /**
     * set exclusion filter flag
     */
    public void setExclusionFilter(boolean value) {
        exclusionFilter = value;
    }
    
    /**
     * @return true, if filter is a exclusion filter
     */
    public boolean isExclusionFilter() {
        return(exclusionFilter);
    }

    public int getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(int filterRule) {
        this.filterRule = filterRule;
    }
    
    public String toString() {
        return (getName() + (isGeneralFilter() ? " (general) " : ""));
    }
}
