/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.java.dev.tda.visualvm;

import org.openide.modules.ModuleInstall;

/**
 *
 * @author irockel
 */
public class Install extends ModuleInstall {
    
    @Override
    public void restored() {
        try {
            TDAViewProvider.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
