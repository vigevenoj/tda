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
 * $Id: TDAView.java,v 1.2 2008-03-12 10:51:46 irockel Exp $
 */

package net.java.dev.tda.visualvm;

import com.pironet.tda.TDA;
import com.pironet.tda.jconsole.MBeanDumper;
import com.sun.tools.visualvm.core.datasource.Application;
import com.sun.tools.visualvm.core.model.jmx.JmxModel;
import com.sun.tools.visualvm.core.model.jmx.JmxModelFactory;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.components.DataViewComponent;
import java.io.IOException;
import javax.management.MBeanServerConnection;
import javax.swing.ImageIcon;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 * tda main display view for visualvm.
 * 
 * @author Ingo Rockel <mailto:irockel@dev.java.net>
 */
public class TDAView extends DataSourceView {
    private static final String IMAGE_PATH = "net/java/dev/tda/visualvm/tda.png"; // NOI18N
    private Application application;
    DataViewComponent view;
    
    public TDAView(Application application) {
        super("Thread Dumps", new ImageIcon(Utilities.loadImage(IMAGE_PATH, true)).getImage(), 60);

        this.application = application;
        
        initComponent();
    }

    @Override
    public DataViewComponent getView() {
        // return tda view.
        return(view);
    }

    /**
     * init tda display component
     */
    private void initComponent() {
        if(view == null) {
            try {
                JmxModel jmx = JmxModelFactory.getJmxModelFor(application);
                MBeanServerConnection mbsc = jmx.getMBeanServerConnection();
                TDA tdaPanel = new TDA(false, new MBeanDumper(mbsc));
                tdaPanel.init(true, true);

                view = new DataViewComponent(new DataViewComponent.MasterView("Thread Dumps", null, tdaPanel), new DataViewComponent.MasterViewConfiguration(true));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
