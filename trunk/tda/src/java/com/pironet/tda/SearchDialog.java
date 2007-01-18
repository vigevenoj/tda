/*
 * SearchDialog.java
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
 * $Id: SearchDialog.java,v 1.8 2007-01-18 09:35:32 irockel Exp $
 */

package com.pironet.tda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.Position;
import javax.swing.tree.TreePath;

/**
 *
 * @author irockel
 */
public class SearchDialog extends JDialog
        implements ActionListener {
    
    private static String SEARCH = "search";
    private static String CANCEL = "cancel";
        
    private JTextField searchField;
    
    private JTree searchTree;
    
    private JScrollPane view;
    
    public SearchDialog(JFrame owner, JTree tree, JScrollPane view) {
        super(owner, "Search this category... ");
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        //Create everything.
        searchField = new JTextField(10);
        searchField.setActionCommand(SEARCH);
        searchField.addActionListener(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel label = new JLabel("Enter search string: ");
        label.setLabelFor(searchField);
        
        searchTree = tree;
        
        this.view = view;
        
        JComponent buttonPane = createButtonPanel();
        
        //Lay out everything.
        JPanel textPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        textPane.add(label);
        textPane.add(searchField);
        
        add(textPane);
        add(buttonPane);
    }
    
    protected JComponent createButtonPanel() {
        JPanel p = new JPanel(new GridLayout(0,1));
        JButton searchButton = new JButton("Search");
        
        searchButton.setActionCommand(SEARCH);
        searchButton.addActionListener(this);
        
        p.add(searchButton);
        
        return p;
    }
    
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        if (SEARCH.equals(cmd)) {
            TreePath searchPath = searchTree.getNextMatch(searchField.getText(),searchTree.getRowCount()-1,Position.Bias.Forward);
            
            //searchTree.expandRow(searchTree.getRowCount()+1);
            if(searchPath != null) {
                searchTree.setExpandsSelectedPaths(true);
                searchTree.setSelectionPath(searchPath);
                searchTree.repaint();
                Rectangle view = searchTree.getPathBounds(searchPath);
                this.view.scrollRectToVisible(view);
                dispose();
                searchTree.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(getOwner(),
                        searchField.getText() + " not found!",
                        "Search Error",
                        JOptionPane.ERROR_MESSAGE);
                resetFocus();
            }
        }
    }
    
    //Must be called from the event-dispatching thread.
    protected void resetFocus() {
        searchField.requestFocusInWindow();
    }
    
    public void reset() {
    }
}
