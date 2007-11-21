/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pironet.tda.utils;

import java.util.Comparator;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author irockel
 */
public class MonitorComparator implements Comparator {

    public int compare(Object arg0, Object arg1) {
        if(arg0 instanceof DefaultMutableTreeNode && arg1 instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode) arg0;
            DefaultMutableTreeNode secondNode = (DefaultMutableTreeNode) arg1;
            return(secondNode.getChildCount() - firstNode.getChildCount());
        }
        return(0);
    }

}
