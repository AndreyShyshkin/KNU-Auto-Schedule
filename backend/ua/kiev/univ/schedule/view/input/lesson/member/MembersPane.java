package ua.kiev.univ.schedule.view.input.lesson.member;

import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.lesson.MemberedEntity;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.service.lesson.MemberService;
import ua.kiev.univ.schedule.service.lesson.MemberedEntityService;
import ua.kiev.univ.schedule.view.core.Label;
import ua.kiev.univ.schedule.view.core.Panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class MembersPane<E extends MemberedEntity> extends Panel {

    public final Label facultyLabel;
    public final FacultyComboBox<E> facultyComboBox;
    public final MemberPane<Chair, Teacher> teacherPane;
    public final MemberPane<Speciality, Group> groupPane;

    public MembersPane(MemberedEntityService<E> service, String key) {
        facultyLabel = new Label(key + ".faculty.label");
        facultyComboBox = new FacultyComboBox<>(service);

        teacherPane = new MemberPane<>(new MemberService<>(Teacher.class, Chair.class, service), key + ".teacher");
        groupPane = new MemberPane<>(new MemberService<>(Group.class, Speciality.class, service), key + ".group");

        service.setTeacherPane(teacherPane);
        service.setGroupPane(groupPane);

        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.weightx = 0.5;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        add(facultyLabel, constraints);

        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 1;
        add(facultyComboBox, constraints);

        constraints = new GridBagConstraints();
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 0.5;

        constraints.gridy = 1;
        add(teacherPane, constraints);

        constraints.gridy = 2;
        add(groupPane, constraints);
    }

    @Override
    public void refresh() {
        facultyComboBox.refresh();
    }

    @Override
    public void loadLanguage() {
        facultyLabel.loadLanguage();
        teacherPane.loadLanguage();
        groupPane.loadLanguage();
    }
}