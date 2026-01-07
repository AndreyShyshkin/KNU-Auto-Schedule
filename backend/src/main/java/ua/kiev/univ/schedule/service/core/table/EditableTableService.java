package ua.kiev.univ.schedule.service.core.table;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.DataService;
import ua.kiev.univ.schedule.service.core.EntityList;
import ua.kiev.univ.schedule.service.core.property.Property;
import ua.kiev.univ.schedule.view.input.table.TablePane;

public class EditableTableService<E extends Entity> extends BaseTableService<E> {

    protected TablePane<E> tablePane;
    public final EntityList<E> entities;
    public E selectedRow;

    public EditableTableService(Class<E> entityClass) {
        entities = DataService.getEntities(entityClass);
    }

    @Override
    public void refreshRows() {
        rows = entities;
    }

    @Override
    protected boolean isCellEditable(E entity, Property<?, E> property) {
        return true;
    }

    public void selectRow(int index) {
        selectedRow = (index >= 0) ? rows.get(index) : null;
        if (tablePane != null && tablePane.removeButton != null) {
            tablePane.removeButton.refresh();
        }
    }

    protected boolean isCellErroneous(E entity, Property<?, E> property) {
        return false;
    }

    public boolean isCellErroneous(int row, int column) {
        E entity = rows.get(row);
        Property<?, E> property = properties.get(column);
        return isCellErroneous(entity, property);
    }

    public boolean isAddEnable() {
        return true;
    }

    protected void onAdd(E entity) {}

    public void addRow() {
        E entity = entities.add();
        onAdd(entity);
        if (tablePane != null) {
            tablePane.refresh();
        }
    }

    public boolean isRemoveEnable() {
        return selectedRow != null;
    }

    protected void onRemove(E entity) {
        entity.onRemove();
    }

    public void removeRow() {
        entities.remove(selectedRow);
        onRemove(selectedRow);
        selectedRow = null;
        if (tablePane != null) {
            tablePane.refresh();
        }
    }

    public void setTablePane(TablePane<E> tablePane) {
        this.tablePane = tablePane;
    }
}