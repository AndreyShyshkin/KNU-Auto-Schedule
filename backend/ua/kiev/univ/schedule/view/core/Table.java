package ua.kiev.univ.schedule.view.core;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.table.TableService;

import javax.swing.JTable;

public abstract class Table<E extends Entity> extends JTable implements Refreshable, Internationalized {

    protected TableModel<E> model;

    public Table(TableService<E> service, String key) {
        model = new TableModel<>(service, key);
        this.setModel(model);
    }

    @Override
    public void refresh() {
        model.refresh();
    }

    @Override
    public void loadLanguage() {
        model.loadLanguage();
    }
}