/*
 * ThreadsTableModel.java
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
 * $Id: ThreadsTableModel.java,v 1.2 2008-01-08 14:12:07 irockel Exp $
 */
package com.pironet.tda.utils;

import com.pironet.tda.ThreadInfo;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * table model for displaying thread overview.
 * 
 * @author irockel
 */
public class ThreadsTableModel extends AbstractTableModel {
    
    private Vector elements;
    
    private String[] columnNames = null;
    
    /**
     * 
     * @param root
     */
    public ThreadsTableModel(DefaultMutableTreeNode rootNode) {
        // transform child nodes in proper vector.
        if(rootNode != null) {
            elements = new Vector();
            for(int i = 0; i < rootNode.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                elements.add(childNode.getUserObject());
                    ThreadInfo ti = (ThreadInfo) childNode.getUserObject();
                if(columnNames == null) {
                    if(ti.getTokens().length > 3) {
                        columnNames = new String[] {"Name", "Type", "Prio", "Thread-ID", "Native-ID", "State", "Address Range"};
                    } else {
                        columnNames = new String[] {"Name", "Native-ID", "State"};
                    }
                }
            }
        }
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public int getRowCount() {
        return(elements.size());
    }

    public int getColumnCount() {
        return(columnNames.length);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ThreadInfo ti = ((ThreadInfo) elements.elementAt(rowIndex));
        String[] columns = ti.getTokens();
        if(getColumnCount() > 3) {
            if (columnIndex > 1 && columnIndex < 5) {
                return new Integer(columns[columnIndex]);
            } else {
                return columns[columnIndex];
            }
        } else {
            if (columnIndex == 1) {
                return new Integer(columns[columnIndex]);
            } else {
                return columns[columnIndex];
            }
            
        }
    }
    
    /**
     * get the thread info object at the specified line
     * @param rowIndex the row index
     * @return thread info object at this line.
     */
    public ThreadInfo getInfoObjectAtRow(int rowIndex) {
        return((ThreadInfo) elements.get(rowIndex));
    }
    
    /**
     * @inherited
     */
    public Class getColumnClass(int columnIndex) {
        if(columnIndex > 1 && columnIndex < 5) {
            return Integer.class;
        } else {
            return String.class;
        }
    }

}
