package ua.kiev.univ.schedule.view.input.table;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.table.EditableTableService;
import ua.kiev.univ.schedule.view.core.Label;
import ua.kiev.univ.schedule.view.core.Refreshable;

public class TableLabel<E extends Entity> extends Label implements Refreshable {

    private String text;
    private final EditableTableService<E> service;

    public TableLabel(EditableTableService<E> service, String key) {
        super(key);
        this.service = service;
    }

    @Override
    public void loadLanguage() {
        text = getLabelText();
        this.refresh();
    }

    @Override
    public void refresh() {
        // service.entities доступний завдяки public полю в EditableTableService
        setText(text + " " + service.getRowCount() + "/" + service.entities.size());
    }
}