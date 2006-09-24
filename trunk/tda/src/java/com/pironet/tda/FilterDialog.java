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
 * $Id: FilterDialog.java,v 1.1 2006-09-24 15:09:48 irockel Exp $
 */

package com.pironet.tda;

import com.pironet.tda.utils.PrefManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 *
 * @author irockel
 */
public class FilterDialog extends JDialog {
    private FilterPanel filterPanel;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
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
        filterPanel = new FilterPanel();
        getContentPane().add(filterPanel,BorderLayout.CENTER);
        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
        
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setEnabled(true);
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
    
    class FilterPanel extends JPanel {
        JTextField maxLinesField;
        JTextField bufferField;
        JCheckBox forceLoggcLoading;
        JCheckBox showHotspotClasses;
        
        public FilterPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(580, 190));
            
        }
    }
        
    //Must be called from the event-dispatching thread.
    public void resetFocus() {
    }    
}
