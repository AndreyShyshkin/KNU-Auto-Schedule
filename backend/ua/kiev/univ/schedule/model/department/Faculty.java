package ua.kiev.univ.schedule.model.department;

import ua.kiev.univ.schedule.service.core.DataService;

import java.util.Iterator;

public class Faculty extends DescriptionedEntity {

    @Override
    public void onRemove() {
        super.onRemove();
        Iterator<Speciality> iterator = DataService.getEntities(Speciality.class).iterator();
        while (iterator.hasNext()) {
            Speciality speciality = iterator.next();
            if (speciality.getFaculty() == this) {
                iterator.remove();
                speciality.onRemove();
            }
        }
    }
}