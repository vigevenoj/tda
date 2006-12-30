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
 * $Id: Category.java,v 1.3 2006-12-30 10:03:12 irockel Exp $
 */

package com.pironet.tda;

import com.pironet.tda.filter.FilterChecker;
import com.pironet.tda.utils.PrefManager;
import java.util.Enumeration;
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
    private String name = null;
    
    private DefaultMutableTreeNode rootNode = null;
    
    private JScrollPane lastView = null;
    
    private JTree filteredCatTree;
    
    private FilterChecker filterChecker = null;
    
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
        
    private long lastUpdated = -1;
    
    /**
     * return category tree with filtered child nodes
     */
    public JTree getCatTree(TreeSelectionListener listener) {
        if((filteredCatTree == null) || (getLastUpdated() < PrefManager.get().getFiltersLastChanged())) {
            // first refresh filter checker with current filters
            setFilterChecker(FilterChecker.getFilterChecker());
            
            // FIXME: what to do with special filters?
            
            // apply new filter settings.
            filteredCatTree = filterTree(rootNode);
            filteredCatTree.setRootVisible(false);
            filteredCatTree.addTreeSelectionListener(listener);
            setLastUpdated();
        }
        return(filteredCatTree);
    }
    
    /**
     * return amount of filtered nodes
     */
    public int howManyFiltered() {
       return(0); 
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

    private long getLastUpdated() {
        return lastUpdated;
    }

    private void setLastUpdated() {
        this.lastUpdated = System.currentTimeMillis();
    }

    private JTree filterTree(DefaultMutableTreeNode rootNode) {
        System.out.println("Filtering...");
        DefaultMutableTreeNode filteredRootNode = new DefaultMutableTreeNode("root");
        if(rootNode != null) {
            Enumeration enumChilds = rootNode.children();
            while(enumChilds.hasMoreElements()) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) enumChilds.nextElement();
                if(getFilterChecker().check((ThreadInfo) childNode.getUserObject())) {
                    filteredRootNode.add(childNode);
                }
            }
        }
        return new JTree(filteredRootNode);
    }

    private FilterChecker getFilterChecker() {
        return filterChecker;
    }

    private void setFilterChecker(FilterChecker filterChecker) {
        this.filterChecker = filterChecker;
    }
}
