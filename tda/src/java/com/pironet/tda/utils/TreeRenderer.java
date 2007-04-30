/*
 * TreeRenderer.java
 *
 * Created on 30. April 2007, 10:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.pironet.tda.utils;

import com.pironet.tda.Category;
import com.pironet.tda.HistogramInfo;
import com.pironet.tda.Logfile;
import com.pironet.tda.TDA;
import com.pironet.tda.ThreadInfo;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * adds icons to tda root tree
 * @author irockel
 */
public class TreeRenderer extends DefaultTreeCellRenderer {

    public TreeRenderer() {
       // empty constructor
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                        boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (leaf && isCategory(value)) {
            setIcon(getIconFromCategory(value));
        } else if (leaf && isThreadInfo(value)) {
            setIcon(TDA.createImageIcon("Thread.gif"));
        } else if(leaf && isHistogramInfo(value)) {
            setIcon(TDA.createImageIcon("Histogram.gif"));
        } else if(!leaf) {
            if(((DefaultMutableTreeNode) value).isRoot() || isLogfile(value)) {
                setIcon(TDA.createImageIcon("Root.gif"));
            } else if(isThreadInfo(value)) {
                setIcon(TDA.createImageIcon("Monitor.gif"));
            } else {
                setIcon(TDA.createImageIcon("ThreadDump.gif"));
            }
        }

        return this;
    }

    protected boolean isCategory(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        return(node.getUserObject() instanceof Category);
    }
        
    protected Icon getIconFromCategory(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Category nodeInfo = (Category) node.getUserObject();
        
        return(nodeInfo.getIcon());
    }    

    private boolean isHistogramInfo(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        return(node.getUserObject() instanceof HistogramInfo);
    }
    
    private boolean isThreadInfo(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        return((node.getUserObject() instanceof ThreadInfo) && 
                !((ThreadInfo) node.getUserObject()).threadName.startsWith("Full"));
    }
    
    private boolean isLogfile(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        return(node.getUserObject() instanceof Logfile);
    }
}
