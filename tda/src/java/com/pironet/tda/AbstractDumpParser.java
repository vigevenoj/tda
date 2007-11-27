/*
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
 * $Id: AbstractDumpParser.java,v 1.1 2007-11-27 13:19:19 irockel Exp $
 */
package com.pironet.tda;

import com.pironet.tda.utils.IconFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * abstract dump parser class, contains all generic dump parser
 * stuff, which doesn't have any jdk specific parsing code.
 * 
 * All Dump Parser should extend from this class as it already provides
 * a basic parsing interface.
 * 
 * @author irockel
 */
public abstract class AbstractDumpParser implements DumpParser {
    private BufferedReader bis = null;


    /**
     * strip the dump string from a given path
     * @param path the treepath to check
     * @return dump string, if proper tree path, null otherwise.
     */
    protected String getDumpStringFromTreePath(TreePath path) {
        String[] elems = path.toString().split(",");
        if (elems.length > 1) {
            return (elems[1].substring(0, elems[1].lastIndexOf(']')).trim());
        } else {
            return null;
        }
    }
    
    /**
     * find long running threads.
     * @param root the root node to use for the result.
     * @param dumpStore the dump store to use
     * @param paths paths to the dumps to check
     * @param minOccurence the min occurrence of a long running thread
     * @param regex regex to be applied to the thread titles.
     */
    public void findLongRunningThreads(DefaultMutableTreeNode root, Map dumpStore, TreePath[] paths, int minOccurence, String regex) {
        diffDumps("Long running thread detection", root, dumpStore, paths, minOccurence, regex);
    }
    
    /**
     * merge the given dumps.
     * @param root the root node to use for the result.
     * @param dumpStore the dump store tu use
     * @param dumps paths to the dumps to check
     * @param minOccurence the min occurrence of a long running thread
     * @param regex regex to be applied to the thread titles.
     */
    public void mergeDumps(DefaultMutableTreeNode root, Map dumpStore, TreePath[] dumps, int minOccurence, String regex) {
        diffDumps("Merge", root, dumpStore, dumps, minOccurence, regex);
    }
    
    protected void diffDumps(String prefix, DefaultMutableTreeNode root, Map dumpStore, TreePath[] dumps, int minOccurence, String regex) {
        Vector keys = new Vector(dumps.length);        
        
        for(int i = 0; i < dumps.length; i++) {
            keys.add(getDumpStringFromTreePath(dumps[i]));
        }
           
        String info = prefix + " between " + keys.get(0) + " and " + keys.get(keys.size()-1); 
        DefaultMutableTreeNode catMerge = new DefaultMutableTreeNode(new Category(info, IconFactory.DIFF_DUMPS));
        root.add(catMerge);
        
        if(dumpStore.get(keys.get(0)) != null) {
            Iterator dumpIter = ((Map) dumpStore.get(keys.get(0))).keySet().iterator();
            
            while(dumpIter.hasNext()) {
                String threadKey = ((String) dumpIter.next()).trim();
                int occurence = 0;
                
                if(regex == null || regex.equals("") || threadKey.matches(regex)) {
                    for(int i = 1; i < dumps.length; i++) {
                        Map threads = (Map) dumpStore.get(keys.get(i));
                        if(threads.containsKey(threadKey)) {
                            occurence++;
                        }
                    }
                
                    if(occurence >= (minOccurence-1)) {
                        StringBuffer content = new StringBuffer("<body bgcolor=\"ffffff\"><pre><font size=").append(TDA.getFontSizeModifier(-1)).append(">").append((String) keys.get(0)).append("\n\n").append((String) ((Map) dumpStore.get(keys.get(0))).get(threadKey));
                        for(int i = 1; i < dumps.length; i++) {
                            if(((Map)dumpStore.get(keys.get(i))).containsKey(threadKey)) {
                                content.append("\n\n---------------------------------\n\n");
                                content.append(keys.get(i));
                                content.append("\n\n");
                                content.append((String) ((Map)dumpStore.get(keys.get(i))).get(threadKey));
                            }
                        }
                        createCategoryNode(catMerge, threadKey, null, content, 0);
                    }
                }
            }
        }
        
    }
    
    /**
     * create a tree node with the provided information
     * @param top the parent node the new node should be added to.
     * @param title the title of the new node
     * @param info the info part of the new node
     * @param content the content part of the new node
     * @see ThreadInfo 
     */
    protected void createNode(DefaultMutableTreeNode top, String title, String info, String content, int lineCount) {
        DefaultMutableTreeNode threadInfo = null;
        threadInfo = new DefaultMutableTreeNode(new ThreadInfo(title, info, content, lineCount));
        top.add(threadInfo);
    }
    
    /**
     * create a node for a category (categories are "Monitors", "Threads waiting", e.g.). A ThreadInfo
     * instance will be created with the passed information.
     * @param category the category the node should be added to.
     * @param title the title of the new node
     * @param info the info part of the new node
     * @param content the content part of the new node
     * @see ThreadInfo 
     */
    protected void createCategoryNode(DefaultMutableTreeNode category, String title, StringBuffer info, StringBuffer content, int lineCount) {
        DefaultMutableTreeNode threadInfo = null;
        threadInfo = new DefaultMutableTreeNode(new ThreadInfo(title, info != null ? info.toString() : null, content.toString(), lineCount));
        ((Category)category.getUserObject()).addToCatTree(threadInfo);
    }

    /**
     * get the stream to parse
     * @return stream or null if none is set up
     */
    protected BufferedReader getBis() {
        return bis;
    }

    /**
     * set the stream to parse
     * @param bis the stream
     */
    protected void setBis(BufferedReader bis) {
        this.bis = bis;
    }
    
    /**
     * close this dump parser, also closes the passed dump stream
     */
    public void close() throws IOException {
        if(getBis() != null) {
            getBis().close();
        }        
    }

}
