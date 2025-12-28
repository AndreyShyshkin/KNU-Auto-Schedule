package ua.kiev.univ.schedule.view.input.table.cell;

import ua.kiev.univ.schedule.model.member.Year;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;

public class YearEditor extends AbstractCellEditor implements TableCellEditor {

    private final JComboBox<Year> comboBox;

    public YearEditor() {
        comboBox = new JComboBox<>(Year.values());
        comboBox.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Year year = (Year) value;
        comboBox.setSelectedItem(year);
        return comboBox;
    }
}