package ua.kiev.univ.schedule.view.input.table.cell;

import ua.kiev.univ.schedule.model.core.ActivableEntity;
import ua.kiev.univ.schedule.util.EntityFilter;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.util.List;

public class EntityEditor extends AbstractCellEditor implements TableCellEditor {

    private JComboBox<ActivableEntity> comboBox;

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Class<? extends ActivableEntity> entityClass = (Class<? extends ActivableEntity>) table.getColumnClass(column);
        List<? extends ActivableEntity> entities = EntityFilter.getActiveEntities(entityClass);

        comboBox = new JComboBox<>(entities.toArray(new ActivableEntity[0]));
        comboBox.setSelectedItem(value);
        comboBox.addActionListener(e -> fireEditingStopped());

        return comboBox;
    }
}