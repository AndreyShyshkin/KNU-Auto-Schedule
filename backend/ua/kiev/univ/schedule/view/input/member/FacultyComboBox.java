package ua.kiev.univ.schedule.view.input.member;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.service.member.MemberService;
import ua.kiev.univ.schedule.view.core.ComboBox;

import java.awt.event.ActionListener;
import java.util.List;

public class FacultyComboBox<D extends Department, E extends Member<D>> extends ComboBox<Faculty> {

    private final MemberService<D, E> service;
    private final ActionListener listener;

    public FacultyComboBox(MemberService<D, E> service) {

        this.service = service;


        this.listener = e -> {
            Faculty faculty = (Faculty) getSelectedItem();
            service.setSelectedFaculty(faculty);
        };

        this.addActionListener(listener);
    }

    @Override
    public void refresh() {
        service.refreshFaculties();
        // Видаляємо слухача перед зміною елементів
        this.removeActionListener(listener);

        List<Faculty> faculties = service.getFaculties();
        this.removeAllItems();
        for (Faculty faculty : faculties) {
            this.addItem(faculty);
        }

        // Повертаємо слухача
        this.addActionListener(listener);
        this.setSelectedItem(service.getSelectedFaculty());
    }
}