package util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom table cell renderer that colour-codes cells based on their numeric value.
 * Green for positive, red for negative, yellow for zero.
 * Strips a configurable character (e.g. "€" or "%") before parsing the value.
 */
public class CellRenderer extends DefaultTableCellRenderer {

    private String stripChar;

    public CellRenderer(String stripChar) {
        this.stripChar = stripChar;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value != null) {
            try {
                double diff = Double.parseDouble(value.toString().replace("+", "").replace(stripChar, ""));

                if (diff > 0) {
                    c.setBackground(Color.GREEN);
                    c.setForeground(Color.BLACK);
                } else if (diff < 0) {
                    c.setBackground(Color.RED);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.YELLOW);
                    c.setForeground(Color.BLACK);
                }
            } catch (NumberFormatException e) {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }
        } else {
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
        }
        return c;
    }
}
