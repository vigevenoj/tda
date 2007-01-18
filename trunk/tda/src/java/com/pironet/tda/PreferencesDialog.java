/*
 * PreferencesDialog.java
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
 * $Id: PreferencesDialog.java,v 1.12 2007-01-18 09:35:32 irockel Exp $
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
public class PreferencesDialog extends JDialog {
    private JTabbedPane prefsPane;
    private GeneralPanel generalPanel;
    private RegExPanel regExPanel;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JFrame frame;
    
    /**
     * Creates a new instance of PreferencesDialog
     */
    public PreferencesDialog(JFrame owner) {
        super(owner, "Preferences");
        frame = owner;
        getContentPane().setLayout(new BorderLayout());
        initPanel();        
    }
    
    private void initPanel() {
        prefsPane = new JTabbedPane();
        generalPanel = new GeneralPanel();
        regExPanel = new RegExPanel();
        prefsPane.addTab("General", generalPanel);
        prefsPane.addTab("Date Parsing", regExPanel);
        getContentPane().add(prefsPane,BorderLayout.CENTER);
        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setEnabled(true);
                saveSettings();
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
        loadSettings();
    }
    
    private void loadSettings() {
        generalPanel.forceLoggcLoading.setSelected(PrefManager.get().getForceLoggcLoading());
        generalPanel.maxLinesField.setText(String.valueOf(PrefManager.get().getMaxRows()));
        generalPanel.bufferField.setText(String.valueOf(PrefManager.get().getStreamResetBuffer()));
        generalPanel.showHotspotClasses.setSelected(PrefManager.get().getShowHotspotClasses());
        
        DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
        String[] regexs = PrefManager.get().getDateParsingRegexs();
        for(int i = 0; i < regexs.length; i++) {
            boxModel.addElement(regexs[i]);
        }
        regExPanel.dateParsingRegexs.setModel(boxModel);
        regExPanel.dateParsingRegexs.setSelectedItem(PrefManager.get().getDateParsingRegex());
        
        regExPanel.isMillisTimeStamp.setSelected(PrefManager.get().getMillisTimeStamp());
    }
    
    private void saveSettings() {
        PrefManager.get().setForceLoggcLoading(generalPanel.forceLoggcLoading.isSelected());
        PrefManager.get().setMaxRows(Integer.parseInt(generalPanel.maxLinesField.getText()));
        PrefManager.get().setStreamResetBuffer(Integer.parseInt(generalPanel.bufferField.getText()));
        PrefManager.get().setShowHotspotClasses(generalPanel.showHotspotClasses.isSelected());
        PrefManager.get().setDateParsingRegex((String) regExPanel.dateParsingRegexs.getSelectedItem());
        PrefManager.get().setDateParsingRegexs(regExPanel.dateParsingRegexs.getModel());
        PrefManager.get().setMillisTimeStamp(regExPanel.isMillisTimeStamp.isSelected());
        dispose();
    }
    
    class GeneralPanel extends JPanel {
        JTextField maxLinesField;
        JTextField bufferField;
        JCheckBox forceLoggcLoading;
        JCheckBox showHotspotClasses;
        
        public GeneralPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(580, 190));
            
            JPanel layoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            layoutPanel.add(new JLabel("Maximum amount of lines to check for\n class histogram or possible deadlock informations"));
            maxLinesField = new JTextField(3);
            layoutPanel.add(maxLinesField);
            add(layoutPanel);
            
            layoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            layoutPanel.add(new JLabel("Stream Reset Buffer Size (in bytes)"));
            bufferField = new JTextField(10);
            layoutPanel.add(bufferField);
            add(layoutPanel);
            
            layoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            layoutPanel.add(new JLabel("Force Open Loggc Option even if class histograms were found in general logfile"));
            forceLoggcLoading = new JCheckBox();
            layoutPanel.add(forceLoggcLoading);
            add(layoutPanel);
            
            
            layoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            layoutPanel.add(new JLabel("Show internal hotspot classes in class histograms"));
            showHotspotClasses = new JCheckBox();
            layoutPanel.add(showHotspotClasses);
            add(layoutPanel);
        }
    }
    
    public class RegExPanel extends JPanel implements ActionListener {
        JComboBox dateParsingRegexs;
        JCheckBox isMillisTimeStamp;
        JButton clearButton;
        String lastSelectedItem = null;
        
        RegExPanel() {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(580, 190));
            
            JPanel layoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            layoutPanel.add(new JLabel("Regular Expression for parsing timestamps in logs files"));
            dateParsingRegexs = new JComboBox();
            dateParsingRegexs.setEditable(true);
            dateParsingRegexs.addActionListener(this);
            layoutPanel.add(dateParsingRegexs);
            clearButton = new JButton("Clear");
            clearButton.addActionListener(this);
            layoutPanel.add(clearButton);
            
            add(layoutPanel,BorderLayout.CENTER);
            
            layoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            isMillisTimeStamp = new JCheckBox();
            layoutPanel.add(new JLabel("Parsed timestamp is a long representing msecs since 1970"));
            layoutPanel.add(isMillisTimeStamp);
            add(layoutPanel,BorderLayout.SOUTH);
        }
        
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == dateParsingRegexs) {
                if((lastSelectedItem == null) || !((String) dateParsingRegexs.getSelectedItem()).equals(lastSelectedItem)) {
                    dateParsingRegexs.addItem(dateParsingRegexs.getSelectedItem());
                    lastSelectedItem = (String) dateParsingRegexs.getSelectedItem();
                }
            } else if (e.getSource() == clearButton) {
                dateParsingRegexs.setModel(new DefaultComboBoxModel());
            }
        }
    }
    
    //Must be called from the event-dispatching thread.
    public void resetFocus() {
    }
    
    
}
