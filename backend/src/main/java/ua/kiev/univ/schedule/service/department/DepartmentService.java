package ua.kiev.univ.schedule.service.department;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.department.Faculty;

public abstract class DepartmentService<E extends Department> extends DescriptionedEntityService<E> {

    protected FacultyService<E> facultyService;

    public DepartmentService(Class<E> entityClass) {
        super(entityClass);
    }

    @Override
    public void refreshRows() {
        rows.clear();
        if (facultyService != null && facultyService.selectedRow != null) {
            Faculty faculty = facultyService.selectedRow;
            for (E department : entities) {
                if (department.getFaculty() == faculty) {
                    rows.add(department);
                }
            }
        }
    }

    @Override
    public boolean isAddEnable() {
        return facultyService != null && facultyService.selectedRow != null;
    }

    @Override
    protected void onAdd(E department) {
        if (facultyService != null) {
            department.setFaculty(facultyService.selectedRow);
        }
    }

    public void setFacultyService(FacultyService<E> facultyService) {
        this.facultyService = facultyService;
    }
}