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
 * $Id: LogfileDumpView.java,v 1.2 2008-04-30 09:02:49 irockel Exp $
 */
package net.java.dev.tda.visualvm.logfile;

import com.pironet.tda.TDA;
import com.pironet.tda.utils.PrefManager;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.java.dev.tda.visualvm.TDAView;
import net.java.dev.tda.visualvm.LogPanel;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author irockel
 */
public class LogfileDumpView extends DataSourceView {
    private static final String IMAGE_PATH = "net/java/dev/tda/visualvm/resources/logfile.gif";  // NOI18N
    private Logfile logfile;
    private TDA tdaPanel;
    private JButton collapseAllButton;
    private JButton expandAllButton;
    private LogPanel logPanel = null;
    
    public LogfileDumpView(Logfile logfile) {
        super(logfile, NbBundle.getMessage(TDAView.class, "LBL_DumpView"), new ImageIcon(Utilities.loadImage(IMAGE_PATH, true)).getImage(), 0, false);    // NOI18N
        this.logfile = logfile;
    }

    @Override
    protected DataViewComponent createComponent() {
        tdaPanel = new TDA(false, logfile.getFile().getAbsolutePath());
        
        // init panel and set border
        tdaPanel.init(false, true);
        tdaPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // display the logfile
        tdaPanel.initDumpDisplay();
        
        tdaPanel.revalidate();
        
        logPanel = new LogPanel(tdaPanel);
        
        JPanel viewPanel = createView();
        DataViewComponent dvc = new DataViewComponent(new DataViewComponent.MasterView(NbBundle.getMessage(TDAView.class, 
                "MSG_DumpView"), null, viewPanel), 
                new DataViewComponent.MasterViewConfiguration(false));
        
        dvc.configureDetailsArea(new DataViewComponent.DetailsAreaConfiguration(NbBundle.getMessage(TDAView.class, 
                "LBL_Dump_results"), false), DataViewComponent.TOP_LEFT);   // NOI18N
        
        dvc.addDetailsView(new DataViewComponent.DetailsView(NbBundle.getMessage(TDAView.class, 
                "MSG_Dump_results"), null, 10, tdaPanel, null), DataViewComponent.TOP_LEFT);
        
        if(PrefManager.get().getMaxLogfileSize() * 1024 >= logfile.getFile().length()) {
            logPanel.setText(readText());
            dvc.addDetailsView(new DataViewComponent.DetailsView(NbBundle.getMessage(TDAView.class,
                    "MSG_Logfile"), null, 10, logPanel, null), DataViewComponent.TOP_LEFT);
        }
        
        return(dvc);
    }

    private JPanel createView() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 3, 0));
        
        collapseAllButton = new JButton(NbBundle.getMessage(TDAView.class, "LBL_CollapseTree"), TDA.createImageIcon("Collapsed.gif"));
        collapseAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tdaPanel.expandAllDumpNodes(false);
            }
        });

        expandAllButton = new JButton(NbBundle.getMessage(TDAView.class, "LBL_ExpandTree"), TDA.createImageIcon("Expanded.gif"));
        expandAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tdaPanel.expandAllDumpNodes(true);
            }
        });
        
        buttonPanel.add(new JLabel("<html><body><b>Dump Actions:"));
        buttonPanel.add(collapseAllButton);
        buttonPanel.add(expandAllButton);
                
        return(buttonPanel);
    }

    /**
     * read log file
     * 
     * @return
     */
    private String readText() {
        BufferedReader br = null;
        try {
            StringBuffer text = new StringBuffer();
            br = new BufferedReader(new FileReader(logfile.getFile()));
            while(br.ready()) {
                text.append(br.readLine());
                text.append("\n");
            }
            return(text.toString());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return("");
    }
}
