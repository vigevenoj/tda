/*
 * LongThreadDialog.java
 *
 * Created on 30. März 2006, 10:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.pironet.tda;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author irockel
 */
public class LongThreadDialog extends JDialog {
    private JTabbedPane prefsPane;
    private SettingsPanel settingsPanel;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
    private TDA backRef;
    private TreePath[] dumps;
    private DefaultMutableTreeNode top;
    private Map threadDumps;
    
    /** 
     * Creates a new instance of PreferencesDialog 
     */
    public LongThreadDialog(TDA owner, TreePath[] dumps, DefaultMutableTreeNode top, Map threadDumps) {        
        super(owner.frame, "Detect long running Threads");
        backRef = owner;
        this.dumps = dumps;
        this.threadDumps = threadDumps;
        this.top = top;
        getContentPane().setLayout(new BorderLayout());
        initPanel();
        setLocationRelativeTo(owner);
    }
    
    private void initPanel() {
        prefsPane = new JTabbedPane();
        settingsPanel = new SettingsPanel();
        prefsPane.addTab("Settings", settingsPanel);        
        getContentPane().add(prefsPane,BorderLayout.CENTER);
        okButton = new JButton("Start Detection");
        cancelButton = new JButton("Cancel");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int divider = backRef.topSplitPane.getDividerLocation();

                backRef.frame.setEnabled(true);
                DumpParserFactory.get().getCurrentDumpParser().findLongRunningThreads(top, threadDumps, dumps, Integer.parseInt(settingsPanel.minOccurenceField.getText()), settingsPanel.threadRegExField.getText());
                backRef.createTree();
                backRef.tree.expandRow(1);

                backRef.getRootPane().revalidate();
                backRef.topSplitPane.setDividerLocation(divider);
                dispose();
            }
        });
        
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                backRef.frame.setEnabled(true);
                dispose();
            }
        });
        reset();        
    }
    
    public void reset() {
        getRootPane().setDefaultButton(okButton);
    }
    
    class SettingsPanel extends JPanel {
        JTextField minOccurenceField;
        JTextField threadRegExField;
        
        public SettingsPanel() {
            //super(new GridLayout(3,2, 10, 10));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setPreferredSize(new Dimension(420, 100));
            
            JPanel layoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));            
            layoutPanel.add(new JLabel("Minimum occurence of a thread"));
            minOccurenceField = new JTextField(3);
            minOccurenceField.setText(String.valueOf(dumps.length));
            layoutPanel.add(minOccurenceField);
            
            layoutPanel.add(new JLabel("Regular Expression thread identifier matches"));
            threadRegExField = new JTextField(30);
            layoutPanel.add(threadRegExField);
            JLabel example = new JLabel("<html><body>Example is <b>\"AJPRequestHandler(.*)</b>");
            example.setFont(new Font("SansSerif", Font.PLAIN, 10));
            layoutPanel.add(example);
            add(layoutPanel);            
        }
    }
    
    /**
     * Must be called from the event-dispatching thread.
     */
    public void resetFocus() {
        //searchField.requestFocusInWindow();
    }
    
}
