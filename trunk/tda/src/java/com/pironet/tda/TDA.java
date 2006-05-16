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
 * $Id: TDA.java,v 1.34 2006-05-16 07:22:10 irockel Exp $
 */
package com.pironet.tda;

import com.pironet.tda.utils.HistogramTableModel;
import com.pironet.tda.utils.PrefManager;
import com.pironet.tda.utils.SwingWorker;
import com.pironet.tda.utils.TableSorter;
import java.awt.BorderLayout;
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
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
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;

/**
 * main class of the Thread Dump Analyzer. Start using static main method.
 *
 * @author irockel
 */
public class TDA extends JPanel implements TreeSelectionListener, ActionListener {
    private static JFileChooser fc;
    private static boolean DEBUG = false;
    protected static JFrame frame;

    private static String dumpFile;
    
    private static String loggcFile;

    private JEditorPane htmlPane;
    private JTree tree;
    private JSplitPane splitPane;
    private TreePath mergeDump;
    private Map threadDumps;
    private Vector topNodes;
    private InputStream dumpFileStream;
    private JScrollPane htmlView;
    private JScrollPane tableView;
    private JMenuItem loggcMenuItem;
    private JMenuItem addMenuItem;
    private JMenuItem addJMXMenuItem;
    private JTextField filter;
    private JCheckBox checkCase;
    private PreferencesDialog prefsDialog;
    private LongThreadDialog longThreadDialog;
    private JTable histogramTable;
    
    public TDA() {
        super(new GridLayout(1,0));
        //setUIFont (new javax.swing.plaf.FontUIResource("SansSerif",Font.PLAIN,10));        
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
        info.append("(C)opyright 2006 - Ingo Rockel<br>");
        info.append("Version: <b>1.1</b><p>");
        info.append("Select File/Open to open your log file containing thread dumps to start analyzing these thread dumps.<p></body></html>");
        return(info.toString());
    }
    
    public void init() {
        // clear tree
        threadDumps = new HashMap();
        
        /*if(top != null) {
            top.removeAllChildren();
            top.removeFromParent();
            top = null;
        }*/
        topNodes = new Vector();
        
        addDumpFile();
    }
    
