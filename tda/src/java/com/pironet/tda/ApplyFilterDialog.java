/*
 * ApplyFilterDialog.java
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
 * $Id: ApplyFilterDialog.java,v 1.1 2007-01-18 09:35:32 irockel Exp $
 */
package com.pironet.tda;

import com.pironet.tda.filter.Filter;
import com.pironet.tda.utils.PrefManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author irockel
 */
public class ApplyFilterDialog extends JDialog {
    FilterPanel filterPanel = null;
    Category cat = null;

    private JButton closeButton;

    private JPanel buttonPanel;
    
    /**
     * Creates a new instance of ApplyFilterDialog
     */
    public ApplyFilterDialog(JFrame owner, Category cat) {
        super(owner, "Apply Special Filter to Category");
        getContentPane().setLayout(new BorderLayout());
        this.cat = cat;
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
                getOwner().setEnabled(true);
                dispose();
            }
        });
        reset();
    }

    public void reset() {
        getRootPane().setDefaultButton(closeButton);
    }

    
    class FilterPanel extends JPanel implements ActionListener, ListSelectionListener  {
        JButton addButton = null;
        JButton removeButton = null;
        
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
            removeButton.setEnabled(false);
            
            addButton.addActionListener(this);
            removeButton.addActionListener(this);
            
            buttonFlow.add(innerButtonPanel);
            
            add(buttonFlow,BorderLayout.EAST);
            setPreferredSize(new Dimension(380, 290));
            
            filterList = new JList();
            System.out.println("cat " + cat);
            Iterator filterIter = cat.getFilterChecker().iterOfFilters();
            DefaultListModel model = new DefaultListModel();
            while(filterIter.hasNext()) {
                Filter filter = (Filter) filterIter.next();
                model.addElement(filter);
            }
            filterList.setModel(model);
            scrollPane = new JScrollPane(filterList);
            filterList.addListSelectionListener(this);
            
            add(scrollPane,BorderLayout.CENTER);
            
        }
                
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            
            if ("Add".equals(cmd)) {
                //createFilterDialog("Add Filter", true, -1);
            } else if("Remove".equals(cmd)) {
                removeFilter();
            }
        }
        
        private void removeFilter() {
            if(JOptionPane.showConfirmDialog(null, "Are you sure, you want to remove the selected filter?", "Confirm Remove",  
                    JOptionPane.YES_NO_OPTION) == 0) {
                ((DefaultListModel) filterList.getModel()).removeElementAt(filterList.getSelectedIndex());
            }
        }
        
        private void createFilterDialog(String title, boolean isAdd, int selectedIndex) {
            EditFilterDialog fDiag = new EditFilterDialog(owner, title, filterList, isAdd);
            fDiag.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            owner.setEnabled(false);
            
            //Display the window.
            fDiag.reset();
            fDiag.pack();
            fDiag.setLocationRelativeTo(getOwner());
            fDiag.setVisible(true);
        }
        
        public void valueChanged(ListSelectionEvent e) {
            if(filterList.getSelectedIndex() >= 0) {
                removeButton.setEnabled(true);
            } else {
                removeButton.setEnabled(false);
            }
        }
    }
    
}
