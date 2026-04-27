package ua.kiev.univ.schedule.scheduler.point;

import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.AppointmentEntry;
import ua.kiev.univ.schedule.model.appointment.Part;
import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Grade;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.member.Year;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.scheduler.ColorMap;
import ua.kiev.univ.schedule.scheduler.Progress;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepository;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepositoryFactory;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.BuildingEarmark;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
    public int both;
    
    // Поля, які необхідні для роботи Executor (ад'юцентність чисельника/знаменника)
    public int[] first;
    public int[] second;
    public int part;
    
    // Нові поля для збереження результатів розрахунку
    protected int initialPairCount;
    protected int multiplier;
    protected int totalStudents;

    public static Point getPoint(Lesson lesson, List<Date> dates, List<BuildingEarmark> types, RestrictionMap restrictionMap) {
        return new Point(lesson, dates, types, restrictionMap);
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

        this.building = lesson.getBuilding() != null ? lesson.getBuilding() : (fixedAuditorium != null ? fixedAuditorium.getBuilding() : null);
        
        this.groups = new LinkedList<>(lesson.getGroups());
        this.teachers = new LinkedList<>(lesson.getTeachers());
        
        this.totalStudents = groups.stream().mapToInt(g -> g.getSize() != null ? g.getSize() : 0).sum();
        
        this.initialPairCount = (int) Math.ceil((double) (lesson.getTotalHours() != null ? lesson.getTotalHours() : 30) / 1.5);
        if (this.initialPairCount <= 0) this.initialPairCount = 1;
        
        this.multiplier = 1;
        int calculatedSize = 1;
        int index = -1;

        if (this.online) {
            this.earmark = -1;
            this.size = 0;
        } else {
            int teacherCount = this.teachers.size() > 0 ? this.teachers.size() : 1;
            
            for (int i = 0; i < types.size(); i++) {
                BuildingEarmark type = types.get(i);
                if (Objects.equals(type.getBuilding(), this.building) && Objects.equals(type.getEarmark(), lesson.getEarmark())) {
                    Integer audSize = type.getEarmark() != null ? type.getEarmark().getSize() : null;
                    if (audSize == null || audSize <= 0) audSize = 49; 
                    if (audSize >= totalStudents) {
                        index = i;
                        calculatedSize = 1;
                        this.multiplier = 1;
                        break;
                    } else if (lesson.isAllowMultipleAuditoriums()) {
                        int needed = (int) Math.ceil((double) totalStudents / audSize);
                        if (teacherCount >= needed) {
                            index = i;
                            calculatedSize = needed;
                            this.multiplier = 1;
                        } else {
                            index = i;
                            calculatedSize = 1;
                            this.multiplier = needed;
                        }
                        break;
                    }
                }
            }
            
            if (index == -1) {
                for (int i = 0; i < types.size(); i++) {
                    Earmark e = types.get(i).getEarmark();
                    if (e != null && Objects.equals(e, lesson.getEarmark())) {
                        Integer audSize = e.getSize();
                        if (audSize == null || audSize <= 0) audSize = 49;
                        if (audSize >= totalStudents) {
                            index = i;
                            calculatedSize = 1;
                            this.multiplier = 1;
                            break;
                        } else if (lesson.isAllowMultipleAuditoriums()) {
                            int needed = (int) Math.ceil((double) totalStudents / audSize);
                            if (teacherCount >= needed) {
                                index = i;
                                calculatedSize = needed;
                                this.multiplier = 1;
                            } else {
                                index = i;
                                calculatedSize = 1;
                                this.multiplier = needed;
                            }
                            break;
                        }
                    }
                }
            }
            this.earmark = index;
            this.size = calculatedSize;
        }
        
        this.restriction = new int[count];
        restrictionMap.addRestrictions(lesson.getGroups(), this);
        restrictionMap.addRestrictions(lesson.getTeachers(), this);

        for (int i = 0; i < count; i++) {
            Date date = dates.get(i);
            LocalDate localDate = date.getLocalDate();
            
            if (!this.online) {
                Building slotBuilding = date.getTime().getBuilding();
                if (slotBuilding != null && !Objects.equals(slotBuilding.getId(), (this.building != null ? this.building.getId() : null))) {
                    this.restriction[i] -= 100000;
                }
            }
            
            if (localDate != null) {
                if (lesson.getStartDate() != null && localDate.isBefore(lesson.getStartDate())) this.restriction[i] -= 1000000;
                if (lesson.getEndDate() != null && localDate.isAfter(lesson.getEndDate())) this.restriction[i] -= 1000000;
                
                // Періодичність тижнів
                if (lesson.getWeekFrequency() != null && lesson.getWeekFrequency() > 0) {
                    int weekNum = localDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                    boolean isOddWeek = (weekNum % 2 != 0);
                    
                    if (lesson.getWeekFrequency() == 1 && !isOddWeek) { // Тільки непарні
                        this.restriction[i] -= 1000000;
                    } else if (lesson.getWeekFrequency() == 2 && isOddWeek) { // Тільки парні
                        this.restriction[i] -= 1000000;
                    }
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
            if (list2.contains(bean)) return true;
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
        
        appointment.setSubjectName(subject != null ? subject.getName() : "Без назви");
        appointment.setTeacherNames(getTeacherNames());
        appointment.setGroupNames(getGroupNames());
        appointment.setTeacherIds(teachers.stream().map(t -> t.getId().toString()).collect(Collectors.joining(",")));
        appointment.setGroupIds(groups.stream().map(g -> g.getId().toString()).collect(Collectors.joining(",")));

        AuditoriumRepository repository = repositoryFactory.getAuditoriumRepository(Part.BOTH);

        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];
            Date date = dates.get(colorMap.getDate(color));
            String dayName = date.getDay().getName();
            String start = date.getTime().getStart();
            String end = date.getTime().getEnd();
            String bName = date.getTime().getBuilding() != null ? date.getTime().getBuilding().getName() : "";

            List<Auditorium> auds;
            if (online) auds = List.of();
            else if (fixedAuditorium != null) auds = List.of(fixedAuditorium);
            else auds = repository.getAuditoriums(color, earmark, size);

            int groupPartIndex = i / initialPairCount;
            int audCount = auds.size();

            if (audCount <= 1) {
                for (Auditorium aud : auds) {
                    String tNames = (teachers.size() >= multiplier) ? teachers.get(groupPartIndex).getName() : getTeacherNames();
                    int startG = (groupPartIndex * groups.size()) / multiplier;
                    int endG = ((groupPartIndex + 1) * groups.size()) / multiplier;
                    List<Group> subGroups = groups.subList(startG, Math.min(endG, groups.size()));
                    String gNames = subGroups.stream().map(Group::getName).collect(Collectors.joining(", "));
                    if (gNames.isEmpty()) gNames = getGroupNames();

                    AppointmentEntry entry = new AppointmentEntry(appointment, dayName, start, end, bName, aud.getName(), tNames, gNames);
                    entry.setActualDate(date.getLocalDate());
                    appointment.getEntries().add(entry);
                }
            } else {
                for (int j = 0; j < audCount; j++) {
                    Auditorium aud = auds.get(j);
                    String tNames = (teachers.size() >= audCount) ? teachers.get(j).getName() : getTeacherNames();
                    int startG = (j * groups.size()) / audCount;
                    int endG = ((j + 1) * groups.size()) / audCount;
                    List<Group> subGroups = groups.subList(startG, Math.min(endG, groups.size()));
                    String gNames = subGroups.stream().map(Group::getName).collect(Collectors.joining(", "));

                    AppointmentEntry entry = new AppointmentEntry(appointment, dayName, start, end, bName, aud.getName(), tNames, gNames);
                    entry.setActualDate(date.getLocalDate());
                    appointment.getEntries().add(entry);
                }
            }
        }
    }

    private String getTeacherNames() {
        return teachers.stream().map(Teacher::getName).collect(Collectors.joining(", "));
    }

    private String getGroupNames() {
        return groups.stream().map(Group::getName).collect(Collectors.joining(", "));
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
