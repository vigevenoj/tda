/*
 * EditFilterDialog.java
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
 * $Id: EditFilterDialog.java,v 1.2 2006-11-01 18:44:32 irockel Exp $
 */

package com.pironet.tda;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * dialog for editing filters.
 * @author irockel
 */
public class EditFilterDialog extends JDialog {
    private SettingsPanel settingsPanel;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JFrame frame;
    
    /**
     * Creates a new instance of PreferencesDialog
     */
    public EditFilterDialog(JFrame owner, String frameTitle) {
        super(owner, frameTitle);
        frame = owner;
        getContentPane().setLayout(new BorderLayout());
        initPanel();        
    }
    
    private void initPanel() {
        settingsPanel = new SettingsPanel();
        getContentPane().add(settingsPanel,BorderLayout.CENTER);
        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setEnabled(true);
                dispose();
            }
        });
        
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setEnabled(true);
                dispose();
            }
        });
        reset();
    }
    
    public void reset() {
        getRootPane().setDefaultButton(okButton);
    }
    
    class SettingsPanel extends JPanel {
        JTextField regEx = null;
        JCheckBox generalFilter = null;
                
        public SettingsPanel() {
            setLayout(new BorderLayout());
            FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
            JPanel innerSettingsPanel = new JPanel(fl);
            
            regEx = new JTextField(30);
            innerSettingsPanel.add(new JLabel("Match Expression"));
            innerSettingsPanel.add(regEx);
            add(innerSettingsPanel, BorderLayout.CENTER);
            
            innerSettingsPanel = new JPanel(fl);
            innerSettingsPanel.add(new JLabel("Filter is a general filter"));
            innerSettingsPanel.add(generalFilter = new JCheckBox());
            add(innerSettingsPanel, BorderLayout.SOUTH);
            
        }
    }        
}

