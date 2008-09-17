/*
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
 * $Id: TDAView.java,v 1.4 2008-04-27 20:32:33 irockel Exp $
 */

package net.java.dev.tda.visualvm;

import com.pironet.tda.LogFileContent;
import com.pironet.tda.TDA;
import com.pironet.tda.jconsole.MBeanDumper;
import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import com.sun.tools.visualvm.tools.jmx.JmxModel;
import com.sun.tools.visualvm.tools.jmx.JmxModelFactory;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.management.MBeanServerConnection;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * tda main display view for visualvm.
 * 
 * @author Ingo Rockel <mailto:irockel@dev.java.net>
 */
public class TDAView extends DataSourceView {
    private static final String IMAGE_PATH = "net/java/dev/tda/visualvm/resources/tda.gif"; // NOI18N
    private Application application;
    
    private JButton requestDumpButton = null;
    private JButton collapseAllButton = null;
    private JButton expandAllButton = null;
    private TDA tdaPanel = null;
    private LogPanel logPanel = null;
    
    public TDAView(Application application) {
        super(application, "Thread Dump Analyzer", new ImageIcon(Utilities.loadImage(IMAGE_PATH, true)).getImage(), 60, false);

        this.application = application;
    }

    @Override
    protected DataViewComponent createComponent() {
        JmxModel jmx = JmxModelFactory.getJmxModelFor(application);
        MBeanServerConnection mbsc = jmx.getMBeanServerConnection();
        try {
            tdaPanel = new TDA(false, new MBeanDumper(mbsc));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        tdaPanel.init(true, true);
        tdaPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        logPanel = new LogPanel(tdaPanel);
        
        JPanel viewPanel = createView();
        
        DataViewComponent dvc = new DataViewComponent(new DataViewComponent.MasterView(NbBundle.getMessage(TDAView.class, 
                "MSG_Dump"), null, viewPanel), 
                new DataViewComponent.MasterViewConfiguration(false));
        
        dvc.configureDetailsArea(new DataViewComponent.DetailsAreaConfiguration(NbBundle.getMessage(TDAView.class, 
                "LBL_Dump_results"), false), DataViewComponent.TOP_LEFT);   // NOI18N
        
        //dvc.hideDetailsArea(DataViewComponent.TOP_RIGHT);
        
        dvc.addDetailsView(new DataViewComponent.DetailsView(NbBundle.getMessage(TDAView.class, 
                "MSG_Dump_results"), null, 10, tdaPanel, null), DataViewComponent.TOP_LEFT);
        
        dvc.addDetailsView(new DataViewComponent.DetailsView(NbBundle.getMessage(TDAView.class, 
                "MSG_Logfile"), null, 10, logPanel, null), DataViewComponent.TOP_LEFT);

        return(dvc);
    }
    
    private JPanel createView() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 3, 0));
        
        requestDumpButton = new JButton(NbBundle.getMessage(TDAView.class, "LBL_RequestDump"));  // NOI18N
        requestDumpButton.setIcon(TDA.createImageIcon("FileOpen.gif"));   // NOI18N

        requestDumpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LogFileContent lfc = tdaPanel.addMXBeanDump();
                logPanel.setText(lfc.getContent());
                logPanel.setCaretPosition(0);
                collapseAllButton.setEnabled(true);
                expandAllButton.setEnabled(true);
            }
        });

        collapseAllButton = new JButton(NbBundle.getMessage(TDAView.class, "LBL_CollapseTree"), TDA.createImageIcon("Collapsed.gif"));
        collapseAllButton.setEnabled(false);
        collapseAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tdaPanel.expandAllDumpNodes(false);
            }
        });

        expandAllButton = new JButton(NbBundle.getMessage(TDAView.class, "LBL_ExpandTree"), TDA.createImageIcon("Expanded.gif"));
        expandAllButton.setEnabled(false);        
        expandAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tdaPanel.expandAllDumpNodes(true);
            }
        });
        
        buttonPanel.add(new JLabel("<html><body><b>Dump Actions:"));
        buttonPanel.add(requestDumpButton);
        buttonPanel.add(collapseAllButton);
        buttonPanel.add(expandAllButton);
                
        return(buttonPanel);
    }
    
}