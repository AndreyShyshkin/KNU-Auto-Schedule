package ua.kiev.univ.schedule.service.member;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.member.Restrictor;
import ua.kiev.univ.schedule.view.input.member.restriction.RestrictionPane;

public abstract class RestrictorService<D extends Department, E extends Restrictor<D>> extends MemberService<D, E> {

    private RestrictionPane<D, E> restrictionPane;

    public RestrictorService(Class<E> entityClass, Class<D> departmentClass) {
        super(entityClass, departmentClass);
    }

    @Override
    public void selectRow(int index) {
        super.selectRow(index);
        if (restrictionPane != null) {
            restrictionPane.refresh();
        }
    }

    public void setRestrictionPane(RestrictionPane<D, E> restrictionPane) {
        this.restrictionPane = restrictionPane;
    }
}