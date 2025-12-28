package ua.kiev.univ.schedule.service.member;

import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.member.Teacher;

public class TeacherService extends RestrictorService<Chair, Teacher> {

    public TeacherService() {
        super(Teacher.class, Chair.class);
    }
}