/*
 * SearchDialog.java
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
 * $Id: SearchDialog.java,v 1.2 2006-02-20 09:47:43 irockel Exp $
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
