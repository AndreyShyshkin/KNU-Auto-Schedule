package ua.kiev.univ.schedule.view.input.member.restriction;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.member.Restrictor;
import ua.kiev.univ.schedule.service.member.RestrictionService;
import ua.kiev.univ.schedule.view.core.Label;
import ua.kiev.univ.schedule.view.core.Panel;

import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class RestrictionPane<D extends Department, E extends Restrictor<D>> extends Panel {

    private final Label label;
    private final RestrictionTable<D, E> table;

    public RestrictionPane(RestrictionService<D, E> service, String key) {
        label = new Label(key + ".label");
        table = new RestrictionTable<>(service);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1.0;

        constraints.gridy = 0;
        add(label, constraints);

        constraints.weighty = 1.0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        add(scrollPane, constraints);
    }

    @Override
    public void refresh() {
        table.refresh();
    }

    @Override
    public void loadLanguage() {
        label.loadLanguage();
        table.loadLanguage();
    }
}