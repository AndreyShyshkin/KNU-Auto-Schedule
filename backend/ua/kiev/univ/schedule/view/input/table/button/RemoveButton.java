package ua.kiev.univ.schedule.view.input.table.button;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.table.EditableTableService;

public class RemoveButton<E extends Entity> extends TableButton<E> {

    public RemoveButton(EditableTableService<E> service, String key) {
        super(service, key);
        // Лямбда-вираз
        this.addActionListener(e -> service.removeRow());
    }

    @Override
    public void refresh() {
        this.setEnabled(service.isRemoveEnable());
    }
}