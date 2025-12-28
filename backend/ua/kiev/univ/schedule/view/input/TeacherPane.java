package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.service.member.TeacherService;
import ua.kiev.univ.schedule.view.input.member.RestrictorPane;

public class TeacherPane extends RestrictorPane<Chair, Teacher> {

    public TeacherPane(InputPane inputPane, String key) {
        super(new TeacherService(), inputPane, key);
    }
}