package ua.kiev.univ.schedule.service.core;

import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.util.HtmlUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataService {

    private static final Map<Class<? extends Entity>, EntityList<?>> entitiesMap = new LinkedHashMap<>();

    static {
        addList(Time.class);
        addList(Day.class);
        addList(Faculty.class);
        addList(Chair.class);
        addList(Speciality.class);
        addList(Teacher.class);
        addList(Group.class);
        addList(Earmark.class);
        addList(Auditorium.class);
        addList(Subject.class);
        addList(Lesson.class);
        addList(Appointment.class);
    }

    private static <E extends Entity> void addList(Class<E> entityClass) {
        entitiesMap.put(entityClass, new EntityList<>(entityClass));
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity> EntityList<E> getEntities(Class<E> entityClass) {
        return (EntityList<E>) entitiesMap.get(entityClass);
    }

    public static void clear() {
        for (EntityList<?> list : entitiesMap.values()) {
            list.clear();
        }
    }

    private static DataInitializationService persistenceService;

    public static void setPersistenceService(DataInitializationService service) {
        persistenceService = service;
    }

    public static void read(File file) throws IOException {
        System.out.println("Reloading data (ignoring file argument)...");
        if (persistenceService != null) {
            persistenceService.initializeData();
        }
    }

    public static void write(File file) throws IOException {
        System.out.println("Saving to Database via Spring Data...");
        if (persistenceService != null) {
            persistenceService.saveAll();
        }
    }

    public static void writeResults(File file) throws IOException {
        try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {

            os.write(HtmlUtils.generateResultsHtml().getBytes());
        }
    }
}