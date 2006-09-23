/*
 * Category.java
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
 * $Id: Category.java,v 1.2 2006-09-23 16:33:20 irockel Exp $
 */

package com.pironet.tda;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This class represent a category node.
 *
 * @author irockel
 */
public class Category {
    String name = null;
    
    DefaultMutableTreeNode rootNode = null;
    
    JScrollPane lastView = null;
    
    JTree catTree = null;
    
    /** 
     * Creates a new instance of Category 
     */
    public Category(String name) {
        setName(name);
    }
    
    public void setName(String value) {
       name = value;
    }
    
    public String getName() {
        return(name);
    }
        
    /**
     * fetch the tree with all threads belonging to this category
     */
    public JTree getCatTree(TreeSelectionListener listener) {
        if(catTree == null) {
            catTree = new JTree(rootNode);
            catTree.setRootVisible(false);
            catTree.addTreeSelectionListener(listener);
        }
        
        return(catTree);
    }
    
    public String toString() {
        return(getName());
    }
    
    /**
     * add the passed node to the category tree
     */
    public void addToCatTree(DefaultMutableTreeNode node) {
        if(rootNode == null) {
            rootNode = new DefaultMutableTreeNode("root");
        }
        rootNode.add(node);
    }
    
    public void setLastView(JScrollPane view) {
        lastView = view;
    }
    
    public JScrollPane getLastView() {
        return(lastView);
    }
}
