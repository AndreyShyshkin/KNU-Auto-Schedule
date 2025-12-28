package ua.kiev.univ.schedule.view.input.table.cell;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;
import java.awt.Component;

public class IntegerEditor extends AbstractCellEditor implements TableCellEditor {

    private final JSpinner spinner;

    public IntegerEditor() {
        SpinnerModel model = new SpinnerNumberModel(0, 0, 1000, 1);
        spinner = new JSpinner(model);
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        spinner.setValue(value);
        return spinner;
    }
}