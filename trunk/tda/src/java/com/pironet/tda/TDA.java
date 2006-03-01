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
 * TDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * TDA should have received a copy of the Lesser GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: TDA.java,v 1.10 2006-03-01 20:42:23 irockel Exp $
 */
package com.pironet.tda;

import com.pironet.tda.utils.HistogramTableModel;
import com.pironet.tda.utils.PrefManager;
import com.pironet.tda.utils.SwingWorker;
import com.pironet.tda.utils.TableSorter;
import java.io.FileNotFoundException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.Icon;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.tree.TreePath;

/**
 * main class of the Thread Dump Analyzer. Start using static main method.
 *
 * @author irockel
 */
public class TDA extends JPanel implements TreeSelectionListener, ActionListener {
    private static JFileChooser fc;
    private static boolean DEBUG = false;
    private static JFrame frame;

    //private static String dumpFile = "/home/irockel/kunden/metro/oom/durpcom3/dump.log";
    //private static String dumpFile = "/home/irockel/kunden/metro/oom/durpcom1/OC4J~cms~default_island~1";
    private static String dumpFile;

    private JEditorPane htmlPane;
    private JTree tree;
    private JSplitPane splitPane;
    private TreePath mergeDump;
    private Map threadDumps;
    private DefaultMutableTreeNode top;
    private InputStream dumpFileStream;
    private JScrollPane htmlView;
    private JScrollPane tableView;
    
    
    public TDA() {
        super(new GridLayout(1,0));
        tree = new JTree();
        
        //Create the HTML viewing pane.
        htmlPane = new JEditorPane("text/html", getInfoText());
        htmlPane.setEditable(false);
        
        JEditorPane emptyPane = new JEditorPane("text/html", "<i>empty</i>");
        emptyPane.setEditable(false);
        
        htmlView = new JScrollPane(htmlPane);
        JScrollPane emptyView = new JScrollPane(emptyPane);
        
        //Add the scroll panes to a split pane.
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBottomComponent(htmlView);
        splitPane.setTopComponent(emptyView);
        
        Dimension minimumSize = new Dimension(200, 50);
        htmlView.setMinimumSize(minimumSize);
        emptyView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(100);
                
        //Add the split pane to this panel.
        add(splitPane);
    }
    
    private String getInfoText() {
        StringBuffer info = new StringBuffer("<html><body><b>TDA - Thread Dump Analyzer</b><p>");
        info.append("(C)opyright 2006 - TDA Team<br>");
        info.append("Version: <b>0.1-prerelease</b><p>");
        info.append("Select File/Open to open your log file containing thread dumps to start analyzing these thread dumps.<p></body></html>");
        return(info.toString());
    }
    
    public void init() {
        try {
            dumpFileStream = new ProgressMonitorInputStream(
                    this,
                    "Parsing " + dumpFile,
                    new FileInputStream(dumpFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        
        //Create the nodes.
        if(top != null) {
            top.removeAllChildren();
            top.removeFromParent();
            top = null;
        }
        top = new DefaultMutableTreeNode("Thread Dumps of " + dumpFile);
        threadDumps = new HashMap();
        
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                createNodes(top, dumpFileStream);                    
                createTree();
                
                return null;
            }
        };
        worker.start();
    }
        
    private void createTree() {
        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);
        
        splitPane.setTopComponent(treeView);
        
        Dimension minimumSize = new Dimension(200, 50);
        treeView.setMinimumSize(minimumSize);
        //Enable tool tips.
        //ToolTipManager.sharedInstance().registerComponent(tree);
        
        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);
        
