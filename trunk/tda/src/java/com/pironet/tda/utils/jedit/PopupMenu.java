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
 * $Id: PopupMenu.java,v 1.1 2007-10-03 12:50:26 irockel Exp $
 */

package com.pironet.tda.utils.jedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * popup for the jedit text area
 * @author irockel
 */
public class PopupMenu extends JPopupMenu implements ActionListener {
    
    public PopupMenu() {
        JMenuItem menuItem;
        
        menuItem = new JMenuItem("Goto Line");
        menuItem.addActionListener(this);
        add(menuItem);
        this.addSeparator();
        menuItem = new JMenuItem("Search...");
        menuItem.addActionListener(this);
        add(menuItem);
    }

    public void actionPerformed(ActionEvent e) {
    }

}
