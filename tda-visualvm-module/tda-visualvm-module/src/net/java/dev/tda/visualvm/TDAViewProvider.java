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
 * $Id: TDAViewProvider.java,v 1.4 2008-09-30 19:22:54 irockel Exp $
 */

package net.java.dev.tda.visualvm;

import com.sun.tools.visualvm.core.datasource.DataSource;
import com.sun.tools.visualvm.core.ui.DataSourceView;
import com.sun.tools.visualvm.core.ui.DataSourceViewProvider;
import com.sun.tools.visualvm.core.ui.DataSourceViewsManager;
import com.sun.tools.visualvm.threaddump.ThreadDump;
import net.java.dev.tda.visualvm.logfile.Logfile;

/**
 * provides a tda view.
 * 
 * @author irockel
 */
public class TDAViewProvider extends DataSourceViewProvider<DataSource> {
    static void initialize() {
        DataSourceViewsManager.sharedInstance().addViewProvider(new TDAViewProvider(), DataSource.class);
    }

    @Override
    protected boolean supportsViewFor(DataSource logContent) {
        return ((logContent instanceof ThreadDump) || (logContent instanceof Logfile));
    }

    @Override
    protected DataSourceView createView(DataSource logContent) {
        return(new TDAView(logContent));
    }
}