        createPopupMenu();
        
    }
    
    
    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        tree.getLastSelectedPathComponent();
        
        if (node == null) {
            return;
        }
        
        Object nodeInfo = node.getUserObject();
        if (nodeInfo instanceof ThreadInfo) {
            ThreadInfo ti = (ThreadInfo)nodeInfo;
            displayContent(ti.content);
            if (DEBUG) {
                System.out.print(ti.content + ":  \n    ");
            }
        } else if (nodeInfo instanceof HistogramInfo) {
            HistogramInfo tdi = (HistogramInfo)nodeInfo;
            System.out.println("Content" + tdi.content);
            displayTable((HistogramTableModel) tdi.content);
        } else {
            displayContent(null);
        }
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }
    }
    
    private void displayContent(String text) {
        if(splitPane.getBottomComponent() != htmlView) {
            splitPane.setBottomComponent(htmlView);
        }
        htmlPane.setContentType("text/html");
        if (text != null) {
            htmlPane.setText(text);
        } else {
            htmlPane.setText("");
        }
    }
    
    private void displayTable(HistogramTableModel htm) {
        TableSorter ts = new TableSorter(htm);
        JTable histogramTable = new JTable(ts);
        ts.setTableHeader(histogramTable.getTableHeader());
        tableView = new JScrollPane(histogramTable);
        splitPane.setBottomComponent(tableView);
    }
    
    private void createNodes(DefaultMutableTreeNode top, InputStream dumpFileStream) {
        DumpParser dp = null;
        try {
            dp = DumpParserFactory.get().getDumpParserForVersion("1.4", dumpFileStream, threadDumps);
            while(dp.hasMoreDumps()) {
                top.add(dp.parseNext());
            }
        } finally {
            if(dp != null) {
                try {
                    dp.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public void createPopupMenu() {
        JMenuItem menuItem;
        
        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();
        /*menuItem = new JMenuItem("Expand All below node");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Collapse All below node");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popup.addSeparator();*/
        menuItem = new JMenuItem("Search below node...");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popup.addSeparator();
        menuItem = new JMenuItem("Select this dump for diff...");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        
        //Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(popup);
        tree.addMouseListener(popupListener);
    }
    
    class PopupListener extends MouseAdapter {
        JPopupMenu popup;
        
        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }
    
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        
        //Create the menu bar.
        menuBar = new JMenuBar();
        
        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription(
                "File Menu");
        menuBar.add(menu);
        
        //a group of JMenuItems
        menuItem = new JMenuItem("Open...",
                KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open Log File with dumps");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Exit TDA",
                KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exit TDA");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        //Build second menu in the menu bar.
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription(
                "Help Menu");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("About TDA",
                KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "About Thread Dump Analyzer");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        return menuBar;
    }
    
    
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        System.out.println(source.getText());
        if("Open...".equals(source.getText())) {
            openFile();
        } else if("Exit TDA".equals(source.getText())) {
            saveState();
            frame.dispose();
        } else if("About TDA".equals(source.getText())) {
            showInfo();
        } else if("Search below node...".equals(source.getText())) {
            SearchDialog.createAndShowGUI(tree);
        } else if("Select this dump for diff...".equals(source.getText())) {
            if(mergeDump == null) {
                mergeDump = tree.getSelectionPath();
            } else {
                System.out.println(mergeDump + " // " + tree.getSelectionPath());
                DumpParserFactory.get().getCurrentDumpParser().mergeDumps(top, threadDumps, mergeDump, tree.getSelectionPath());
                createTree();
                this.getRootPane().revalidate();
            }
        }
    }
    
    private void showInfo() {
        JOptionPane.showMessageDialog(this.getRootPane(),
                "TDA - Thread Dump Analyzer\n\n" +
                "(c) by TDA Team 2006\n\n" +
                "TDA is free software; you can redistribute it and/or modify\n" +
                "it under the terms of the Lesser GNU General Public License as published by\n" +
                "the Free Software Foundation; either version 2.1 of the License, or\n" +
                "(at your option) any later version.\n\n" +
                "TDA is distributed in the hope that it will be useful,\n" +
                "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                "Lesser GNU General Public License for more details.\n\n" +
                "You should have received a copy of the Lesser GNU General Public License\n" +
                "along with TDA; if not, write to the Free Software\n" +
                "Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA\n\n",
                "Copyright Notice", JOptionPane.INFORMATION_MESSAGE);

    }
    
    
    private void openFile() {
        int returnVal = fc.showOpenDialog(this.getRootPane());
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            dumpFile = file.getAbsolutePath();
            tree = null;
            top = null;
            if(dumpFile != null) {
                init();
                this.getRootPane().revalidate();
            }
        }
    }
    
    /**
     * save the application state to preferences.
     */
    private static void saveState() {
        PrefManager.get().setWindowState(frame.getExtendedState());
        PrefManager.get().setSelectedPath(fc.getCurrentDirectory());
        PrefManager.get().setPreferredSize(frame.getSize());
        PrefManager.get().setWindowPos(frame.getX(), frame.getY());
        PrefManager.get().flush();
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {        
        //Create and set up the window.
        frame = new JFrame("TDA - Thread Dump Analyzer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // init filechooser
        fc = new JFileChooser();
        fc.setCurrentDirectory(PrefManager.get().getSelectedPath());
        
        frame.setPreferredSize(PrefManager.get().getPreferredSize());
        
        //Create and set up the content pane.
        TDA newContentPane = new TDA();
        if(dumpFile != null) {
            newContentPane.init();
        }
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
        frame.setJMenuBar(newContentPane.createMenuBar());
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveState();
            }
            
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }            
        });
        
        frame.setLocation(PrefManager.get().getWindowPos());
        
        //Display the window.
        frame.pack();
        
        // restore old window settings.
        frame.setExtendedState(PrefManager.get().getWindowState());
        
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        if(args.length > 0) {
            dumpFile = args[0];
        }
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    private class MyRenderer extends DefaultTreeCellRenderer {
        Icon tutorialIcon;
        
        public MyRenderer(Icon icon) {
            tutorialIcon = icon;
        }
        
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            
            super.getTreeCellRendererComponent(
                    tree, value, sel,
                    expanded, leaf, row,
                    hasFocus);
            /*if (leaf && isTutorialBook(value)) {
                setIcon(tutorialIcon);
                setToolTipText("This book is in the Tutorial series.");
            } else {
                setToolTipText(null); //no tool tip
            }*/
            
            return this;
        }
    }
}
