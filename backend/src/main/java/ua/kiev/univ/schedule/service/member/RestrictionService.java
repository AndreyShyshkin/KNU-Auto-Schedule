package ua.kiev.univ.schedule.service.member;

import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.member.Grade;
import ua.kiev.univ.schedule.model.member.Restriction;
import ua.kiev.univ.schedule.model.member.Restrictor;
import ua.kiev.univ.schedule.service.core.table.TableService;
import ua.kiev.univ.schedule.util.EntityFilter;

import java.util.LinkedList;
import java.util.List;

public class RestrictionService<D extends Department, E extends Restrictor<D>> implements TableService<Restriction> {

    private final RestrictorService<D, E> service;
    private List<Day> days = new LinkedList<>();
    private List<Time> times = new LinkedList<>();

    public RestrictionService(RestrictorService<D, E> service) {
        this.service = service;
    }

    @Override
    public void refreshRows() {
        days = EntityFilter.getActiveEntities(Day.class);
        times = EntityFilter.getActiveEntities(Time.class);
    }

    @Override
    public int getRowCount() {
        return times.size();
    }

    @Override
    public int getColumnCount() {
        return days.size();
    }

    @Override
    public String getColumnName(String key, int column) {
        return days.get(column).toString();
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return Grade.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // Редагувати можна тільки якщо вибрано людину і цей час доступний у цей день
        return (service.selectedRow != null) && days.get(column).getTimes().contains(times.get(row));
    }

    @Override
    public Object getValueAt(int row, int column) {
        Day day = days.get(column);
        Time time = times.get(row);
        // Якщо час є в розкладі дня -> повертаємо оцінку обмеження (або NONE)
        if ((service.selectedRow != null) && day.getTimes().contains(time)) {
            return service.selectedRow.getRestriction().getGrade(new Date(day, time));
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        Day day = days.get(column);
        Time time = times.get(row);
        if (service.selectedRow != null) {
            service.selectedRow.getRestriction().setGrade((Grade) value, new Date(day, time));
        }
    }
}