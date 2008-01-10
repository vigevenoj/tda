/*
 * DateMatcher.java
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
 * $Id: DateMatcher.java,v 1.1 2008-01-10 17:16:08 irockel Exp $
 */
package com.pironet.tda.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JOptionPane;

/**
 *
 * @author irockel
 */
public class DateMatcher {
    private Pattern regexPattern;
    private boolean patternError;
    private Matcher matched = null;
    
    public DateMatcher() {
        // set date parsing pattern.
        if((PrefManager.get().getDateParsingRegex() != null) && !PrefManager.get().getDateParsingRegex().trim().equals("")) {
            try {
                regexPattern = Pattern.compile(PrefManager.get().getDateParsingRegex().trim());
                setPatternError(false);
            } catch (PatternSyntaxException pe) {
                showErrorPane(pe.getMessage());
            }
        }         
    }
    
    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(Pattern regexPattern) {
        this.regexPattern = regexPattern;
    }

    public boolean isPatternError() {
        return patternError;
    }

    public void setPatternError(boolean patternError) {
        this.patternError = patternError;
    }
    
    public Matcher checkForDateMatch(String line) {        
        try {
            if(getRegexPattern() == null) {
                setRegexPattern(Pattern.compile(PrefManager.get().getDateParsingRegex().trim()));
            }
            Matcher m = getRegexPattern().matcher(line);
            if (m.matches()) {
                matched = m;
            }
        } catch (Exception ex) {
            showErrorPane(ex.getMessage());
        }
        
        return(matched);
    }
    
    public Matcher getLastMatch() {
        return(matched);
    }
    
    private void showErrorPane(String message) {
        JOptionPane.showMessageDialog(null,
                "Error during parsing line for timestamp regular expression!\n" +
                "Please check regular expression in your preferences. Deactivating\n" +
                "parsing for the rest of the file! Error Message is " + message + " \n",
                "Error during Parsing", JOptionPane.ERROR_MESSAGE);

        setPatternError(true);
    }
}
