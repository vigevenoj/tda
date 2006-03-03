/*
 * PreferencesDialog.java
 *
 * Created on 2. März 2006, 17:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.pironet.tda;

import com.pironet.tda.utils.PrefManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
    
    /** 
     * Creates a new instance of PreferencesDialog 
     */
    public PreferencesDialog(JFrame owner) {
        super(owner, "Preferences");
        getContentPane().setLayout(new BorderLayout());
        initPanel();
        setLocationRelativeTo(owner);
    }
    
    private void initPanel() {
        prefsPane = new JTabbedPane();
        generalPanel = new GeneralPanel();
        prefsPane.addTab("General", generalPanel);        
        getContentPane().add(prefsPane,BorderLayout.CENTER);
        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveSettings();                
            }
        });
        
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        generalPanel.dateParsingRegex.setText(PrefManager.get().getDateParsingRegex());
    }
    
    private void saveSettings() {
        PrefManager.get().setForceLoggcLoading(generalPanel.forceLoggcLoading.isSelected());
        PrefManager.get().setMaxRows(Integer.parseInt(generalPanel.maxLinesField.getText()));
        PrefManager.get().setStreamResetBuffer(Integer.parseInt(generalPanel.bufferField.getText()));
        PrefManager.get().setDateParsingRegex(generalPanel.dateParsingRegex.getText());
        dispose();
    }
    
    class GeneralPanel extends JPanel {
        JTextField maxLinesField;
        JTextField bufferField;
        JCheckBox forceLoggcLoading;
        JTextField dateParsingRegex;
        
        public GeneralPanel() {
            //super(new GridLayout(3,2, 10, 10));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(650, 140));
            
            JPanel layoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));            
            layoutPanel.add(new JLabel("Maximum amount of lines to check for\n class histogram or possible deadlock informations"));
            maxLinesField = new JTextField(3);
            layoutPanel.add(maxLinesField);
            add(layoutPanel);
            
            layoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));            
            layoutPanel.add(new JLabel("Stream Reset Buffer Size (in bytes)"));
            bufferField = new JTextField(10);
            layoutPanel.add(bufferField);
            add(layoutPanel);
            
            layoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
            layoutPanel.add(new JLabel("Force Open Loggc Option even if class histograms were found in general logfile"));
            forceLoggcLoading = new JCheckBox();
            layoutPanel.add(forceLoggcLoading);
            add(layoutPanel);
            
            layoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
            layoutPanel.add(new JLabel("Regular Expression for parsing timestamps in logs files"));
            dateParsingRegex = new JTextField(25);
            layoutPanel.add(dateParsingRegex);
            add(layoutPanel);
        }
    }
    
    //Must be called from the event-dispatching thread.
    public void resetFocus() {
        //searchField.requestFocusInWindow();
    }

    
}
