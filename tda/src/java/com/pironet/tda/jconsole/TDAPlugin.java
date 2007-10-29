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
 * TDA is distributed in the hope that it will be useful,h
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * TDA should have received a copy of the Lesser GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: TDAPlugin.java,v 1.1 2007-10-29 17:20:22 irockel Exp $
 */

package com.pironet.tda.jconsole;

import com.pironet.tda.TDA;
import com.sun.tools.jconsole.JConsoleContext;
import com.sun.tools.jconsole.JConsoleContext.ConnectionState;
import com.sun.tools.jconsole.JConsolePlugin;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.swing.SwingWorker;

/**
 * The TDAPlugin capsulates TDA to be displayed in jconsole.
 */
public class TDAPlugin extends JConsolePlugin implements PropertyChangeListener
{
    private MBeanServerConnection server;
    private ThreadMXBean tmbean;
    private TDA tda = null;
    private Map tabs = null;

    public TDAPlugin() {
        // register itself as a listener
        addContextPropertyChangeListener(this);
    }

    /*
     * Returns a Thread Dumps tab to be added in JConsole.
     */
    public synchronized Map getTabs() {
        if (tabs == null) {
            try {
                tda = new TDA(false);
                tda.init(true);
                setMBeanServerConnection(getContext().getMBeanServerConnection());
                MBeanDumper.init(server);
                // use LinkedHashMap if you want a predictable order
                // of the tabs to be added in JConsole
                tabs = new LinkedHashMap();
                tabs.put("Thread Dumps", tda);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return tabs;
    }
    
    

    /*
     * Returns a SwingWorker which is responsible for updating the TDA tab.
     */
    public SwingWorker newSwingWorker() {
        return(new Worker(tda));
    }
    
    /** 
     * SwingWorker responsible for updating the GUI
     */
    class Worker extends SwingWorker {
        private TDA tda;
        Worker(TDA tda) {
            this.tda = tda;
        }
                                                                                
        // fire table data changed to trigger GUI update
        // when doInBackground() is finished
        protected void done() {
        }

        protected Object doInBackground() throws Exception {
            return null;
        }
    }    

    /*
     * Property listener to reset the MBeanServerConnection
     * at reconnection time.
     */
    public void propertyChange(PropertyChangeEvent ev) {
        String prop = ev.getPropertyName();
        if (JConsoleContext.CONNECTION_STATE_PROPERTY.equals(prop)) {
            ConnectionState oldState = (ConnectionState)ev.getOldValue();
            ConnectionState newState = (ConnectionState)ev.getNewValue();
            
            /* 
               JConsole supports disconnection and reconnection
               The MBeanServerConnection will become invalid when
               disconnected. Need to use the new MBeanServerConnection object
               created at reconnection time. 
             */
            if (newState == ConnectionState.CONNECTED && tda != null) {
                setMBeanServerConnection(getContext().getMBeanServerConnection());
            }
        }
    }

    private void setMBeanServerConnection(MBeanServerConnection mbs) {
        this.server = mbs;
        try {
            this.tmbean = (ThreadMXBean) ManagementFactory.newPlatformMXBeanProxy(server, ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
