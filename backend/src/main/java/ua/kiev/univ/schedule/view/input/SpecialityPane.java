package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.service.department.SpecialityService;

public class SpecialityPane extends DepartmentPane<Speciality> {

    public SpecialityPane(InputPane inputPane, String key) {
        super(inputPane, new SpecialityService(), key);
    }
}