package ua.kiev.univ.schedule.view.input.table.cell;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import java.awt.Component;

public class TextEditor extends AbstractCellEditor implements TableCellEditor {

    private final JTextField textField;

    public TextEditor() {
        textField = new JTextField();
        // Завершуємо редагування при натисканні Enter
        textField.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Object getCellEditorValue() {
        return textField.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textField.setText(value != null ? value.toString() : "");
        return textField;
    }
}