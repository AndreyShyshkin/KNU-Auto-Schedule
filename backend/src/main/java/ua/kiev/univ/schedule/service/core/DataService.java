package ua.kiev.univ.schedule.service.core;

import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.lesson.LessonType;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.util.HtmlUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataService {

    private static PersistenceService persistenceService;

    private static final Map<Class<? extends Entity>, EntityList<? extends Entity>> entitiesMap = new HashMap<>();

    static {
        addList(Building.class);
        addList(LessonType.class);
        addList(Time.class);
        addList(Day.class);
        addList(Faculty.class);
        addList(Earmark.class);
        addList(Subject.class);
        addList(Chair.class);
        addList(Speciality.class);
        addList(Auditorium.class);
        addList(Teacher.class);
        addList(Group.class);
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

    public static void setPersistenceService(PersistenceService service) {
        persistenceService = service;
    }

    public static void clear() {
        for (EntityList<? extends Entity> list : entitiesMap.values()) {
            list.clear();
        }
    }

    public static void read(File file) throws IOException {
        try (DataInputStream is = new DataInputStream(new FileInputStream(file))) {
            for (EntityList<? extends Entity> list : entitiesMap.values()) {
                list.clear();
                int size = is.readInt();
                while (size-- > 0) {
                    list.add().read(is);
                }
            }
        }
    }

    public static void write(File file) throws IOException {
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
