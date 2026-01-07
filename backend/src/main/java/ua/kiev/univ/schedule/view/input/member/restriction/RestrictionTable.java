package ua.kiev.univ.schedule.view.input.member.restriction;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.member.Grade;
import ua.kiev.univ.schedule.model.member.Restriction;
import ua.kiev.univ.schedule.model.member.Restrictor;
import ua.kiev.univ.schedule.service.member.RestrictionService;
import ua.kiev.univ.schedule.view.core.Table;

public class RestrictionTable<D extends Department, E extends Restrictor<D>> extends Table<Restriction> {

    public RestrictionTable(RestrictionService<D, E> service) {
        super(service, null);
        this.setDefaultRenderer(Grade.class, new GradeRenderer());
        this.setDefaultEditor(Grade.class, new GradeEditor("grade"));
        this.setCellSelectionEnabled(false);
        this.setFocusable(false);
    }

    @Override
    public void refresh() {
        super.refresh();
        loadLanguage();
    }
}