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
 * $Id: TDAViewProvider.java,v 1.3 2008-04-18 10:57:10 irockel Exp $
 */

package net.java.dev.tda.visualvm;

import com.sun.tools.visualvm.application.Application;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.DataSourceViewProvider;
import com.sun.tools.visualvm.core.ui.DataSourceViewsManager;
import com.sun.tools.visualvm.tools.jmx.JmxModel;
import com.sun.tools.visualvm.tools.jmx.JmxModelFactory;
import javax.management.MBeanServerConnection;

/**
 * provides a tda view.
 * 
 * @author irockel
 */
public class TDAViewProvider extends DataSourceViewProvider<Application> {
    static void initialize() {
        DataSourceViewsManager.sharedInstance().addViewProvider(new TDAViewProvider(), Application.class);
    }

    @Override
    protected boolean supportsViewFor(Application application) {
        JmxModel jmx = JmxModelFactory.getJmxModelFor(application);
        MBeanServerConnection mbsc = jmx.getMBeanServerConnection();
        return mbsc != null;
    }

    @Override
    protected DataSourceView createView(Application application) {
        return(new TDAView(application));
    }
}
