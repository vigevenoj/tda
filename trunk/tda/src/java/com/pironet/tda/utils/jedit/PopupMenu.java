/**
 * Thread Dump Analysis Tool, parses Thread Dump input and displays it as tree
 *
 * This file is part of TDA - Thread Dump Analysis Tool.
 *
 * TDA is free software; you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * TDA is distributed in the hope that it will be useful,h
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * TDA should have received a copy of the Lesser GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: PopupMenu.java,v 1.3 2007-10-04 13:01:12 irockel Exp $
 */

package com.pironet.tda.utils.jedit;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 * popup for the jedit text area
 * @author irockel
 */
public class PopupMenu extends JPopupMenu implements ActionListener {
    private JEditTextArea ref;
    private JPanel parent;
    private JMenuItem againMenuItem;
    
    private String searchString;
    
    public PopupMenu(JEditTextArea ref, JPanel parent) {
        JMenuItem menuItem;
        
        menuItem = new JMenuItem("Goto Line...");
        menuItem.addActionListener(this);
        add(menuItem);
        this.addSeparator();
        menuItem = new JMenuItem("Search...");
        menuItem.addActionListener(this);
        add(menuItem);
        againMenuItem = new JMenuItem("Search again");
        againMenuItem.addActionListener(this);
        againMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        add(againMenuItem);
        
        this.ref = ref;
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JMenuItem) {
            JMenuItem source = (JMenuItem) (e.getSource());
            if (source.getText().equals("Goto Line...")) {
                gotoLine();
            } else if (source.getText().equals("Search...")) {
                search();
            } else if (source.getText().startsWith("Search again")) {
                search(searchString, ref.getCaretPosition() + 1);
            }
        } else if(e.getSource() instanceof JEditTextArea) {
            // only one key binding.
            if(searchString != null) {
                search(searchString, ref.getCaretPosition() + 1);
            }
        }
    }
    
    private void search(String searchString, int offSet) {
        int searchIndex = ref.getText().indexOf(searchString, offSet);
        if (searchIndex < 0) {
            JOptionPane.showMessageDialog(parent, "Search string not found", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            ref.setCaretPosition(searchIndex);
        }
    }
    
    private void gotoLine() {        
        String result = JOptionPane.showInputDialog(parent, "Type in line number (between 1-" + ref.getLineCount() + ")"); 
        
        if (result != null) {
            int lineNumber = 0;
            try {
                lineNumber = Integer.parseInt(result);
            } catch (NumberFormatException ne) {
                JOptionPane.showMessageDialog(parent, "Invalid line number entered", "Error", JOptionPane.ERROR_MESSAGE);
            }
            if ((lineNumber > 0) && (lineNumber <= ref.getLineCount())) {
                ref.setFirstLine(lineNumber);
            }
        }
    }

    private void search() {        
        String result = JOptionPane.showInputDialog(parent, "Type in search string"); 
        
        if (result != null) {
            search(result, ref.getCaretPosition());
            searchString = result;
        }
    }
    
    /**
     * overrides default implementation for "Search Again" enable check.
     */
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
        againMenuItem.setEnabled(searchString != null);
    }
}
