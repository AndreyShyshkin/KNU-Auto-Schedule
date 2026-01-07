package ua.kiev.univ.schedule.view.input.table;

import ua.kiev.univ.schedule.model.core.ActivableEntity;
import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.model.member.Year;
import ua.kiev.univ.schedule.service.core.table.EditableTableService;
import ua.kiev.univ.schedule.view.core.Table;
import ua.kiev.univ.schedule.view.input.table.cell.*;

import javax.swing.ListSelectionModel;

public class EditableEntityTable<E extends Entity> extends Table<E> {

    public EditableEntityTable(final EditableTableService<E> service, String key) {
        super(service, key);

        // Налаштування відображення та редагування
        setDefaultRenderer(String.class, new TextRenderer(service));
        setDefaultEditor(String.class, new TextEditor()); // Додано явний редактор для рядків

        setDefaultEditor(Integer.class, new IntegerEditor());

        setDefaultRenderer(ActivableEntity.class, new EntityRenderer(service));
        setDefaultEditor(ActivableEntity.class, new EntityEditor());

        setDefaultEditor(Year.class, new YearEditor());

        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Лямбда-вираз для обробки виділення рядка
        getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = EditableEntityTable.this.getSelectedRow();
                service.selectRow(index);
            }
        });
    }
}