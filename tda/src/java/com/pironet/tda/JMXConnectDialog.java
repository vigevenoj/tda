/*
 * JMXConnectDialog.java
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
 * $Id: JMXConnectDialog.java,v 1.1 2006-06-01 20:41:32 irockel Exp $
 */
package com.pironet.tda;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * provide a dialog for setting up a JMX connection to remote VM
 *
 * @author irockel
 */
public class JMXConnectDialog extends JDialog {
    JButton okButton;
    JButton cancelButton;
    JTextField connectInfo;
    JFrame frame;
    DefaultMutableTreeNode rootNode;
    TDA backRef;
    
    /** 
     * Creates a new instance of JMXConnectDialog 
     */
    public JMXConnectDialog(TDA backRef, JFrame owner, DefaultMutableTreeNode node) {
        super(owner, "Open a JMX Connection");
        frame = owner;
        rootNode = node;
        this.backRef = backRef;
        getContentPane().setLayout(new BorderLayout());
        
        add(createMainPanel());
    }
    
    /**
     * create the main panel of the dialog, containing all sub-panels
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel("Enter JMX Connection (<server>:<port> e.g. localhost:1090)"),
                BorderLayout.NORTH);
        connectInfo = new JTextField(50);
        mainPanel.add(connectInfo, BorderLayout.CENTER);
        
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        return (mainPanel);
    }
    
    /**
     * create button sub panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setEnabled(true);
                backRef.addJMXConnection(new RemoteConnection("Connection to ", connectInfo.getText()));
                dispose();
            }
        });
        
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setEnabled(true);
                dispose();
            }
        });
        
        return(buttonPanel);
    }    
}
