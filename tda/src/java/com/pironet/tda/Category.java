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
 * $Id: Category.java,v 1.9 2007-04-30 10:57:59 irockel Exp $
 */

package com.pironet.tda;

import com.pironet.tda.filter.FilterChecker;
import com.pironet.tda.utils.PrefManager;
import com.pironet.tda.utils.TreeRenderer;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.Icon;
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
    private DefaultMutableTreeNode filteredRootNode = null;
    
    private JScrollPane lastView = null;
    
    private JTree filteredCatTree;
    
    private FilterChecker filterChecker = null;
    
    private boolean filterEnabled = true;
    
    private Icon icon = null;
        
    /** 
     * Creates a new instance of Category 
     */
    public Category(String name, Icon treeIcon) {
        this(name, treeIcon, true);
    }

    /** 
     * Creates a new instance of Category 
     */
    public Category(String name, Icon treeIcon, boolean filtering) {
        setName(name);
        filterEnabled = filtering;
        icon = treeIcon;
    }
    
    public void setName(String value) {
       name = value;
    }
    
    public String getName() {
        return(name);
    }
    
    public Icon getIcon() {
        return(icon);
    }
        
    private long lastUpdated = -1;
    
    /**
     * return category tree with filtered child nodes
     */
    public JTree getCatTree(TreeSelectionListener listener) {
        if(filterEnabled && (filteredCatTree == null) || (getLastUpdated() < PrefManager.get().getFiltersLastChanged())) {
            // first refresh filter checker with current filters
            setFilterChecker(FilterChecker.getFilterChecker());
            
            // FIXME: what to do with special filters?
            
            // apply new filter settings.
            filteredCatTree = filterTree(rootNode);
            filteredCatTree.setCellRenderer(new TreeRenderer());
            filteredCatTree.setRootVisible(false);
            filteredCatTree.addTreeSelectionListener(listener);
            setLastUpdated();
        } else if (!filterEnabled && (filteredCatTree == null) || (getLastUpdated() < PrefManager.get().getFiltersLastChanged())) {
            filteredCatTree = new JTree(rootNode);
            filteredCatTree.setCellRenderer(new TreeRenderer());
            filteredCatTree.setRootVisible(false);
            filteredCatTree.addTreeSelectionListener(listener);            
        }
        return(filteredCatTree);
    }
    
    /**
     * return amount of filtered nodes
     */
    public int howManyFiltered() {
       return(filteredRootNode != null && rootNode != null ? rootNode.getChildCount() - filteredRootNode.getChildCount() : 0); 
    }
    
    public int showing() {
       return(filteredRootNode != null ? filteredRootNode.getChildCount() : 0);
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
        //System.out.println("lastUpdated=" + new Date(getLastUpdated()) + " // filterChanged=" + new Date(PrefManager.get().getFiltersLastChanged()));
        if(getLastUpdated() < PrefManager.get().getFiltersLastChanged()) {
            // reset view as changed filters need to be applied.
            lastView = null;
        }
        return(lastView);
    }

    private long getLastUpdated() {
        return lastUpdated;
    }

    private void setLastUpdated() {
        this.lastUpdated = System.currentTimeMillis();
    }

    private JTree filterTree(DefaultMutableTreeNode rootNode) {
        filteredRootNode = new DefaultMutableTreeNode("root");
        if(rootNode != null) {
            Enumeration enumChilds = rootNode.children();
            for(int i = 0; i < rootNode.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                if(getFilterChecker().recheck((ThreadInfo) childNode.getUserObject())) {
                    // node needs to be cloned as it is otherwise removed from rootNode.
                    DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(childNode.getUserObject());    
                    filteredRootNode.add(newChild);
                }
            }
        }
        return new JTree(filteredRootNode);
    }

    public FilterChecker getFilterChecker() {
        if(filterChecker == null) {
            setFilterChecker(FilterChecker.getFilterChecker());
        }
        return filterChecker;
    }

    private void setFilterChecker(FilterChecker filterChecker) {
        this.filterChecker = filterChecker;
    }
}
