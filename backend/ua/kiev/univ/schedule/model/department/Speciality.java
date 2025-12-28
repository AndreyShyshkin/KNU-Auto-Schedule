package ua.kiev.univ.schedule.model.department;

import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.service.core.DataService;

import java.util.Iterator;

public class Speciality extends Department {

    @Override
    public void onRemove() {
        super.onRemove();
        Iterator<Group> iterator = DataService.getEntities(Group.class).iterator();
        while (iterator.hasNext()) {
            Group group = iterator.next();

            if (group.getDepartment() == this) {
                iterator.remove();
                group.onRemove();
            }
        }
    }
}