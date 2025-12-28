package ua.kiev.univ.schedule.service.department;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.view.input.description.DescriptionedPane;

// E extends Department - це тип підлеглої сутності (наприклад, Chair або Speciality),
// якою керує цей факультет у конкретній вкладці інтерфейсу.
public class FacultyService<E extends Department> extends DescriptionedEntityService<Faculty> {

    private DescriptionedPane<E> departmentPane;

    public FacultyService() {
        super(Faculty.class);
    }

    @Override
    public void selectRow(int index) {
        super.selectRow(index);
        if (departmentPane != null) {
            departmentPane.refresh();
        }
    }

    public void setDepartmentPane(DescriptionedPane<E> departmentPane) {
        this.departmentPane = departmentPane;
    }
}