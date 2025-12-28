package ua.kiev.univ.schedule.util;

import ua.kiev.univ.schedule.model.core.ActivableEntity;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.service.core.DataService;

import java.util.LinkedList;
import java.util.List;

public class EntityFilter {

    public static <E extends ActivableEntity> List<E> getActiveEntities(List<E> entities) {
        List<E> active = new LinkedList<>();
        for (E entity : entities) {
            if (entity.isActive()) {
                active.add(entity);
            }
        }
        return active;
    }

    public static <E extends ActivableEntity> List<E> getActiveEntities(Class<E> entityClass) {
        List<E> entities = DataService.getEntities(entityClass);
        return getActiveEntities(entities);
    }

    public static <E extends Department> List<E> getActiveDepartments(Class<E> entityClass, Faculty faculty) {
        List<E> active = new LinkedList<>();
        for (E entity : DataService.getEntities(entityClass)) {
            if (entity.getFaculty() == faculty && entity.isActive()) {
                active.add(entity);
            }
        }
        return active;
    }

    public static List<Date> getActiveDates() {
        List<Date> active = new LinkedList<>();
        for (Day day : DataService.getEntities(Day.class)) {
            for (Time time : day.getTimes()) {
                Date date = new Date(day, time);
                if (date.isActive()) {
                    active.add(date);
                }
            }
        }
        return active;
    }
}