package ua.kiev.univ.schedule.view.core;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.table.TableService;

import javax.swing.table.AbstractTableModel;

public class TableModel<E extends Entity> extends AbstractTableModel implements Refreshable, Internationalized {

    private final String key;
    protected TableService<E> service;

    public TableModel(TableService<E> service, String key) {
        this.service = service;
        this.key = key;
    }

    @Override
    public void refresh() {
        service.refreshRows();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return service.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return service.getColumnCount();
    }

    @Override
    public String getColumnName(int column) {
        return service.getColumnName(key, column);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return service.getColumnClass(column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return service.isCellEditable(row, column);
    }

    @Override
    public Object getValueAt(int row, int column) {
        return service.getValueAt(row, column);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        service.setValueAt(value, row, column);
    }

    @Override
    public void loadLanguage() {
        fireTableStructureChanged();
    }
}