/*
 * MainMenu.java
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
 * TDA should have received a copy of the Lesser GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: MainMenu.java,v 1.8 2006-09-25 08:46:53 irockel Exp $
 */

package com.pironet.tda;

import com.pironet.tda.utils.PrefManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * provides instances of the main menu (though there is typically only one).
 *
 * @author irockel
 */
public class MainMenu extends JMenuBar {
    
    private JMenuItem closeMenuItem;
    private JMenuItem longMenuItem;
    private JMenuItem recentFilesMenu;
    
    private ActionListener actionListener;


    /** 
     * Creates a new instance of the MainMenu 
     */
    public MainMenu(ActionListener listener) {
        actionListener = listener;
        createMenuBar();
    }
            
    /**
     * get the close file menu item
     */
    public JMenuItem getCloseMenuItem() {
        return(closeMenuItem);
    }
    
    public JMenuItem getLongMenuItem() {
        return(longMenuItem);
    }
    
    /**
     * create the top level menu bar
     */
    private void createMenuBar() {
        JMenu menu;
        JMenuItem menuItem;
        
        add(createFileMenu());
        add(createToolsMenu());
        add(createHelpMenu());
    }
    
    private JMenu createFileMenu() {
        JMenuItem menuItem;
        JMenu menu;
        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File Menu");
        
        //a group of JMenuItems
        menuItem = new JMenuItem("Open...",
                KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open Log File with dumps.");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        
        /*menuItem = new JMenuItem("Open JMX Connection...",
                KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open remote JMX Connection.");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);*/
        
        closeMenuItem = new JMenuItem("Close...",
                KeyEvent.VK_X);
        closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.ALT_MASK));
        closeMenuItem.getAccessibleContext().setAccessibleDescription(
                "Open Log File with dumps.");
        closeMenuItem.addActionListener(actionListener);
        closeMenuItem.setEnabled(false);
        menu.add(closeMenuItem);
        
        createRecentFileMenu();
        menu.add(recentFilesMenu);
        
        menu.addSeparator();
        menuItem = new JMenuItem("Save Session...",
                KeyEvent.VK_S);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Save the current session of loaded log files");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menuItem.setEnabled(false);
        menuItem = new JMenuItem("Open Session...",
                KeyEvent.VK_P);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open a stored session of logfiles");
        menuItem.addActionListener(actionListener);
        menuItem.setEnabled(false);
        menu.add(menuItem);
        menuItem = new JMenuItem("Open recent Session",
                null);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open a stored session of logfiles");
        menuItem.addActionListener(actionListener);
        menuItem.setEnabled(false);
        menu.add(menuItem);
        
        menu.addSeparator();

        menuItem = new JMenuItem("Preferences",
                KeyEvent.VK_P);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Set Preferences");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        
        menu.addSeparator();

        menuItem = new JMenuItem("Exit TDA",
                KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exit TDA");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        
        return(menu);
        
    }
    
    /**
     * Build tools menu in the menu bar.
     */
    private JMenu createToolsMenu() {
        JMenuItem menuItem;
        JMenu menu;
        menu = new JMenu("Tools");
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription(
                "Tools Menu");
        add(menu);
        
        longMenuItem = new JMenuItem("Find long running threads...",
                KeyEvent.VK_L);
        longMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, ActionEvent.ALT_MASK));
        longMenuItem.getAccessibleContext().setAccessibleDescription(
                "Exit TDA");
        longMenuItem.addActionListener(actionListener);
        longMenuItem.setEnabled(false);
        menu.add(longMenuItem);

        menuItem = new JMenuItem("Filters...",
                KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Setup Filter");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        
        return(menu);
    }
    
    /**
     * Build help menu in the menu bar.
     */
    private JMenu createHelpMenu() {
        JMenuItem menuItem;
        JMenu menu;
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription(
                "Help Menu");
        
        menuItem = new JMenuItem("Tutorial",
                KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "About Thread Dump Analyzer");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("About TDA",
                KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "About Thread Dump Analyzer");
        menuItem.addActionListener(actionListener);
        menu.add(menuItem);
        
        return(menu);
    }
    
    /**
     * create the menu for opening recently selected files.
     */
    private void createRecentFileMenu() {
        String[] recentFiles = PrefManager.get().getRecentFiles();
        
        if(recentFiles.length > 1) {
            recentFilesMenu = new JMenu("Open recent file");
            
            for(int i = 1; i < recentFiles.length; i++) {
                if(!recentFiles[i].equals("")) {
                    JMenuItem item = new JMenuItem(recentFiles[i]);
                    ((JMenu) recentFilesMenu).add(item);
                    item.addActionListener(actionListener);
                }
            }
        } else {
            recentFilesMenu = new JMenuItem("Open recent file");
            recentFilesMenu.setEnabled(false);
        }
    }
}
