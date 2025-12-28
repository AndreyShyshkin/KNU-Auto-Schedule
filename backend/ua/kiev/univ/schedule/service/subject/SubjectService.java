package ua.kiev.univ.schedule.service.subject;

import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.service.core.NamedEntityService;

public class SubjectService extends NamedEntityService<Subject> {

    public SubjectService() {
        super(Subject.class);
    }
}