package ua.kiev.univ.schedule.view.input.table;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.service.core.table.EditableTableService;
import ua.kiev.univ.schedule.view.core.Panel;
import ua.kiev.univ.schedule.view.input.table.button.AddButton;
import ua.kiev.univ.schedule.view.input.table.button.RemoveButton;

import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class TablePane<E extends Entity> extends Panel {

    public final TableLabel<E> label;
    public final EditableEntityTable<E> table;
    public final AddButton<E> addButton;
    public final RemoveButton<E> removeButton;

    public TablePane(EditableTableService<E> service, String key) {
        service.setTablePane(this);

        label = new TableLabel<>(service, key + ".label");
        this.table = new EditableEntityTable<>(service, key);
        this.addButton = new AddButton<>(service, key + ".add");
        this.removeButton = new RemoveButton<>(service, key + ".remove");

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridwidth = 2;

        constraints.gridy = 0;
        add(label, constraints);

        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        add(scrollPane, constraints);

        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.weightx = 0.5;
        constraints.gridy = 2;

        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        add(addButton, constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        add(removeButton, constraints);
    }

    @Override
    public void refresh() {
        table.refresh();
        label.refresh();
        addButton.refresh();
        removeButton.refresh();
    }

    @Override
    public void loadLanguage() {
        table.loadLanguage();
        label.loadLanguage();
        addButton.loadLanguage();
        removeButton.loadLanguage();
    }
}