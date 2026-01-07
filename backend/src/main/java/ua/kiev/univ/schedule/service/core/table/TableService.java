package ua.kiev.univ.schedule.service.core.table;

import ua.kiev.univ.schedule.model.core.Entity;

public interface TableService<E extends Entity> {

    void refreshRows();

    int getRowCount();

    int getColumnCount();

    String getColumnName(String key, int column);

    Class<?> getColumnClass(int column);

    boolean isCellEditable(int row, int column);

    Object getValueAt(int row, int column);

    void setValueAt(Object value, int row, int column);
}