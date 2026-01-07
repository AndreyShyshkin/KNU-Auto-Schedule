package ua.kiev.univ.schedule.scheduler.point;

import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.Part;
import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.scheduler.ColorMap;
import ua.kiev.univ.schedule.scheduler.Progress;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepository;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepositoryFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Point {

    private final Subject subject;
    public List<Group> groups;
    private final List<Teacher> teachers;
    public int[] colors;
    public int[] maxes;
    public int earmark;
    public int size;
    public int[] restriction;
    public List<Point> verges = new LinkedList<>();
    public int[] first;
    public int[] second;
    public int both;
    public int part;

    public static Point getPoint(Lesson lesson, List<Date> dates, List<Earmark> earmarks, RestrictionMap restrictionMap) {
        if (lesson.getCount() % 2 == 0) {
            return new Point(lesson, dates, earmarks, restrictionMap);
        } else {
            return new HalvedPoint(lesson, dates, earmarks, restrictionMap);
        }
    }

    protected Point(Lesson lesson, List<Date> dates, List<Earmark> earmarks, RestrictionMap restrictionMap) {
        int count = dates.size();
        this.subject = lesson.getSubject();
        this.groups = new LinkedList<>(lesson.getGroups());
        this.teachers = new LinkedList<>(lesson.getTeachers());
        this.earmark = earmarks.indexOf(lesson.getEarmark());
        this.size = this.teachers.size();
        this.restriction = new int[count];

        restrictionMap.addRestrictions(lesson.getGroups(), this);
        restrictionMap.addRestrictions(lesson.getTeachers(), this);

        int pairCount = lesson.getCount() / 2;
        colors = new int[pairCount];
        maxes = new int[pairCount];

        Progress.DONE.value += pairCount;

        this.first = new int[count];
        this.second = new int[count];
        this.both = count;
    }

    private static <E extends Entity> boolean isIntersect(List<E> list1, List<E> list2) {
        for (E bean : list1) {
            if (list2.contains(bean)) {
                return true;
            }
        }
        return false;
    }

    public static void setVerges(List<Point> points) {
        List<Point> list = new LinkedList<>(points);
        for (Point point1 : points) {
            list.remove(point1);
            for (Point point2 : list) {
                // Якщо є спільні групи або викладачі — це ребро в графі (конфлікт)
                if (isIntersect(point1.groups, point2.groups) || isIntersect(point1.teachers, point2.teachers)) {
                    point1.verges.add(point2);
                    point2.verges.add(point1);
                }
            }
        }
    }

    public static void filterVerges(List<Point> points) {
        List<Point> list = new LinkedList<>();
        for (Point point : points) {
            point.verges.removeAll(list);
            list.add(point);
        }
    }

    protected void initAppointment(Appointment appointment, List<Date> dates, ColorMap colorMap, AuditoriumRepositoryFactory repositoryFactory) {
        appointment.setSubject(subject);
        appointment.setGroups(groups);
        appointment.setTeachers(teachers);

        Map<Date, List<Auditorium>> auditoriumMap = appointment.getAuditoriumMap();
        AuditoriumRepository repository = repositoryFactory.getAuditoriumRepository(Part.BOTH);

        for (int color : colors) {
            Date date = dates.get(colorMap.getDate(color));
            List<Auditorium> auditoriums = repository.getAuditoriums(color, earmark, size);
            auditoriumMap.put(date, auditoriums);
        }
    }

    public Appointment getAppointment(List<Date> dates, ColorMap colorMap, AuditoriumRepositoryFactory repositoryFactory) {
        Appointment appointment = new Appointment();
        initAppointment(appointment, dates, colorMap, repositoryFactory);
        return appointment;
    }
}