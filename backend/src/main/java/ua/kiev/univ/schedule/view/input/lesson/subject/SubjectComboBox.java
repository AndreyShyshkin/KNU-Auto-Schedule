package ua.kiev.univ.schedule.view.input.lesson.subject;

import ua.kiev.univ.schedule.model.lesson.SubjectedEntity;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.service.lesson.SubjectedEntityService;
import ua.kiev.univ.schedule.view.core.ComboBox;

import java.awt.event.ActionListener;
import java.util.List;

public class SubjectComboBox<E extends SubjectedEntity> extends ComboBox<Subject> {

    private final SubjectedEntityService<E> service;
    private final ActionListener listener;

    public SubjectComboBox(SubjectedEntityService<E> service) {
        this.service = service;


        this.listener = e -> {
            Subject subject = (Subject) getSelectedItem();
            this.service.setSelectedSubject(subject);
        };

        this.addActionListener(listener);
    }

    @Override
    public void refresh() {
        service.refreshSubjects();
        this.removeActionListener(listener);

        List<Subject> subjects = service.getSubjects();
        this.removeAllItems();
        this.addItem(null);
        for (Subject subject : subjects) {
            this.addItem(subject);
        }

        this.addActionListener(listener);
        this.setSelectedItem(service.getSelectedSubject());
    }
}