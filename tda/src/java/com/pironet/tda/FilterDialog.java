/*
 * FilterDialog.java
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
 * $Id: FilterDialog.java,v 1.3 2006-11-01 18:44:32 irockel Exp $
 */

package com.pironet.tda;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * overview of all available filters
 * @author irockel
 */
public class FilterDialog extends JDialog {
    private FilterPanel filterPanel;
    private JPanel buttonPanel;
    private JButton closeButton;
    private JFrame frame;
    
    /**
     * Creates a new instance of PreferencesDialog
     */
    public FilterDialog(JFrame owner) {
        super(owner, "General Filter Settings");
        frame = owner;
        getContentPane().setLayout(new BorderLayout());
        initPanel();        
    }
    
    private void initPanel() {
        filterPanel = new FilterPanel((JFrame) this.getOwner());
        getContentPane().add(filterPanel,BorderLayout.CENTER);
        closeButton = new JButton("Close");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setEnabled(true);
                dispose();
            }
        });
        reset();
    }
    
    public void reset() {
        getRootPane().setDefaultButton(closeButton);
    }
    
    class FilterPanel extends JPanel implements ActionListener {
        JButton addButton = null;
        JButton removeButton = null;
        JButton editButton = null;
        
        JPanel buttonFlow = null;
        
        JList filterList = null;
        
        JScrollPane scrollPane = null;
        
        JFrame owner = null; 
        
        public FilterPanel(JFrame owner) {
            this.owner = owner;
            setLayout(new BorderLayout());
            
            buttonFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            add(Box.createVerticalStrut(5), BorderLayout.NORTH);
            add(Box.createHorizontalStrut(5),BorderLayout.WEST);
            JPanel innerButtonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            
            innerButtonPanel.add(addButton = new JButton("Add"));
            innerButtonPanel.add(removeButton = new JButton("Remove"));
            innerButtonPanel.add(editButton = new JButton("Edit"));
            addButton.addActionListener(this);
            removeButton.addActionListener(this);
            editButton.addActionListener(this);
            
            buttonFlow.add(innerButtonPanel);
            
            add(buttonFlow,BorderLayout.EAST);
            setPreferredSize(new Dimension(380, 290));
            
            filterList = new JList(new String[] {"Idle Threads Filter", "System Threads Filter"});
            scrollPane = new JScrollPane(filterList);
            
            add(scrollPane,BorderLayout.CENTER);
            
        }
        
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            
            if ("Add".equals(cmd)) {
                createFilterDialog("Add Filter");
            } else if("Edit".equals(cmd)) {
                createFilterDialog("Edit Filter");
            }
        }
        
        private void createFilterDialog(String title) {
            EditFilterDialog fDiag = new EditFilterDialog(owner, title);
            fDiag.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            owner.setEnabled(false);
            
            //Display the window.
            fDiag.reset();
            fDiag.pack();
            fDiag.setLocationRelativeTo(frame);
            fDiag.setVisible(true);
        }
    }
        
    //Must be called from the event-dispatching thread.
    public void resetFocus() {
    }    

}
