package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.service.member.GroupService;
import ua.kiev.univ.schedule.view.input.member.RestrictorPane;

public class GroupPane extends RestrictorPane<Speciality, Group> {

    public GroupPane(InputPane inputPane, String key) {
        super(new GroupService(), inputPane, key);
    }
}