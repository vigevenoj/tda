/*
 * HistogramTableModel.java
 *
 * Thread Dump Analysis Tool, parses Thread Dump input and displays it as tree
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
 * $Id: HistogramTableModel.java,v 1.1 2006-03-01 19:19:38 irockel Exp $
 */
package com.pironet.tda.utils;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * Provides table data model for the display of class histograms.
 *
 * @author irockel
 */
public class HistogramTableModel extends AbstractTableModel {
    private static int DEFINED_ROWS = 3;
    
    private Vector elements = new Vector();

    private String[] columnNames = {"class name",
                                    "instance count",
                                    "#bytes"};
    
    /** Creates a new instance of HistogramTableModel */
    public HistogramTableModel() {
    }
    
    public void addEntry(String className, int instanceCount, int bytes) {
        elements.addElement(new Entry(className, instanceCount, bytes));
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0 : {
                return ((Entry) elements.elementAt(rowIndex)).className;
            }
            case 1 : {
                return new Integer(((Entry) elements.elementAt(rowIndex)).bytes);
            } 
            case 2 : {
                return new Integer(((Entry) elements.elementAt(rowIndex)).instanceCount);
            }
        }
        return null;
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public int getRowCount() {
        return elements.size();
    }

    public int getColumnCount() {
        return DEFINED_ROWS;
    }
    
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public class Entry {
        private String className;
        private int instanceCount;
        private int bytes;
        
        public Entry(String className, int instanceCount, int bytes) {
            if(className.trim().equals("[I")) {
                this.className = "int[]";
            } else if (className.trim().equals("[B")) {
                this.className = "byte[]";
            } else if (className.trim().equals("[C")) {
                this.className = "char[]";
            } else if (className.trim().equals("[L")) {
                this.className = "long[]";
            } else {
                this.className = className;
            }
            this.instanceCount = instanceCount;
            this.bytes = bytes;
        }
    }
}
