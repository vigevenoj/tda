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
 * $Id: TDA.java,v 1.45 2006-09-22 09:21:12 irockel Exp $
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
    private JTextField filter;
    private JCheckBox checkCase;
    private PreferencesDialog prefsDialog;
    private LongThreadDialog longThreadDialog;
    private JMXConnectDialog jmxConnectionDialog;
    private JTable histogramTable;
    
    public TDA() {
        super(new GridLayout(1,0));
        setUIFont (new javax.swing.plaf.FontUIResource("SansSerif",Font.PLAIN,11));        
        tree = new JTree();
        
        //Create the HTML viewing pane.
        htmlPane = new JEditorPane("text/html", getInfoText());
        htmlPane.setEditable(false);
        
        JEditorPane emptyPane = new JEditorPane("text/html", "<i><font size=-1>empty</i>");
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
        StringBuffer info = new StringBuffer("<html><body><font size=-1><b>TDA - Thread Dump Analyzer</b><p>");
        info.append("(C)opyright 2006 - Ingo Rockel<br>");
        info.append("Version: <b>1.2</b><p>");
        info.append("Select File/Open to open your log file containing thread dumps to start analyzing these thread dumps.<p></font></body></html>");
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
    
    private boolean openFileActionRunning = false;
    
    /**
     * add the set dumpFileStream to the tree
     */
    private void addDumpFile() {
        String[] file = new String[1];
        file[0] = dumpFile;
        addDumpFiles(file);
    }
    
    /**
     * add the set dumpFileStream to the tree
     */
    private void addDumpFiles(String[] files) {
        try {
            dumpFileStream = new ProgressMonitorInputStream(
                    this,
                    "Parsing " + dumpFile,
                    new FileInputStream(dumpFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        
        //Create the nodes.
        final DefaultMutableTreeNode top = new DefaultMutableTreeNode(new Logfile("Thread Dumps of " + dumpFile));
        topNodes.add(top);
                        
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                createNodes(top, dumpFileStream);
                createTree();
                tree.collapseRow(3);
                
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
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Thread Dump Nodes");
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
            if(ti.info != null) {
                StringBuffer sb = new StringBuffer(ti.info);
                sb.append(ti.content);
                displayContent(sb.toString());
            } else {
                displayContent(ti.content);
            }
            if (DEBUG) {
                System.out.print(ti.content + ":  \n    ");
            }
        } else if (nodeInfo instanceof HistogramInfo) {
            HistogramInfo tdi = (HistogramInfo)nodeInfo;
            displayTable((HistogramTableModel) tdi.content);
        } else if (nodeInfo instanceof Logfile && ((String)((Logfile)nodeInfo).getContent()).startsWith("Thread Dumps")) {
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
            //getMainMenu().getAddJMXMenuItem().setEnabled(true);
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
    
    protected MainMenu getMainMenu() {
        return((MainMenu) frame.getJMenuBar());
    }
    
    public void createPopupMenu() {
        JMenuItem menuItem;
        
        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();
        
        /*menuItem = new JMenuItem("Fetch Thread Dump");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popup.addSeparator();*/
        
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
        
    /**
     * check menu events
     */ 
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        if(source.getText().startsWith(":\\") || source.getText().startsWith("/") ) {
            dumpFile = source.getText();
            if(firstFile) {
                init();
                firstFile = false;
            } else {
                setRootNodeLevel(1);
                addDumpFile();
            }
        } else if("Open...".equals(source.getText())) {
            openFile();
        } else if("Open JMX Connection...".equals(source.getText())) {
            openJMXConnection(true);
        } else if("Add JMX Connection...".equals(source.getText())) {
            openJMXConnection(false);
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
            SearchDialog.createAndShowGUI(tree, frame);
        } else if("Parse loggc-logfile...".equals(source.getText())) {
            parseLoggcLogfile();
        } else if("Find long running threads...".equals(source.getText())) {
            findLongRunningThreads();
        } else if("Close...".equals(source.getText())) {
            closeCurrentDump();
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
                "<html><body><p><b>TDA - Thread Dump Analyzer</b></p><br>" +
                "<p>Version: 1.2</p><br>" +
                "<p>(c) by Ingo Rockel &lt;irockel@dev.java.net&gt;</p><br>" +
                "<p>TDA is free software; you can redistribute it and/or modify<br>" +
                "it under the terms of the Lesser GNU General Public License as published by<br>" +
                "the Free Software Foundation; either version 2.1 of the License, or<br>" +
                "(at your option) any later version.</p><br>" +
                "TDA is distributed in the hope that it will be useful,<br>" +
                "but WITHOUT ANY WARRANTY; without even the implied warranty of<br>" +
                "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br>" +
                "Lesser GNU General Public License for more details.<p><br>" +
                "You should have received a copy of the Lesser GNU General Public License<br>" +
                "along with TDA; if not, write to the Free Software<br>" +
                "Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA<p>",
                "Copyright Notice", JOptionPane.INFORMATION_MESSAGE);

    }
    
    /**
     * set the ui font for all tda stuff (needs to be done for create of objects)
     * @param f the font to user
     */
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
        tutDialog.setLocationRelativeTo(frame);
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
        prefsDialog.setLocationRelativeTo(frame);
        prefsDialog.setVisible(true);
    }

    /**
     * flag indicates if next file to open will be the first file (so fresh open)
     * or if a add has to be performed.
     */
    private boolean firstFile = true;
    
    /**
     * open a log file.
     * @param addFile check if a log file should be added or if tree should be cleared.
     */
    private void openFile() {
        int returnVal = fc.showOpenDialog(this.getRootPane());
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = fc.getSelectedFiles();
            for (int i = 0; i < files.length; i++) {
                dumpFile = files[i].getAbsolutePath();
                if(dumpFile != null) {
                    if(!firstFile) {
                        // root nodes are moved down.
                        setRootNodeLevel(1);

                        // do direct add without re-init.
                        addDumpFile();
                    } else {
                        init();
                        firstFile = false;
                    }
                }
                PrefManager.get().addToRecentFiles(files[i].getAbsolutePath());
            }
            this.getRootPane().revalidate();
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
     * search for dump root node of for given node
     * @param node starting to search for
     * @return root node returns null, if no root was found.
     */
    private DefaultMutableTreeNode getDumpRootNode(DefaultMutableTreeNode node) {
        // search for starting node
        while(node != null && !checkNameFromNode(node, "Full Thread Dump")) {
            node = (DefaultMutableTreeNode) node.getParent();
        }
        
        return(node);
    }
    
    /**
     * load a loggc log file based on the current selected thread dump
     */
    private void parseLoggcLogfile() {
        DefaultMutableTreeNode node = getDumpRootNode((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
        
        // get pos of this node in the thread dump hierarchy.
        int pos = node.getParent().getIndex(node);
        
        DumpParserFactory.get().getCurrentDumpParser().setDumpHistogramCounter(pos);
        openLoggcFile();
    }
    
    /**
     * close the currently selected dump.
     */
    private void closeCurrentDump() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        
        while(node != null && !checkNameFromNode(node, "Thread Dumps")) {
            node = (DefaultMutableTreeNode) node.getParent();
        }
        
        Object[] options = { "Close File", "Cancel close" };
        
        String fileName = node.getUserObject().toString();
        fileName = fileName.substring(fileName.indexOf('/'));
        
        JOptionPane.showOptionDialog(null, "<html><body>Are you sure, you want to close the currently selected dump file<br><b>" + fileName + 
                "</b></body></html>", "Confirm closing...",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
    }
        
    /**
     * check if name of node starts with passed string
     */
    private boolean checkNameFromNode(DefaultMutableTreeNode node, String startsWith) {
        Object info = node.getUserObject();
        String result = null;
        if(info instanceof ThreadInfo) {
            result = ((ThreadInfo) info).threadName;
        } else if (info instanceof DumpsBaseNode) {
            result = (String) ((DumpsBaseNode) info).getContent();
        }
        
        return(result != null && result.startsWith(startsWith));
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
            longThreadDialog.setLocationRelativeTo(frame);
            longThreadDialog.setVisible(true);
            
        }
    }
    
    private void openJMXConnection(boolean resetNodes) {
        if(jmxConnectionDialog == null) {
            jmxConnectionDialog = new JMXConnectDialog(this, frame, new DefaultMutableTreeNode());
            jmxConnectionDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
        
        frame.setEnabled(false);
        jmxConnectionDialog.reset();
        jmxConnectionDialog.pack();
        jmxConnectionDialog.setLocationRelativeTo(frame);
        jmxConnectionDialog.setVisible(true);
        
        if(resetNodes){
            topNodes = new Vector();
        }
    }
    
    public void addJMXConnection(RemoteConnection jmxConnection) {
        final DefaultMutableTreeNode top = new DefaultMutableTreeNode(jmxConnection);
        topNodes.add(top);
        // root nodes are moved down.
        setRootNodeLevel(1);
                        
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                createTree();
                
                return null;
            }
        };
        worker.start();
    }
    
    private int rootNodeLevel = 0;
    
    private int getRootNodeLevel() {
        return(rootNodeLevel);
    }
    
    private void setRootNodeLevel(int value) {
       rootNodeLevel = value;
    }
    
    private DefaultMutableTreeNode fetchTop(TreePath pathToRoot) {
        return((DefaultMutableTreeNode) pathToRoot.getPathComponent(getRootNodeLevel()));
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
        
        
        frame.getRootPane().setPreferredSize(PrefManager.get().getPreferredSize());
        
        //Create and set up the content pane.
        TDA newContentPane = new TDA();
        if(dumpFile != null) {
            newContentPane.init();
        }
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
        frame.setJMenuBar(new MainMenu(newContentPane));
        
        // init filechooser
        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(PrefManager.get().getSelectedPath());
        
        /**
         * add window listener for persisting state of main frame
         */
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
   
    /**
     * main startup method for TDA
     */
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
}
