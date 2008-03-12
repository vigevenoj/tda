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
 * $Id: TDAViewProvider.java,v 1.2 2008-03-12 10:37:36 irockel Exp $
 */

package net.java.dev.tda.visualvm;

import com.sun.tools.visualvm.core.datasource.Application;
import com.sun.tools.visualvm.core.model.jmx.JmxModel;
import com.sun.tools.visualvm.core.model.jmx.JmxModelFactory;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.DataSourceViewsProvider;
import com.sun.tools.visualvm.core.ui.DataSourceWindowFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServerConnection;

/**
 * provides a tda view.
 * 
 * @author irockel
 */
public class TDAViewProvider implements DataSourceViewsProvider<Application> {
    private Map<Application, DataSourceView> viewsCache = new HashMap();

    public Set<? extends DataSourceView> getViews(Application application) {
        DataSourceView view = viewsCache.get(application);
        if (view == null) {
            view = new TDAView(application);
        }
        return Collections.singleton(view);
    }

    static void initialize() {
        DataSourceWindowFactory.sharedInstance().addViewProvider(new TDAViewProvider(), Application.class);
    }

    public boolean supportsViewsFor(Application application) {
        JmxModel jmx = JmxModelFactory.getJmxModelFor(application);
        MBeanServerConnection mbsc = jmx.getMBeanServerConnection();
        return mbsc != null;
    }
}
