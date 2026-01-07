package ua.kiev.univ.schedule.service.core.table;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.property.Property;
import ua.kiev.univ.schedule.util.Language;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseTableService<E extends Entity> implements TableService<E> {

    protected List<Property<?, E>> properties = new LinkedList<>();
    protected List<E> rows = new LinkedList<>();

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return properties.size();
    }

    @Override
    public String getColumnName(String key, int column) {
        return Language.getText(key + ".column." + properties.get(column).getName());
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return properties.get(column).getType();
    }

    protected boolean isCellEditable(E entity, Property<?, E> property) {
        return false;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        E entity = rows.get(row);
        Property<?, E> property = properties.get(column);
        return isCellEditable(entity, property);
    }

    protected <T> T getValue(Property<T, E> property, E entity) {
        return property.getValue(entity);
    }

    @Override
    public Object getValueAt(int row, int column) {
        E entity = rows.get(row);
        Property<?, E> property = properties.get(column);
        return getValue(property, entity);
    }

    // Helper method to capture generic type T
    protected <T> void setValue(Object value, Property<T, E> property, E entity) {
        @SuppressWarnings("unchecked")
        T castValue = (T) value;
        property.setValue(castValue, entity);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        E entity = rows.get(row);
        Property<?, E> property = properties.get(column);
        setValue(value, property, entity);
    }
}