package ua.kiev.univ.schedule.view.input.lesson.member;

import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.lesson.MemberedEntity;
import ua.kiev.univ.schedule.service.lesson.MemberedEntityService;
import ua.kiev.univ.schedule.view.core.ComboBox;

import java.awt.event.ActionListener;
import java.util.List;

public class FacultyComboBox<E extends MemberedEntity> extends ComboBox<Faculty> {

    private final MemberedEntityService<E> service;
    private final ActionListener listener;

    public FacultyComboBox(MemberedEntityService<E> service) {
        this.service = service;

        this.listener = e -> {
            Faculty faculty = (Faculty) getSelectedItem();
            this.service.setSelectedFaculty(faculty);
        };

        this.addActionListener(listener);
    }

    @Override
    public void refresh() {

        service.refreshFaculties();
        this.removeActionListener(listener);

        List<Faculty> faculties = service.getFaculties();
        this.removeAllItems();
        for (Faculty faculty : faculties) {
            this.addItem(faculty);
        }

        this.addActionListener(listener);
        this.setSelectedItem(service.getSelectedFaculty());
    }
}