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
 * $Id: MainMenu.java,v 1.22 2007-11-02 12:00:04 irockel Exp $
 */

package com.pironet.tda;

import com.pironet.tda.utils.PrefManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
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
    private JMenuItem recentSessionsMenu;
    private JMenuItem closeAllMenuItem;
    
    private TDA listener;
    private JToolBar toolBar;
    private JButton closeToolBarButton;
    private JMenuItem saveSessionMenuItem;
    private boolean runningAsPlugin;



    /** 
     * Creates a new instance of the MainMenu 
     */
    public MainMenu(TDA listener) {
        this.listener = listener;
        createMenuBar();
    }
    
    public MainMenu(TDA listener, boolean runningAsPlugin) {
        this(listener);
        this.runningAsPlugin = runningAsPlugin;
    }
            
    /**
     * get the close file menu item
     */
    public JMenuItem getCloseMenuItem() {
        return(closeMenuItem);
    }
    
    /**
     * get the close file menu item
     */
    public JButton getCloseToolBarButton() {
        return(closeToolBarButton);
    }
    
    /**
     * get the close all file menu item
     */
    public JMenuItem getCloseAllMenuItem() {
        return(closeAllMenuItem);
    }
    
    public JMenuItem getLongMenuItem() {
        return(longMenuItem);
    }
    
    public JMenuItem getSaveSessionMenuItem() {
        return(saveSessionMenuItem);
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
        menu.addMenuListener(listener);
        
        //a group of JMenuItems
        menuItem = new JMenuItem("Open...",
                KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open Log File with dumps.");
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        
        closeMenuItem = new JMenuItem("Close...");
        closeMenuItem.getAccessibleContext().setAccessibleDescription(
                "Close currently selected dump file.");
        closeMenuItem.addActionListener(listener);
        closeMenuItem.setEnabled(false);
        menu.add(closeMenuItem);
        
        closeAllMenuItem = new JMenuItem("Close all...");
        closeAllMenuItem.getAccessibleContext().setAccessibleDescription(
                "Close all open dump files.");
        closeAllMenuItem.addActionListener(listener);
        closeAllMenuItem.setEnabled(false);
        menu.add(closeAllMenuItem);
        
        createRecentFileMenu();
        menu.add(recentFilesMenu);
        
        menu.addSeparator();
        saveSessionMenuItem = new JMenuItem("Save Session...",
                KeyEvent.VK_S);
        saveSessionMenuItem.getAccessibleContext().setAccessibleDescription(
                "Save the current session of loaded log files");
        saveSessionMenuItem.addActionListener(listener);
        menu.add(saveSessionMenuItem);
        saveSessionMenuItem.setEnabled(false);
        menuItem = new JMenuItem("Open Session...",
                KeyEvent.VK_P);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open a stored session of logfiles");
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        
        createRecentSessionsMenu();
        menu.add(recentSessionsMenu);
        
        menu.addSeparator();

        menuItem = new JMenuItem("Preferences",
                KeyEvent.VK_P);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Set Preferences");
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        
        menu.addSeparator();

        menuItem = new JMenuItem("Exit TDA",
                KeyEvent.VK_X);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exit TDA");
        menuItem.addActionListener(listener);
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
        longMenuItem.addActionListener(listener);
        longMenuItem.setEnabled(false);
        menu.add(longMenuItem);

        menuItem = new JMenuItem("Filters",
                KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Setup Filter");
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        
        menu.addSeparator();
        menuItem = new JCheckBoxMenuItem("Show Toolbar", PrefManager.get().getShowToolbar());
        menuItem.addActionListener(listener);
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
        
        menuItem = new JMenuItem("Overview",
                KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "About Thread Dump Analyzer");
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Release Notes",null);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Release Notes");
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        menuItem = new JMenuItem("License",null);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "TDA Distribution License");
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("Forum",null);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Online TDA Forum");
        menuItem.addActionListener(listener);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("About TDA",
                KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "About Thread Dump Analyzer");
        menuItem.addActionListener(listener);
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
                    item.addActionListener(listener);
                }
            }
        } else {
            recentFilesMenu = new JMenuItem("Open recent file");
            recentFilesMenu.setEnabled(false);
        }
    }
    
    /**
     * create the menu for opening recently selected files.
     */
    private void createRecentSessionsMenu() {
        String[] recentFiles = PrefManager.get().getRecentSessions();
        
        if(recentFiles.length > 1) {
            recentSessionsMenu = new JMenu("Open recent session");
            
            for(int i = 1; i < recentFiles.length; i++) {
                if(!recentFiles[i].equals("")) {
                    JMenuItem item = new JMenuItem(recentFiles[i]);
                    ((JMenu) recentSessionsMenu).add(item);
                    item.addActionListener(listener);
                }
            }
        } else {
            recentSessionsMenu = new JMenuItem("Open recent session");
            recentSessionsMenu.setEnabled(false);
        }
    }

    /**
     * creates and returns a toolbar for the main menu with most
     * important entries.
     * @return toolbar instance, is created on demand.
     */
    public JToolBar getToolBar() {
        if(toolBar == null) {
            createToolBar();
        }
        return toolBar;
    }
    
    /**
     * create a toolbar showing the most important main menu entries.
     */
    private void createToolBar() {
        toolBar = new JToolBar("TDA Toolbar");
        if(runningAsPlugin) {
            toolBar.add(createToolBarButton("Request a Thread Dump", "FileOpen.gif"));
        } else {
            toolBar.add(createToolBarButton("Open Logfile", "FileOpen.gif"));
            closeToolBarButton = createToolBarButton("Close selected Logfile", "CloseFile.gif");
            closeToolBarButton.setEnabled(false);
            toolBar.add(closeToolBarButton);
        }
        toolBar.addSeparator();
        toolBar.add(createToolBarButton("Preferences", "Preferences.gif"));
        toolBar.addSeparator();
        toolBar.add(createToolBarButton("Find long running threads", "FindLRThreads.gif"));
        toolBar.add(createToolBarButton("Filters", "Filters.gif"));
        toolBar.addSeparator();
        toolBar.add(createToolBarButton("Help","Help.gif"));
    }
    
    /**
     * create a toolbar button with tooltip and given icon.
     * @param text tooltip text
     * @param fileName filename for the icon to load
     * @return toolbar button
     */
    private JButton createToolBarButton(String text, String fileName) {
        JButton toolbarButton = new JButton(TDA.createImageIcon(fileName));
        if(text != null) {
            toolbarButton.setToolTipText(text);
        }
        toolbarButton.addActionListener(listener);
        return(toolbarButton);
    }
}
