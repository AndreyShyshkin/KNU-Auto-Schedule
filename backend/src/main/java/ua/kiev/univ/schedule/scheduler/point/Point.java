package ua.kiev.univ.schedule.scheduler.point;

import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.Part;
import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.scheduler.ColorMap;
import ua.kiev.univ.schedule.scheduler.Progress;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepository;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepositoryFactory;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.BuildingEarmark;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Point {

    private final Subject subject;
    private final Auditorium fixedAuditorium;
    private final Building building;
    public final boolean online;
    private final String onlineLink;
    private final String earmarkName;
    private final String lessonTypeNames;
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

    public static Point getPoint(Lesson lesson, List<Date> dates, List<BuildingEarmark> types, RestrictionMap restrictionMap) {
        if (lesson.getCount() % 2 == 0) {
            return new Point(lesson, dates, types, restrictionMap);
        } else {
            return new HalvedPoint(lesson, dates, types, restrictionMap);
        }
    }

    protected Point(Lesson lesson, List<Date> dates, List<BuildingEarmark> types, RestrictionMap restrictionMap) {
        int count = dates.size();
        this.subject = lesson.getSubject();
        this.fixedAuditorium = lesson.getAuditorium();
        this.online = lesson.isOnline();
        this.onlineLink = lesson.getOnlineLink();
        this.earmarkName = lesson.getEarmark() != null ? lesson.getEarmark().getName() : "";
        this.lessonTypeNames = lesson.getLessonTypes() != null ? 
            lesson.getLessonTypes().stream().map(t -> t.getName()).collect(Collectors.joining(", ")) : "";

        // Пріоритет: будівля з уроку -> будівля з обраної аудиторії -> null
        this.building = lesson.getBuilding() != null ? lesson.getBuilding() : (fixedAuditorium != null ? fixedAuditorium.getBuilding() : null);
        
        this.groups = new LinkedList<>(lesson.getGroups());
        this.teachers = new LinkedList<>(lesson.getTeachers());
        
        int totalStudents = groups.stream().mapToInt(g -> g.getSize() != null ? g.getSize() : 0).sum();
        int initialPairCount = lesson.getCount() / 2;
        int multiplier = 1;
        int calculatedSize = 1;
        int index = -1;

        // Якщо онлайн, тип аудиторії не потрібен
        if (this.online) {
            this.earmark = -1;
            this.size = 0;
        } else {
            int teacherCount = this.teachers.size() > 0 ? this.teachers.size() : 1;
            
            // Шукаємо підходящий тип аудиторії
            for (int i = 0; i < types.size(); i++) {
                BuildingEarmark type = types.get(i);
                if (Objects.equals(type.getBuilding(), this.building) && 
                    Objects.equals(type.getEarmark(), lesson.getEarmark())) {
                    
                    Integer audSize = type.getEarmark() != null ? type.getEarmark().getSize() : null;
                    if (audSize == null || audSize <= 0) audSize = 49; // Дефолт для вашого корпусу
                    
                    if (audSize >= totalStudents) {
                        index = i;
                        calculatedSize = 1;
                        multiplier = 1;
                        break;
                    } else if (lesson.isAllowMultipleAuditoriums()) {
                        int needed = (int) Math.ceil((double) totalStudents / audSize);
                        if (teacherCount >= needed) {
                            // Є вчителі для паралельних занять (один час, багато залів)
                            index = i;
                            calculatedSize = needed;
                            multiplier = 1;
                        } else {
                            // Вчителів мало, проводимо послідовно (різний час, один зал)
                            index = i;
                            calculatedSize = 1;
                            multiplier = needed;
                        }
                        break;
                    }
                }
            }
            
            // Fallback: шукаємо будь-де
            if (index == -1) {
                for (int i = 0; i < types.size(); i++) {
                    Earmark e = types.get(i).getEarmark();
                    if (e != null && Objects.equals(e, lesson.getEarmark())) {
                        Integer audSize = e.getSize();
                        if (audSize == null || audSize <= 0) audSize = 49;

                        if (audSize >= totalStudents) {
                            index = i;
                            calculatedSize = 1;
                            multiplier = 1;
                            break;
                        } else if (lesson.isAllowMultipleAuditoriums()) {
                            int needed = (int) Math.ceil((double) totalStudents / audSize);
                            if (teacherCount >= needed) {
                                index = i;
                                calculatedSize = needed;
                                multiplier = 1;
                            } else {
                                index = i;
                                calculatedSize = 1;
                                multiplier = needed;
                            }
                            break;
                        }
                    }
                }
            }

            this.earmark = index;
            this.size = calculatedSize;

            if (this.earmark == -1) {
                System.out.println("DEBUG: Lesson '" + lesson.getSubject().getName() + "' REJECTED: Немає підходящих аудиторій");
            } else if (multiplier > 1) {
                System.out.println("DEBUG: Lesson '" + lesson.getSubject().getName() + "' split into " + multiplier + " time slots");
            }
        }
        
        this.restriction = new int[count];

        restrictionMap.addRestrictions(lesson.getGroups(), this);
        restrictionMap.addRestrictions(lesson.getTeachers(), this);

        // Обмеження по корпусу
        if (!this.online) {
            for (int i = 0; i < count; i++) {
                Building slotBuilding = dates.get(i).getTime().getBuilding();
                if (slotBuilding != null && !Objects.equals(slotBuilding.getId(), (this.building != null ? this.building.getId() : null))) {
                    this.restriction[i] -= 100000;
                }
            }
        }

        int totalPairCount = initialPairCount * multiplier;
        colors = new int[totalPairCount];
        maxes = new int[totalPairCount];

        Progress.DONE.value += totalPairCount;

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
        appointment.setOnline(online);
        appointment.setOnlineLink(onlineLink);
        appointment.setEarmarkName(earmarkName);
        appointment.setLessonTypeNames(lessonTypeNames);

        Map<Date, List<Auditorium>> auditoriumMap = appointment.getAuditoriumMap();
        AuditoriumRepository repository = repositoryFactory.getAuditoriumRepository(Part.BOTH);

        for (int color : colors) {
            Date date = dates.get(colorMap.getDate(color));
            List<Auditorium> auditoriums;
            if (online) {
                auditoriums = List.of(); 
            } else if (fixedAuditorium != null) {
                auditoriums = List.of(fixedAuditorium);
            } else {
                auditoriums = repository.getAuditoriums(color, earmark, size);
            }
            auditoriumMap.put(date, auditoriums);
        }
    }

    public Appointment getAppointment(List<Date> dates, ColorMap colorMap, AuditoriumRepositoryFactory repositoryFactory) {
        Appointment appointment = new Appointment();
        initAppointment(appointment, dates, colorMap, repositoryFactory);
        return appointment;
    }

    public String getSubjectName() {
        return subject != null ? subject.getName() : "Без назви";
    }

    public List<Group> getGroups() {
        return groups;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }
}
