package ua.kiev.univ.schedule.service.lesson;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.lesson.MemberedEntity;
import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.service.core.DataService;
import ua.kiev.univ.schedule.service.core.property.BeanProperty;
import ua.kiev.univ.schedule.service.core.property.Property;
import ua.kiev.univ.schedule.service.core.table.BaseTableService;
import ua.kiev.univ.schedule.util.EntityFilter;
import ua.kiev.univ.schedule.view.input.lesson.member.MemberTable;

import java.util.LinkedList;
import java.util.List;

public class MemberService<D extends Department, E extends Member<D>> extends BaseTableService<E> {

    protected MemberTable<D, E> table;
    protected MemberedEntityService<? extends MemberedEntity> service;
    public final Class<E> entityClass;
    protected Class<D> departmentClass;
    protected List<D> departments = new LinkedList<>();
    protected D selectedDepartment;

    protected Property<Boolean, E> belongProperty = new Property<Boolean, E>("belong", Boolean.class) {

        @Override
        public Boolean getValue(E member) {
            MemberedEntity entity = service.selectedRow;
            return (entity != null) && entity.getMembers(MemberService.this.entityClass).contains(member);
        }

        @Override
        public void setValue(Boolean value, E member) {
            MemberedEntity entity = service.selectedRow;
            if (entity != null) {
                List<E> members = entity.getMembers(MemberService.this.entityClass);
                if (Boolean.TRUE.equals(value)) {
                    members.add(member);
                } else {
                    members.remove(member);
                }
                service.onMembersChanged();
            }
        }
    };

    protected Property<String, E> nameProperty = new BeanProperty<>("name", String.class);
    protected Property<D, E> departmentProperty;

    public MemberService(Class<E> entityClass, Class<D> departmentClass, MemberedEntityService<? extends MemberedEntity> service) {
        this.entityClass = entityClass;
        this.departmentClass = departmentClass;
        this.service = service;
        departmentProperty = new BeanProperty<>("department", departmentClass);
        properties.add(belongProperty);
        properties.add(nameProperty);
        properties.add(departmentProperty);
    }

    public void refreshDepartments() {
        departments = EntityFilter.getActiveDepartments(departmentClass, service.selectedFaculty);
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
        if (table != null) {
            table.refresh();
        }
    }

    @Override
    public void refreshRows() {
        rows.clear();
        for (E entity : DataService.getEntities(entityClass)) {
            if ((entity.getDepartment() == selectedDepartment) && entity.isActive()) {
                rows.add(entity);
            }
        }
        // Додаємо учасників, які вже вибрані, навіть якщо вони з іншого департаменту (щоб не зникли з таблиці)
        if (service.selectedRow != null) {
            List<E> members = service.selectedRow.getMembers(entityClass);
            rows.removeAll(members);
            rows.addAll(members);
        }
    }

    @Override
    protected boolean isCellEditable(E entity, Property<?, E> property) {
        return (service.selectedRow != null) && (property == belongProperty);
    }

    public void setTable(MemberTable<D, E> table) {
        this.table = table;
    }
}