    /**
     * add the set dumpFileStream to the tree
     * @param clearTree true, if the tree should be cleared before.
     */
    private void addDumpFile() {
        try {
            dumpFileStream = new ProgressMonitorInputStream(
                    this,
                    "Parsing " + dumpFile,
                    new FileInputStream(dumpFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        
        //Create the nodes.
        final DefaultMutableTreeNode top = new DefaultMutableTreeNode("Thread Dumps of " + dumpFile);
        topNodes.add(top);
                        
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                createNodes(top, dumpFileStream);
                createTree();
                
                return null;
            }
        };
        worker.start();
        
    }
        
    protected void createTree() {
        //Create a tree that allows multiple selection at a time.
        if(topNodes.size() == 1) {
            tree = new JTree((DefaultMutableTreeNode) topNodes.get(0));
            frame.setTitle("TDA - Thread Dumps of " + dumpFile);
        } else {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Thread Dump files");
            for(int i = 0; i < topNodes.size(); i++) {
                root.add((DefaultMutableTreeNode) topNodes.get(i));
            }
            tree = new JTree(root);
            frame.setTitle(frame.getTitle() + " ...");
        }
        
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        
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
            displayTable((HistogramTableModel) tdi.content);
        } else if (nodeInfo instanceof String && ((String)nodeInfo).startsWith("Thread Dumps")) {
            displayLogFile();
        } else {
            displayContent(null);
        }
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }
    }
    
    private void displayLogFile() {
        if(splitPane.getBottomComponent() != htmlView) {
            splitPane.setBottomComponent(htmlView);
        }
        htmlPane.setText("");
        htmlPane.setCaretPosition(0);
    }
    
    private void displayContent(String text) {
        if(splitPane.getBottomComponent() != htmlView) {
            splitPane.setBottomComponent(htmlView);
        }
        if (text != null) {
            htmlPane.setText(text);
            htmlPane.setCaretPosition(0);
        } else {
            htmlPane.setText("");
        }
    }
    
    private void displayTable(HistogramTableModel htm) {
        htm.setFilter("");
        htm.setShowHotspotClasses(PrefManager.get().getShowHotspotClasses());

        TableSorter ts = new TableSorter(htm);
        histogramTable = new JTable(ts);
        ts.setTableHeader(histogramTable.getTableHeader());
        histogramTable.getColumnModel().getColumn(0).setPreferredWidth(700);
        tableView = new JScrollPane(histogramTable);

        JPanel histogramView = new JPanel(new BorderLayout());
        JPanel histoStatView = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        Font font = new Font("SansSerif", Font.PLAIN, 10);
        JLabel infoLabel = new JLabel(NumberFormat.getInstance().format(htm.getRowCount()) + " classes and base types");
        infoLabel.setFont(font);
        histoStatView.add(infoLabel);
        infoLabel = new JLabel(NumberFormat.getInstance().format(htm.getBytes()) + " bytes");
        infoLabel.setFont(font);
        histoStatView.add(infoLabel);
        infoLabel = new JLabel(NumberFormat.getInstance().format(htm.getInstances()) + " live objects");
        infoLabel.setFont(font);
        histoStatView.add(infoLabel);
        if(htm.isOOM()) {
            infoLabel = new JLabel("<html><b>OutOfMemory found!</b>");
            infoLabel.setFont(font);
            histoStatView.add(infoLabel);
        }
        if(htm.isIncomplete()) {
            infoLabel = new JLabel("<html><b>Class Histogram is incomplete! (broken logfile?)</b>");
            infoLabel.setFont(font);
            histoStatView.add(infoLabel);
        }
        JPanel filterPanel = new JPanel(new FlowLayout());
        infoLabel = new JLabel("Filter-Expression");
        infoLabel.setFont(font);
        filterPanel.add(infoLabel);
        
        filter = new JTextField(30);
        filter.setFont(font);
        filter.addCaretListener(new FilterListener(htm));
        filterPanel.add(infoLabel);
        filterPanel.add(filter);
        checkCase = new JCheckBox();
        checkCase.addChangeListener(new CheckCaseListener(htm));
        infoLabel = new JLabel("Ignore Case");
        infoLabel.setFont(font);        
        filterPanel.add(infoLabel);
        filterPanel.add(checkCase);
        histoStatView.add(filterPanel);
        histogramView.add(histoStatView, BorderLayout.SOUTH);
        histogramView.add(tableView, BorderLayout.CENTER);
        
        histogramView.setPreferredSize(splitPane.getBottomComponent().getSize());
        
        splitPane.setBottomComponent(histogramView);
    }
    
    private class FilterListener implements CaretListener {
        HistogramTableModel htm;
        String currentText = "";
        FilterListener(HistogramTableModel htm) {
            this.htm = htm;
        }        

        public void caretUpdate(CaretEvent event) {
            if(!filter.getText().equals(currentText)) {
                htm.setFilter(filter.getText());
                histogramTable.revalidate();
            }
        }
    }
    
    private class CheckCaseListener implements ChangeListener {
        HistogramTableModel htm;
        
        CheckCaseListener(HistogramTableModel htm) {
            this.htm = htm;
        }
        
        public void stateChanged(ChangeEvent e) {            
            htm.setIgnoreCase(checkCase.isSelected());
            histogramTable.revalidate();
        }
    }
    
    private void createNodes(DefaultMutableTreeNode top, InputStream dumpFileStream) {
        DumpParser dp = null;
        try {
            dp = DumpParserFactory.get().getDumpParserForVersion("1.4", dumpFileStream, threadDumps);
            while(dp.hasMoreDumps()) {
                top.add(dp.parseNext());
            }
            loggcMenuItem.setEnabled(!dp.isFoundClassHistograms() || PrefManager.get().getForceLoggcLoading());
            addMenuItem.setEnabled(true);
            addJMXMenuItem.setEnabled(true);
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
        menuItem = new JMenuItem("Search below node...");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popup.addSeparator();
        menuItem = new JMenuItem("Diff Selection");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        menuItem = new JMenuItem("Parse loggc-logfile...");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popup.addSeparator();
        menuItem = new JMenuItem("Find long running threads...");
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
                "Open Log File with dumps.");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Open JMX Connection...",
                KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open remote JMX Connection.");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("Open recent file",
                null);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open recent log file.");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu.addSeparator();
        addMenuItem = new JMenuItem("Add...",
                KeyEvent.VK_A);
        addMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        addMenuItem.getAccessibleContext().setAccessibleDescription(
                "Open additional Log File with dumps.");
        addMenuItem.addActionListener(this);
        addMenuItem.setEnabled(false);
        menu.add(addMenuItem);
        addJMXMenuItem = new JMenuItem("Add JMX Connection...",
                KeyEvent.VK_J);
        addJMXMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_J, ActionEvent.ALT_MASK));
        addJMXMenuItem.getAccessibleContext().setAccessibleDescription(
                "Request additional thread dumps using a remote jmx connection.");
        addJMXMenuItem.addActionListener(this);
        addJMXMenuItem.setEnabled(false);
        menu.add(addJMXMenuItem);
        loggcMenuItem = new JMenuItem("Open loggc file...",
                KeyEvent.VK_O);
        loggcMenuItem.getAccessibleContext().setAccessibleDescription(
                "Open GC Log files with heap dumps");
        loggcMenuItem.addActionListener(this);
        loggcMenuItem.setEnabled(false);
        menu.add(loggcMenuItem);
        
        menu.addSeparator();
        menuItem = new JMenuItem("Save Session...",
                KeyEvent.VK_S);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Save the current session of loaded log files");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem.setEnabled(false);
        menuItem = new JMenuItem("Open stored Session...",
                KeyEvent.VK_P);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Open a stored session of logfiles");
        menuItem.addActionListener(this);
        menuItem.setEnabled(false);
        menu.add(menuItem);
        
        menu.addSeparator();

        menuItem = new JMenuItem("Preferences",
                KeyEvent.VK_P);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Set Preferences");
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
        menu = new JMenu("Tools");
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription(
                "Tools Menu");
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Find long running threads...",
                KeyEvent.VK_L);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, ActionEvent.ALT_MASK));
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
        
        menuItem = new JMenuItem("Tutorial",
                KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "About Thread Dump Analyzer");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("About TDA",
                KeyEvent.VK_A);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "About Thread Dump Analyzer");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        return menuBar;
    }
    
    /**
     * check menu events
     */ 
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        if("Open...".equals(source.getText())) {
            openFile(false);
        } else if("Add...".equals(source.getText())) {
            openFile(true);
        } else if("Open loggc file...".equals(source.getText())) {
            openLoggcFile();
        } else if("Preferences".equals(source.getText())) {
            showPreferencesDialog();
        } else if("Exit TDA".equals(source.getText())) {
            saveState();
            frame.dispose();
        } else if("Tutorial".equals(source.getText())) {
            showTutorial();
        } else if("About TDA".equals(source.getText())) {
            showInfo();
        } else if("Search below node...".equals(source.getText())) {
            SearchDialog.createAndShowGUI(tree);
        } else if("Parse loggc-logfile...".equals(source.getText())) {
            parseLoggcLogfile();
        } else if("Find long running threads...".equals(source.getText())) {
            findLongRunningThreads();
        } else if("Diff Selection".equals(source.getText())) {
            TreePath[] paths = tree.getSelectionPaths();
            if(paths.length < 2) {
                JOptionPane.showMessageDialog(this.getRootPane(),
                        "You must select at least two dumps for getting a diff!\n",
                        "Error", JOptionPane.ERROR_MESSAGE);
                
            } else {
                DumpParserFactory.get().getCurrentDumpParser().mergeDumps(fetchTop(tree.getSelectionPath()), 
                        threadDumps, paths, paths.length, null);
                createTree();
                this.getRootPane().revalidate();
            }
        }
    }
    
    private void showInfo() {
        JOptionPane.showMessageDialog(this.getRootPane(),
                "TDA - Thread Dump Analyzer\n\n" +
                "Version: 1.1\n\n" +
                "(c) by Ingo Rockel\n\n" +
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
    
    private void setUIFont(javax.swing.plaf.FontUIResource f){
        //
        // sets the default font for all Swing components.
        // ex.
        //  setUIFont (new javax.swing.plaf.FontUIResource("Serif",Font.ITALIC,12));
        //
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }
    
    private void showTutorial() {
        TutorialDialog tutDialog = new TutorialDialog(frame);
        tutDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        //Display the window.
        tutDialog.pack();
        tutDialog.setVisible(true);

    }
    
    private void showPreferencesDialog() {
        //Create and set up the window.
        if(prefsDialog == null) {
            prefsDialog = new PreferencesDialog(frame);
            prefsDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
        
        frame.setEnabled(false);
        //Display the window.
        prefsDialog.reset();
        prefsDialog.pack();
        prefsDialog.setVisible(true);
    }
    
    /**
     * open a log file.
     * @param addFile check if a log file should be added or if tree should be cleared.
     */
    private void openFile(boolean addFile) {
        int returnVal = fc.showOpenDialog(this.getRootPane());
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            dumpFile = file.getAbsolutePath();
            if(dumpFile != null) {
                if(addFile) {
                    // do direct add without re-init.
                    addDumpFile();
                } else {
                    init();
                }
                this.getRootPane().revalidate();
            }
        }
    }
    
    /** 
     * Returns an ImageIcon, or null if the path was invalid. 
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = TDA.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    /**
     * load a loggc log file based on the current selected thread dump
     */
    private void parseLoggcLogfile() {
        // search for starting node
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        tree.getLastSelectedPathComponent();
        while(node != null && !checkNameFromNode(node, "Full Thread Dump")) {
            node = (DefaultMutableTreeNode) node.getParent();
        }
        
        // get pos of this node in the thread dump hierarchy.
        int pos = node.getParent().getIndex(node);
        
        DumpParserFactory.get().getCurrentDumpParser().setDumpHistogramCounter(pos);
        openLoggcFile();
    }
        
    /**
     * check if name of node starts with passed string
     */
    private boolean checkNameFromNode(DefaultMutableTreeNode node, String startsWith) {
        Object info = node.getUserObject();
        String result = null;
        if(info instanceof ThreadInfo) {
            result = ((ThreadInfo) info).threadName;
        }
        
        return(result.startsWith(startsWith));
    }
    
    /**
     * open and parse loggc file
     */
    private void openLoggcFile() {
        int returnVal = fc.showOpenDialog(this.getRootPane());
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            loggcFile = file.getAbsolutePath();
            if(loggcFile != null) {
                try {
                    final InputStream loggcFileStream = new ProgressMonitorInputStream(
                            this,
                            "Parsing " + loggcFile,
                            new FileInputStream(loggcFile));
                    
                    final SwingWorker worker = new SwingWorker() {
                        public Object construct() {
                            try {
                                DefaultMutableTreeNode top = fetchTop(tree.getSelectionPath());
                                DumpParserFactory.get().getCurrentDumpParser().parseLoggcFile(loggcFileStream, top, threadDumps);
                                
                                createNodes(top, dumpFileStream);
                                createTree();
                                getRootPane().revalidate();
                            } finally {
                                if(loggcFileStream != null) {
                                    try {
                                        loggcFileStream.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                            return null;
                        }
                    };
                    worker.start();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /**
     * find long running threads either in all parsed thread dumps or in marked thread 
     * dump range.
     */
    private void findLongRunningThreads() {
        TreePath[] paths = tree.getSelectionPaths();
        if((paths == null) || (paths.length < 2)) {
            JOptionPane.showMessageDialog(this.getRootPane(),
                    "You must select at least two dumps for long thread run detection!\n",
                    "Error", JOptionPane.ERROR_MESSAGE);
            
        } else {
            if(longThreadDialog == null) {
                longThreadDialog = new LongThreadDialog(this, paths, fetchTop(tree.getSelectionPath()), threadDumps);
                longThreadDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            }
            
            frame.setEnabled(false);
            //Display the window.
            longThreadDialog.reset();
            longThreadDialog.pack();
            longThreadDialog.setVisible(true);
            
        }
    }
    
    private DefaultMutableTreeNode fetchTop(TreePath pathToRoot) {
        System.out.println("Node=" + pathToRoot.getPathComponent(1).getClass().getName());
        return((DefaultMutableTreeNode) pathToRoot.getPathComponent(1));
    }
    
    /**
     * save the application state to preferences.
     */
    private static void saveState() {
        PrefManager.get().setWindowState(frame.getExtendedState());
        PrefManager.get().setSelectedPath(fc.getCurrentDirectory());
        PrefManager.get().setPreferredSize(frame.getRootPane().getSize());
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
        
        Image image = Toolkit.getDefaultToolkit().getImage( "TDA.gif" );
        frame.setIconImage( image );
        //frame.setIconImage(createImageIcon("resources/images/TDA.gif").getImage());
        
        // init filechooser
        fc = new JFileChooser();
        fc.setCurrentDirectory(PrefManager.get().getSelectedPath());
        
        frame.getRootPane().setPreferredSize(PrefManager.get().getPreferredSize());
        
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
