/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pironet.tda.utils;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * GrayWhiteTable renders its rows with a sequential color combination of white
 * and gray. Rows with even indicies are rendered white, odd indicies light grey.
 * Note: Do not use GrayWhiteTable for tables with custom renderers such as
 * check boxes. Use JTable instead and modify DefaultTableCellRenderer. Just keep
 * in mind that in order to display a table with more than 1 row colors, you
 * must have 2 separate intances of the renderer, one for each color.
 * 
 * @author irockel
 */
public class ColoredTable extends JTable {

    private DefaultTableCellRenderer whiteRenderer;
    private DefaultTableCellRenderer grayRenderer;

    public ColoredTable() {
        super();
    }

    public ColoredTable(TableModel tm) {
        super(tm);
    }

    public ColoredTable(Object[][] data, Object[] columns) {
        super(data, columns);
    }

    public ColoredTable(int rows, int columns) {
        super(rows, columns);
    }

    /**
     * If row is an even number, getCellRenderer() returns a DefaultTableCellRenderer
     * with white background. For odd rows, this method returns a DefaultTableCellRenderer
     * with a light gray background.
     */
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (whiteRenderer == null) {
            whiteRenderer = new DefaultTableCellRenderer();
            whiteRenderer.setBackground(Color.WHITE);
        }

        if (grayRenderer == null) {
            grayRenderer = new DefaultTableCellRenderer();
            grayRenderer.setBackground(new Color(240, 240, 240));
        }

        if ((row % 2) == 0) {
            return whiteRenderer;
        } else {
            return grayRenderer;
        }
    }
}
