/*
 * SearchDialog.java
 *
 * Created on 10. Februar 2006, 10:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
public class SearchDialog extends JPanel
                          implements ActionListener {

    private static String SEARCH = "search";
    private static String CANCEL = "cancel";

    private JFrame controllingFrame; //needed for dialogs
    private JTextField searchField;
    
    private JTree searchTree;

    public SearchDialog(JFrame f, JTree tree) {
        //Use the default FlowLayout.
        controllingFrame = f;

        //Create everything.
        searchField = new JTextField(10);
        searchField.setActionCommand(SEARCH);
        searchField.addActionListener(this);

        JLabel label = new JLabel("Enter search string: ");
        label.setLabelFor(searchField);
        
        searchTree = tree;

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
        //JButton cancelButton = new JButton("Cancel");

        searchButton.setActionCommand(SEARCH);
        //cancelButton.setActionCommand(CANCEL);
        searchButton.addActionListener(this);
        //cancelButton.addActionListener(this);

        p.add(searchButton);
        //p.add(cancelButton);

        return p;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (SEARCH.equals(cmd)) { //Process the password.
            TreePath searchPath = searchTree.getNextMatch(searchField.getText(),searchTree.getRowCount()-1,Position.Bias.Forward);
            System.out.println("treepath " + searchPath);
            if(searchPath != null) {
                searchTree.setSelectionPath(searchPath);
            } else {
                JOptionPane.showMessageDialog(controllingFrame,
                    searchField.getText() + " not found!",
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            resetFocus();
        }
    }

    //Must be called from the event-dispatching thread.
    protected void resetFocus() {
        searchField.requestFocusInWindow();
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(JTree searchTree) {
        //Make sure we have nice window decorations.
        //JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Search below selected node... ");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Create and set up the content pane.
        final SearchDialog newContentPane = new SearchDialog(frame, searchTree);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Make sure the focus goes to the right component
        //whenever the frame is initially given the focus.
        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                newContentPane.resetFocus();
            }
        });

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
