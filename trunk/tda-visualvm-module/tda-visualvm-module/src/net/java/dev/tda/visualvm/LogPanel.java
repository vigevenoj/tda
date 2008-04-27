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
 * $Id: LogPanel.java,v 1.1 2008-04-27 20:32:33 irockel Exp $
 */
package net.java.dev.tda.visualvm;

import com.pironet.tda.TDA;
import com.pironet.tda.utils.jedit.JEditTextArea;
import com.pironet.tda.utils.jedit.PopupMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 *
 * @author irockel
 */
public class LogPanel extends JPanel {
    JEditorPane editComp = new JEditorPane();
    
    public LogPanel(TDA ref) {
        super(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        editComp = new JEditorPane();
        editComp.setFont(Font.getFont(Font.MONOSPACED));
        
        JScrollPane scrollPane = new JScrollPane(editComp);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        
        editComp.setEditable(false);
        editComp.setBackground(Color.WHITE);
        setOpaque(true);
        setBackground(Color.WHITE);
        
        //editComp.setCaretVisible(false);
        //editComp.setCaretBlinkEnabled(false);
        //setRightClickPopup(new PopupMenu(this, ref, true));
        //getInputHandler().addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), (ActionListener) getRightClickPopup());
        //getInputHandler().addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK), (ActionListener) getRightClickPopup());
    }

    public void setCaretPosition(int i) {
        editComp.setCaretPosition(i);
    }

    public void setText(String content) {
        editComp.setText(content);
    }
}
