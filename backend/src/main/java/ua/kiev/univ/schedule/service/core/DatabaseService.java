package ua.kiev.univ.schedule.service.core;

import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.AppointmentEntry;
import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.*;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.model.subject.Subject;

import java.sql.*;
import java.util.*;

public class DatabaseService {

    private static String url = "jdbc:postgresql://schedule-db:5432/schedule_db";
    private static String user = "postgres";
    private static String password = "password";

    public static void setCredentials(String dbUrl, String dbUser, String dbPassword) {
        if (dbUrl != null) url = dbUrl;
        if (dbUser != null) user = dbUser;
        if (dbPassword != null) password = dbPassword;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static void loadAll() {
        DataService.clear();
        try (Connection conn = getConnection()) {
            Map<Long, Building> buildingMap = loadBuildings(conn);
            Map<Long, Time> timeMap = loadTimes(conn, buildingMap);
            Map<Long, Day> dayMap = loadDays(conn, timeMap);
            Map<Long, Faculty> facultyMap = loadFaculties(conn);
            Map<Long, Earmark> earmarkMap = loadEarmarks(conn, buildingMap);
            Map<Long, Subject> subjectMap = loadSubjects(conn, facultyMap);
            Map<Long, Chair> chairMap = loadChairs(conn, facultyMap);
            Map<Long, Speciality> specialityMap = loadSpecialities(conn, facultyMap);
            Map<Long, Auditorium> auditoriumMap = loadAuditoriums(conn, earmarkMap, buildingMap);
            Map<Long, Teacher> teacherMap = loadTeachers(conn, chairMap, dayMap, timeMap);
            Map<Long, Group> groupMap = loadGroups(conn, specialityMap, dayMap, timeMap);
            loadLessons(conn, subjectMap, earmarkMap, buildingMap, teacherMap, groupMap);
            loadAppointments(conn, subjectMap, teacherMap, groupMap, dayMap, timeMap, auditoriumMap);
        } catch (SQLException e) {
            System.err.println("Database loadAll failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Map<Long, Building> loadBuildings(Connection conn) throws SQLException {
        Map<Long, Building> map = new HashMap<>();
        EntityList<Building> list = DataService.getEntities(Building.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM building")) {
            while (rs.next()) {
                Building b = list.add();
                b.setId(rs.getLong("id"));
                b.setName(rs.getString("name"));
                b.setDescription(rs.getString("description"));
                b.setEnable(rs.getBoolean("enable"));
                map.put(b.getId(), b);
            }
        }
        return map;
    }

    private static Map<Long, Time> loadTimes(Connection conn, Map<Long, Building> buildingMap) throws SQLException {
        Map<Long, Time> map = new HashMap<>();
        EntityList<Time> list = DataService.getEntities(Time.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM time_slot")) {
            while (rs.next()) {
                Time t = list.add();
                t.setId(rs.getLong("id"));
                t.setStart(rs.getString("start_time"));
                t.setEnd(rs.getString("end_time"));
                t.setEnable(rs.getBoolean("enable"));
                t.setBuilding(buildingMap.get(rs.getLong("building_id")));
                map.put(t.getId(), t);
            }
        }
        return map;
    }

    private static Map<Long, Day> loadDays(Connection conn, Map<Long, Time> timeMap) throws SQLException {
        Map<Long, Day> map = new HashMap<>();
        EntityList<Day> list = DataService.getEntities(Day.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM day")) {
            while (rs.next()) {
                Day d = list.add();
                d.setId(rs.getLong("id"));
                d.setName(rs.getString("name"));
                d.setDayOfWeek(rs.getInt("day_of_week"));
                d.setEnable(rs.getBoolean("enable"));
                map.put(d.getId(), d);
            }
        }
        // JPA table name is day_times
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM day_times")) {
            while (rs.next()) {
                Day d = map.get(rs.getLong("day_id"));
                Time t = timeMap.get(rs.getLong("time_id"));
                if (d != null && t != null) d.getTimes().add(t);
            }
        }
        return map;
    }

    private static Map<Long, Faculty> loadFaculties(Connection conn) throws SQLException {
        Map<Long, Faculty> map = new HashMap<>();
        EntityList<Faculty> list = DataService.getEntities(Faculty.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM faculty")) {
            while (rs.next()) {
                Faculty f = list.add();
                f.setId(rs.getLong("id"));
                f.setName(rs.getString("name"));
                f.setDescription(rs.getString("description"));
                f.setEnable(rs.getBoolean("enable"));
                map.put(f.getId(), f);
            }
        }
        return map;
    }

    private static Map<Long, Earmark> loadEarmarks(Connection conn, Map<Long, Building> buildingMap) throws SQLException {
        Map<Long, Earmark> map = new HashMap<>();
        EntityList<Earmark> list = DataService.getEntities(Earmark.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM earmark")) {
            while (rs.next()) {
                Earmark e = list.add();
                e.setId(rs.getLong("id"));
                e.setName(rs.getString("name"));
                e.setSize(rs.getInt("size"));
                e.setEnable(rs.getBoolean("enable"));
                e.setBuilding(buildingMap.get(rs.getLong("building_id")));
                map.put(e.getId(), e);
            }
        }
        return map;
    }

    private static Map<Long, Subject> loadSubjects(Connection conn, Map<Long, Faculty> facultyMap) throws SQLException {
        Map<Long, Subject> map = new HashMap<>();
        EntityList<Subject> list = DataService.getEntities(Subject.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM subject")) {
            while (rs.next()) {
                Subject s = list.add();
                s.setId(rs.getLong("id"));
                s.setName(rs.getString("name"));
                s.setEnable(rs.getBoolean("enable"));
                s.setFaculty(facultyMap.get(rs.getLong("faculty_id")));
                map.put(s.getId(), s);
            }
        }
        return map;
    }

    private static Map<Long, Chair> loadChairs(Connection conn, Map<Long, Faculty> facultyMap) throws SQLException {
        Map<Long, Chair> map = new HashMap<>();
        EntityList<Chair> list = DataService.getEntities(Chair.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM chair")) {
            while (rs.next()) {
                Chair c = list.add();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setDescription(rs.getString("description"));
                c.setEnable(rs.getBoolean("enable"));
                c.setFaculty(facultyMap.get(rs.getLong("faculty_id")));
                map.put(c.getId(), c);
            }
        }
        return map;
    }

    private static Map<Long, Speciality> loadSpecialities(Connection conn, Map<Long, Faculty> facultyMap) throws SQLException {
        Map<Long, Speciality> map = new HashMap<>();
        EntityList<Speciality> list = DataService.getEntities(Speciality.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM speciality")) {
            while (rs.next()) {
                Speciality s = list.add();
                s.setId(rs.getLong("id"));
                s.setName(rs.getString("name"));
                s.setDescription(rs.getString("description"));
                s.setEnable(rs.getBoolean("enable"));
                s.setFaculty(facultyMap.get(rs.getLong("faculty_id")));
                map.put(s.getId(), s);
            }
        }
        return map;
    }

    private static Map<Long, Auditorium> loadAuditoriums(Connection conn, Map<Long, Earmark> earmarkMap, Map<Long, Building> buildingMap) throws SQLException {
        Map<Long, Auditorium> map = new HashMap<>();
        EntityList<Auditorium> list = DataService.getEntities(Auditorium.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM auditorium")) {
            while (rs.next()) {
                Auditorium a = list.add();
                a.setId(rs.getLong("id"));
                a.setName(rs.getString("name"));
                a.setEnable(rs.getBoolean("enable"));
                a.setEarmark(earmarkMap.get(rs.getLong("earmark_id")));
                a.setBuilding(buildingMap.get(rs.getLong("building_id")));
                map.put(a.getId(), a);
            }
        }
        return map;
    }

    private static void loadRestriction(Connection conn, Restrictor restrictor, Map<Long, Day> dayMap, Map<Long, Time> timeMap) throws SQLException {
        Restriction r = restrictor.getRestriction();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM restriction_entry WHERE restriction_id = ?")) {
            stmt.setLong(1, r.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Day d = dayMap.get(rs.getLong("day_id"));
                Time t = timeMap.get(rs.getLong("time_slot_id"));
                Grade g = Grade.values()[rs.getInt("grade")];
                if (d != null && t != null) {
                    r.getGradeMap().put(new Date(d, t), g);
                }
            }
        }
    }

    private static Map<Long, Teacher> loadTeachers(Connection conn, Map<Long, Chair> chairMap, Map<Long, Day> dayMap, Map<Long, Time> timeMap) throws SQLException {
        Map<Long, Teacher> map = new HashMap<>();
        EntityList<Teacher> list = DataService.getEntities(Teacher.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM teacher")) {
            while (rs.next()) {
                Teacher t = list.add();
                t.setId(rs.getLong("id"));
                t.setName(rs.getString("name"));
                t.setEnable(rs.getBoolean("enable"));
                t.setDepartment(chairMap.get(rs.getLong("department_id")));
                t.getRestriction().setId(rs.getLong("restriction_id"));
                loadRestriction(conn, t, dayMap, timeMap);
                map.put(t.getId(), t);
            }
        }
        return map;
    }

    private static Map<Long, Group> loadGroups(Connection conn, Map<Long, Speciality> specialityMap, Map<Long, Day> dayMap, Map<Long, Time> timeMap) throws SQLException {
        Map<Long, Group> map = new HashMap<>();
        EntityList<Group> list = DataService.getEntities(Group.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM student_group")) {
            while (rs.next()) {
                Group g = list.add();
                g.setId(rs.getLong("id"));
                g.setName(rs.getString("name"));
                g.setEnable(rs.getBoolean("enable"));
                g.setDepartment(specialityMap.get(rs.getLong("department_id")));
                int y = rs.getInt("year");
                if (y >= 0 && y < Year.values().length) g.setYear(Year.values()[y]);
                g.setSize(rs.getInt("size"));
                g.getRestriction().setId(rs.getLong("restriction_id"));
                loadRestriction(conn, g, dayMap, timeMap);
                map.put(g.getId(), g);
            }
        }
        return map;
    }

    private static void loadLessons(Connection conn, Map<Long, Subject> subjectMap, Map<Long, Earmark> earmarkMap, Map<Long, Building> buildingMap, Map<Long, Teacher> teacherMap, Map<Long, Group> groupMap) throws SQLException {
        EntityList<Lesson> list = DataService.getEntities(Lesson.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM lesson")) {
            while (rs.next()) {
                Lesson l = list.add();
                l.setId(rs.getLong("id"));
                l.setEnable(rs.getBoolean("enable"));
                l.setSubject(subjectMap.get(rs.getLong("subject_id")));
                l.setEarmark(earmarkMap.get(rs.getLong("earmark_id")));
                l.setBuilding(buildingMap.get(rs.getLong("building_id")));
                l.setOnline(rs.getBoolean("online"));
                l.setOnlineLink(rs.getString("online_link"));
                l.setTotalHours(rs.getInt("total_hours"));
                java.sql.Date sd = rs.getDate("start_date");
                if (sd != null) l.setStartDate(sd.toLocalDate());
                java.sql.Date ed = rs.getDate("end_date");
                if (ed != null) l.setEndDate(ed.toLocalDate());
                l.setWeekFrequency(rs.getInt("week_frequency"));
                l.setAllowMultipleAuditoriums(rs.getBoolean("allow_multiple_auditoriums"));


                // Correct table names lesson_teachers and lesson_groups
                try (PreparedStatement ts = conn.prepareStatement("SELECT teacher_id FROM lesson_teachers WHERE lesson_id = ?")) {
                    ts.setLong(1, l.getId());
                    ResultSet trs = ts.executeQuery();
                    while (trs.next()) {
                        Teacher t = teacherMap.get(trs.getLong(1));
                        if (t != null) l.getTeachers().add(t);
                    }
                }
                try (PreparedStatement gs = conn.prepareStatement("SELECT group_id FROM lesson_groups WHERE lesson_id = ?")) {
                    gs.setLong(1, l.getId());
                    ResultSet grs = gs.executeQuery();
                    while (grs.next()) {
                        Group g = groupMap.get(grs.getLong(1));
                        if (g != null) l.getGroups().add(g);
                    }
                }
            }
        }
    }

    private static void loadAppointments(Connection conn, Map<Long, Subject> subjectMap, Map<Long, Teacher> teacherMap, Map<Long, Group> groupMap, Map<Long, Day> dayMap, Map<Long, Time> timeMap, Map<Long, Auditorium> auditoriumMap) throws SQLException {
        EntityList<Appointment> list = DataService.getEntities(Appointment.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM appointment")) {
            while (rs.next()) {
                Appointment a = list.add();
                a.setId(rs.getLong("id"));
                a.setSubject(subjectMap.get(rs.getLong("subject_id")));
                a.setOnline(rs.getBoolean("online"));
                a.setOnlineLink(rs.getString("online_link"));
                a.setEarmarkName(rs.getString("earmark_name"));
                a.setLessonTypeNames(rs.getString("lesson_type_names"));

                // We don't load teachers/groups for appointments since they aren't saved in join tables by JPA yet.
                // But we don't need them for the BUILD step anyway.
                
                try (PreparedStatement es = conn.prepareStatement("SELECT * FROM appointment_entry WHERE appointment_id = ?")) {
                    es.setLong(1, a.getId());
                    ResultSet ers = es.executeQuery();
                    while (ers.next()) {
                        Day d = dayMap.get(ers.getLong("day_id"));
                        Time t = timeMap.get(ers.getLong("time_slot_id"));
                        Auditorium aud = auditoriumMap.get(ers.getLong("auditorium_id"));
                        if (d != null && t != null) {
                            Date date = new Date(d, t);
                            java.sql.Date ad = ers.getDate("actual_date");
                            if (ad != null) date.setLocalDate(ad.toLocalDate());
                            
                            List<Auditorium> audList = a.getAuditoriumMap().computeIfAbsent(date, k -> new ArrayList<>());
                            if (aud != null) audList.add(aud);
                        }
                    }
                }
            }
        }
    }

    public static void saveAll() {
        // Not used during BUILD
    }
}
