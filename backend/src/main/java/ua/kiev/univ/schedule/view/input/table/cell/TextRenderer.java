package ua.kiev.univ.schedule.view.input.table.cell;

import ua.kiev.univ.schedule.service.core.table.EditableTableService;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class TextRenderer extends DefaultTableCellRenderer {

    private static final Color ERROR_COLOR = new Color(255, 240, 240);
    private final EditableTableService<?> service;

    public TextRenderer(EditableTableService<?> service) {
        this.service = service;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected && service.isCellErroneous(row, column)) {
            c.setBackground(ERROR_COLOR);
        } else if (!isSelected) {
            c.setBackground(table.getBackground());
        }
        return c;
    }
}