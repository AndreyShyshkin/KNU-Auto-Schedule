package ua.kiev.univ.schedule.view.input.table.button;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.table.EditableTableService;
import ua.kiev.univ.schedule.view.core.Button;

public abstract class TableButton<E extends Entity> extends Button {

    protected EditableTableService<E> service;

    public TableButton(EditableTableService<E> service, String key) {
        super(key);
        this.service = service;
    }
}