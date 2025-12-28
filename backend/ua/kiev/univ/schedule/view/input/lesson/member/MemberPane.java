package ua.kiev.univ.schedule.view.input.lesson.member;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.service.lesson.MemberService;
import ua.kiev.univ.schedule.view.core.Label;
import ua.kiev.univ.schedule.view.core.Panel;

import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class MemberPane<D extends Department, E extends Member<D>> extends Panel {

    public final Label departmentLabel;
    public final DepartmentComboBox<D, E> departmentComboBox;
    public Label label;
    public MemberTable<D, E> table;

    public MemberPane(MemberService<D, E> service, String key) {
        departmentLabel = new Label(key + ".department.label");
        departmentComboBox = new DepartmentComboBox<>(service);
        label = new Label(key + ".label");
        table = new MemberTable<>(service, key);

        service.setTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridy = 0;
        constraints.weightx = 0.5;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        add(departmentLabel, constraints);

        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 1;
        add(departmentComboBox, constraints);

        constraints = new GridBagConstraints();
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;

        constraints.gridy = 1;
        add(label, constraints);

        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        add(scrollPane, constraints);
    }

    @Override
    public void refresh() {
        departmentComboBox.refresh();
    }

    @Override
    public void loadLanguage() {
        departmentLabel.loadLanguage();
        label.loadLanguage();
        table.loadLanguage();
    }
}