package ua.kiev.univ.schedule.service.lesson;

import ua.kiev.univ.schedule.model.lesson.SubjectedEntity;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.service.core.EnablableEntityService;
import ua.kiev.univ.schedule.service.core.property.BeanProperty;
import ua.kiev.univ.schedule.service.core.property.Property;
import ua.kiev.univ.schedule.util.EntityFilter;

import java.util.LinkedList;
import java.util.List;

public class SubjectedEntityService<E extends SubjectedEntity> extends EnablableEntityService<E> {

    protected Property<Subject, E> subjectProperty = new BeanProperty<>("subject", Subject.class);
    protected List<Subject> subjects = new LinkedList<>();
    protected Subject selectedSubject;

    public SubjectedEntityService(Class<E> entityClass) {
        super(entityClass);
        properties.add(subjectProperty);
    }

    public void refreshSubjects() {
        subjects = EntityFilter.getActiveEntities(Subject.class);
        if (!subjects.contains(selectedSubject)) {
            setSelectedSubject(null);
        }
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public Subject getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(Subject subject) {
        selectedSubject = subject;
        if (tablePane != null) {
            tablePane.refresh();
        }
    }

    @Override
    public void refreshRows() {
        rows.clear();
        for (E entity : entities) {
            if ((selectedSubject == null) || (selectedSubject == entity.getSubject())) {
                rows.add(entity);
            }
        }
    }

    @Override
    protected boolean isCellErroneous(E entity, Property<?, E> property) {
        if (property == subjectProperty) {
            return (subjectProperty.getValue(entity) == null);
        }
        return super.isCellErroneous(entity, property);
    }

    @Override
    protected void onAdd(E entity) {
        entity.setSubject(selectedSubject);
    }
}