package ua.kiev.univ.schedule.view.input.member;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.service.member.MemberService;
import ua.kiev.univ.schedule.view.core.Label;
import ua.kiev.univ.schedule.view.core.Panel;
import ua.kiev.univ.schedule.view.input.table.TablePane;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class MemberPane<D extends Department, E extends Member<D>> extends Panel {

    public final Label facultyLabel;
    public final FacultyComboBox<D, E> facultyComboBox;
    public final Label departmentLabel;
    public final DepartmentComboBox<D, E> departmentComboBox;
    public final TablePane<E> tablePane;

    public MemberPane(MemberService<D, E> service, String key) {
        facultyLabel = new Label(key + ".faculty.label");
        departmentLabel = new Label(key + ".department.label");
        facultyComboBox = new FacultyComboBox<>(service);
        departmentComboBox = new DepartmentComboBox<>(service);
        tablePane = new TablePane<>(service, key);

        service.setDepartmentComboBox(departmentComboBox);

        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.weightx = 0.25;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.anchor = GridBagConstraints.LINE_END;

        constraints.gridx = 0;
        add(facultyLabel, constraints);

        constraints.gridx = 2;
        add(departmentLabel, constraints);

        constraints.anchor = GridBagConstraints.LINE_START;

        constraints.gridx = 1;
        add(facultyComboBox, constraints);

        constraints.gridx = 3;
        add(departmentComboBox, constraints);

        constraints = new GridBagConstraints();
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        add(tablePane, constraints);
    }

    @Override
    public void refresh() {
        facultyComboBox.refresh();
    }

    @Override
    public void loadLanguage() {
        facultyLabel.loadLanguage();
        departmentLabel.loadLanguage();
        tablePane.loadLanguage();
    }
}