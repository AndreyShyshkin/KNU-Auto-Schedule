package ua.kiev.univ.schedule.view.input.lesson.member;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.service.lesson.MemberService;
import ua.kiev.univ.schedule.view.core.ComboBox;

import java.awt.event.ActionListener;
import java.util.List;

public class DepartmentComboBox<D extends Department, E extends Member<D>> extends ComboBox<D> {

    private final MemberService<D, E> service;
    private final ActionListener listener;

    public DepartmentComboBox(MemberService<D, E> service) {
        // 1. Спочатку присвоюємо значення сервісу
        this.service = service;

        // 2. Тепер безпечно створюємо слухача
        this.listener = e -> {
            @SuppressWarnings("unchecked")
            D department = (D) getSelectedItem();
            service.setSelectedDepartment(department);
        };

        this.addActionListener(listener);
    }

    @Override
    public void refresh() {
        service.refreshDepartments();
        this.removeActionListener(listener);

        List<D> departments = service.getDepartments();
        this.removeAllItems();
        for (D department : departments) {
            this.addItem(department);
        }

        this.addActionListener(listener);
        this.setSelectedItem(service.getSelectedDepartment());
    }
}