package ua.kiev.univ.schedule.service.member;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.service.core.NamedEntityService;
import ua.kiev.univ.schedule.util.EntityFilter;
import ua.kiev.univ.schedule.view.input.member.DepartmentComboBox;

import java.util.LinkedList;
import java.util.List;

public abstract class MemberService<D extends Department, E extends Member<D>> extends NamedEntityService<E> {

    protected DepartmentComboBox<D, E> departmentComboBox;
    protected Class<D> departmentClass;
    protected List<Faculty> faculties = new LinkedList<>();
    protected Faculty selectedFaculty;
    protected List<D> departments = new LinkedList<>();
    protected D selectedDepartment;

    public MemberService(Class<E> entityClass, Class<D> departmentClass) {
        super(entityClass);
        this.departmentClass = departmentClass;
    }

    public void refreshFaculties() {
        faculties = EntityFilter.getActiveEntities(Faculty.class);
        if (!faculties.contains(selectedFaculty)) {
            setSelectedFaculty(null);
        }
    }

    public List<Faculty> getFaculties() {
        return faculties;
    }

    public Faculty getSelectedFaculty() {
        return selectedFaculty;
    }

    public void setSelectedFaculty(Faculty faculty) {
        selectedFaculty = faculty;
        if (departmentComboBox != null) {
            departmentComboBox.refresh();
        }
    }

    public void refreshDepartments() {
        departments = EntityFilter.getActiveDepartments(departmentClass, selectedFaculty);
        if (!departments.contains(selectedDepartment)) {
            setSelectedDepartment(null);
        }
    }

    public List<D> getDepartments() {
        return departments;
    }

    public D getSelectedDepartment() {
        return selectedDepartment;
    }

    public void setSelectedDepartment(D department) {
        selectedDepartment = department;
        if (tablePane != null) {
            tablePane.refresh();
        }
    }

    @Override
    public void refreshRows() {
        rows.clear();
        for (E entity : entities) {
            if (entity.getDepartment() == selectedDepartment) {
                rows.add(entity);
            }
        }
    }

    @Override
    public boolean isAddEnable() {
        return selectedDepartment != null;
    }

    @Override
    protected void onAdd(E entity) {
        entity.setDepartment(selectedDepartment);
    }

    public void setDepartmentComboBox(DepartmentComboBox<D, E> departmentComboBox) {
        this.departmentComboBox = departmentComboBox;
    }
}