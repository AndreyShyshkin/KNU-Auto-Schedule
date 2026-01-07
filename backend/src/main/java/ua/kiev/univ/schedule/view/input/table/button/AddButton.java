package ua.kiev.univ.schedule.view.input.table.button;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.table.EditableTableService;

public class AddButton<E extends Entity> extends TableButton<E> {

    public AddButton(EditableTableService<E> service, String key) {
        super(service, key);
        // Використовуємо лямбда-вираз замість анонімного класу
        this.addActionListener(e -> service.addRow());
    }

    @Override
    public void refresh() {
        this.setEnabled(service.isAddEnable());
    }
}