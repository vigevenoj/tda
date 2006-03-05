/*
 * TutorialDialog.java
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
 * $Id: TutorialDialog.java,v 1.3 2006-03-05 09:36:45 irockel Exp $
 */

package com.pironet.tda;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author irockel
 */
public class TutorialDialog extends JDialog {
    private JEditorPane htmlView;
    private JTabbedPane prefsPane;
    private JPanel buttonPanel;
    private JButton closeButton;
    
    private String content;
    
    /** 
     * Creates a new instance of PreferencesDialog 
     */
    public TutorialDialog(JFrame owner) {
        super(owner, "Tutorial");
        getContentPane().setLayout(new BorderLayout());
        initPanel();
        setLocationRelativeTo(owner);
    }
    
    private void readContent() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("doc/tutorial.html"));
        StringBuffer contentBuffer = new StringBuffer();
        while(br.ready()) {
            contentBuffer.append(br.readLine());
            contentBuffer.append("\n");
        }
        content = contentBuffer.toString();
    }
    
    private void initPanel() {
        try {
            readContent();
            URL tutURL;
            tutURL = new URL("file://" + System.getProperty("user.dir", "") + "/doc/tutorial.html");
            //htmlView = new JEditorPane("text/html", content);
            htmlView = new JEditorPane(tutURL);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        JScrollPane scrollPane = new JScrollPane(htmlView);
        htmlView.setEditable(false);
        htmlView.setPreferredSize(new Dimension(600, 600));
        htmlView.setCaretPosition(0);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        closeButton = new JButton("Close");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getRootPane().setDefaultButton(closeButton);
    }
        
    //Must be called from the event-dispatching thread.
    public void resetFocus() {
        //searchField.requestFocusInWindow();
    }

    
    
}
