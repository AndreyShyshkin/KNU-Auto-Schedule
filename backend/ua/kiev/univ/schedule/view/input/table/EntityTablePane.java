package ua.kiev.univ.schedule.view.input.table;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.table.EditableTableService;

public class EntityTablePane<E extends Entity> extends TablePane<E> {

    public EntityTablePane(EditableTableService<E> service, String key) {
        super(service, key);
    }

    @Override
    public void loadLanguage() {
        super.loadLanguage();
    }
}