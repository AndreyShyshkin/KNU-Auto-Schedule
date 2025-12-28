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

    private static <E extends Entity> void read(EntityList<E> list, DataInputStream is) throws IOException {
        list.clear();
        int count = is.readInt();
        while (count-- > 0) {
            E entity = list.add();
            entity.read(is);
        }
    }

    public static void read(File file) throws IOException {
        try (DataInputStream is = new DataInputStream(new FileInputStream(file))) {
            for (EntityList<?> list : entitiesMap.values()) {
                // TODO fix: оригінальна логіка пропускає читання Appointment
                if (list.entityClass != Appointment.class) {
                    // Безпечне приведення типів завдяки generic helper method
                    readHelper(list, is);
                } else {
                    list.clear();
                }
            }
        }
    }

    // Допоміжний метод для захоплення wildcard типу
    private static <E extends Entity> void readHelper(EntityList<E> list, DataInputStream is) throws IOException {
        read(list, is);
    }

    private static <E extends Entity> void write(EntityList<E> list, DataOutputStream os) throws IOException {
        os.writeInt(list.size());
        for (E entity : list) {
            entity.write(os);
        }
    }

    public static void write(File file) throws IOException {
        try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {
            for (EntityList<?> list : entitiesMap.values()) {
                // TODO fix: оригінальна логіка пропускає запис Appointment
                if (list.entityClass != Appointment.class) {
                    writeHelper(list, os);
                }
            }
        }
    }

    private static <E extends Entity> void writeHelper(EntityList<E> list, DataOutputStream os) throws IOException {
        write(list, os);
    }

    public static void writeResults(File file) throws IOException {
        try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {

            os.write(HtmlUtils.generateResultsHtml().getBytes());
        }
    }
}