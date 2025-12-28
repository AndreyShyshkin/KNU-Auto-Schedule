package ua.kiev.univ.schedule.view.input.lesson.member;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.service.lesson.MemberService;
import ua.kiev.univ.schedule.view.core.Table;

import javax.swing.ListSelectionModel;

public class MemberTable<D extends Department, E extends Member<D>> extends Table<E> {

    public MemberTable(final MemberService<D, E> service, String key) {
        super(service, key);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}