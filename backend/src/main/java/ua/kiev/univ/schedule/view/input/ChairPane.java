package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.service.department.ChairService;

public class ChairPane extends DepartmentPane<Chair> {

    public ChairPane(InputPane inputPane, String key) {
        super(inputPane, new ChairService(), key);
    }
}