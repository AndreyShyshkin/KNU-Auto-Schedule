package ua.kiev.univ.schedule.service.core;

import ua.kiev.univ.schedule.model.appointment.Appointment;
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
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.model.subject.Subject;

import java.sql.*;
import java.util.*;

public class DatabaseService {

    private static final String URL = "jdbc:postgresql://localhost:5432/schedule_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS time_slot (id BIGSERIAL PRIMARY KEY, start_time VARCHAR(255), end_time VARCHAR(255), enable BOOLEAN)");
            stmt.execute("CREATE TABLE IF NOT EXISTS day (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), enable BOOLEAN)");
            stmt.execute("CREATE TABLE IF NOT EXISTS day_time_slot (day_id BIGINT REFERENCES day(id), time_slot_id BIGINT REFERENCES time_slot(id), PRIMARY KEY (day_id, time_slot_id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS faculty (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), description TEXT, enable BOOLEAN)");
            stmt.execute("CREATE TABLE IF NOT EXISTS earmark (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), size INT, enable BOOLEAN)");
            stmt.execute("CREATE TABLE IF NOT EXISTS subject (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), enable BOOLEAN)");
            stmt.execute("CREATE TABLE IF NOT EXISTS chair (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), description TEXT, enable BOOLEAN, faculty_id BIGINT REFERENCES faculty(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS speciality (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), description TEXT, enable BOOLEAN, faculty_id BIGINT REFERENCES faculty(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS auditorium (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), earmark_id BIGINT REFERENCES earmark(id), enable BOOLEAN)");
            stmt.execute("CREATE TABLE IF NOT EXISTS restriction (id BIGSERIAL PRIMARY KEY)");
            stmt.execute("CREATE TABLE IF NOT EXISTS restriction_entry (restriction_id BIGINT REFERENCES restriction(id), day_id BIGINT REFERENCES day(id), time_slot_id BIGINT REFERENCES time_slot(id), grade INT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS teacher (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), enable BOOLEAN, department_id BIGINT REFERENCES chair(id), restriction_id BIGINT REFERENCES restriction(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS student_group (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), enable BOOLEAN, department_id BIGINT REFERENCES speciality(id), year_val INT, size INT, restriction_id BIGINT REFERENCES restriction(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS lesson (id BIGSERIAL PRIMARY KEY, enable BOOLEAN, subject_id BIGINT REFERENCES subject(id), earmark_id BIGINT REFERENCES earmark(id), count INT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS lesson_teacher (lesson_id BIGINT REFERENCES lesson(id), teacher_id BIGINT REFERENCES teacher(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS lesson_group (lesson_id BIGINT REFERENCES lesson(id), group_id BIGINT REFERENCES student_group(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS appointment (id BIGSERIAL PRIMARY KEY, enable BOOLEAN, subject_id BIGINT REFERENCES subject(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS appointment_teacher (appointment_id BIGINT REFERENCES appointment(id), teacher_id BIGINT REFERENCES teacher(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS appointment_group (appointment_id BIGINT REFERENCES appointment(id), group_id BIGINT REFERENCES student_group(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS appointment_entry (appointment_id BIGINT REFERENCES appointment(id), day_id BIGINT REFERENCES day(id), time_slot_id BIGINT REFERENCES time_slot(id), auditorium_id BIGINT REFERENCES auditorium(id))");

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadAll() {
        DataService.clear();
        try (Connection conn = getConnection()) {
            Map<Long, Time> timeMap = loadTimes(conn);
            Map<Long, Day> dayMap = loadDays(conn, timeMap);
            Map<Long, Faculty> facultyMap = loadFaculties(conn);
            Map<Long, Earmark> earmarkMap = loadEarmarks(conn);
            Map<Long, Subject> subjectMap = loadSubjects(conn);
            Map<Long, Chair> chairMap = loadChairs(conn, facultyMap);
            Map<Long, Speciality> specialityMap = loadSpecialities(conn, facultyMap);
            Map<Long, Auditorium> auditoriumMap = loadAuditoriums(conn, earmarkMap);
            Map<Long, Teacher> teacherMap = loadTeachers(conn, chairMap, dayMap, timeMap);
            Map<Long, Group> groupMap = loadGroups(conn, specialityMap, dayMap, timeMap);
            loadLessons(conn, subjectMap, earmarkMap, teacherMap, groupMap);
            loadAppointments(conn, subjectMap, teacherMap, groupMap, dayMap, timeMap, auditoriumMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        try (Connection conn = getConnection()) {
            // Disable foreign key checks or cascade delete
            // Since Postgres doesn't have "SET FOREIGN_KEY_CHECKS", we rely on CASCADE in TRUNCATE
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("TRUNCATE TABLE appointment_entry, appointment_group, appointment_teacher, appointment, " +
                        "lesson_group, lesson_teacher, lesson, student_group, teacher, restriction_entry, restriction, " +
                        "auditorium, speciality, chair, subject, earmark, faculty, day_time_slot, day, time_slot RESTART IDENTITY CASCADE");
            }

            saveTimes(conn);
            saveDays(conn);
            saveFaculties(conn);
            saveEarmarks(conn);
            saveSubjects(conn);
            saveChairs(conn);
            saveSpecialities(conn);
            saveAuditoriums(conn);
            saveTeachers(conn);
            saveGroups(conn);
            saveLessons(conn);
            saveAppointments(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Load Methods ---

    private static Map<Long, Time> loadTimes(Connection conn) throws SQLException {
        Map<Long, Time> map = new HashMap<>();
        EntityList<Time> list = DataService.getEntities(Time.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM time_slot")) {
            while (rs.next()) {
                Time t = list.add();
                t.setId(rs.getLong("id"));
                t.setStart(rs.getString("start_time"));
                t.setEnd(rs.getString("end_time"));
                t.setEnable(rs.getBoolean("enable"));
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
                d.setEnable(rs.getBoolean("enable"));
                map.put(d.getId(), d);
            }
        }
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM day_time_slot")) {
            while (rs.next()) {
                Day d = map.get(rs.getLong("day_id"));
                Time t = timeMap.get(rs.getLong("time_slot_id"));
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

    private static Map<Long, Earmark> loadEarmarks(Connection conn) throws SQLException {
        Map<Long, Earmark> map = new HashMap<>();
        EntityList<Earmark> list = DataService.getEntities(Earmark.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM earmark")) {
            while (rs.next()) {
                Earmark e = list.add();
                e.setId(rs.getLong("id"));
                e.setName(rs.getString("name"));
                e.setSize(rs.getInt("size"));
                e.setEnable(rs.getBoolean("enable"));
                map.put(e.getId(), e);
            }
        }
        return map;
    }

    private static Map<Long, Subject> loadSubjects(Connection conn) throws SQLException {
        Map<Long, Subject> map = new HashMap<>();
        EntityList<Subject> list = DataService.getEntities(Subject.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM subject")) {
            while (rs.next()) {
                Subject s = list.add();
                s.setId(rs.getLong("id"));
                s.setName(rs.getString("name"));
                s.setEnable(rs.getBoolean("enable"));
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

    private static Map<Long, Auditorium> loadAuditoriums(Connection conn, Map<Long, Earmark> earmarkMap) throws SQLException {
        Map<Long, Auditorium> map = new HashMap<>();
        EntityList<Auditorium> list = DataService.getEntities(Auditorium.class);
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM auditorium")) {
            while (rs.next()) {
                Auditorium a = list.add();
                a.setId(rs.getLong("id"));
                a.setName(rs.getString("name"));
                a.setEarmark(earmarkMap.get(rs.getLong("earmark_id")));
                a.setEnable(rs.getBoolean("enable"));
                map.put(a.getId(), a);
            }
        }
        return map;
    }

    private static Restriction loadRestriction(Connection conn, long restrictionId, Map<Long, Day> dayMap, Map<Long, Time> timeMap) throws SQLException {
        Restriction r = new Restriction();
        r.setId(restrictionId);
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM restriction_entry WHERE restriction_id = ?")) {
            stmt.setLong(1, restrictionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Day day = dayMap.get(rs.getLong("day_id"));
                    Time time = timeMap.get(rs.getLong("time_slot_id"));
                    if (day != null && time != null) {
                        r.setGrade(Grade.values()[rs.getInt("grade")], new Date(day, time));
                    }
                }
            }
        }
        return r;
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
                long restrictionId = rs.getLong("restriction_id");
                if (restrictionId > 0) {
                    t.setRestriction(loadRestriction(conn, restrictionId, dayMap, timeMap));
                }
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
                g.setYear(Year.values()[rs.getInt("year_val")]);
                g.setSize(rs.getInt("size"));
                long restrictionId = rs.getLong("restriction_id");
                if (restrictionId > 0) {
                    g.setRestriction(loadRestriction(conn, restrictionId, dayMap, timeMap));
                }
                map.put(g.getId(), g);
            }
        }
        return map;
    }

    private static void loadLessons(Connection conn, Map<Long, Subject> s, Map<Long, Earmark> e, Map<Long, Teacher> tMap, Map<Long, Group> gMap) throws SQLException {
        EntityList<Lesson> list = DataService.getEntities(Lesson.class);
        Map<Long, Lesson> lessonMap = new HashMap<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM lesson")) {
            while (rs.next()) {
                Lesson l = list.add();
                l.setId(rs.getLong("id"));
                l.setEnable(rs.getBoolean("enable"));
                l.setSubject(s.get(rs.getLong("subject_id")));
                l.setEarmark(e.get(rs.getLong("earmark_id")));
                l.setCount(rs.getInt("count"));
                lessonMap.put(l.getId(), l);
            }
        }
        // Load teachers and groups for lessons
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM lesson_teacher")) {
            while (rs.next()) {
                Lesson l = lessonMap.get(rs.getLong("lesson_id"));
                Teacher t = tMap.get(rs.getLong("teacher_id"));
                if (l != null && t != null) l.getTeachers().add(t);
            }
        }
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM lesson_group")) {
            while (rs.next()) {
                Lesson l = lessonMap.get(rs.getLong("lesson_id"));
                Group g = gMap.get(rs.getLong("group_id"));
                if (l != null && g != null) l.getGroups().add(g);
            }
        }
    }

    private static void loadAppointments(Connection conn, Map<Long, Subject> s, Map<Long, Teacher> tMap, Map<Long, Group> gMap, Map<Long, Day> dMap, Map<Long, Time> tmMap, Map<Long, Auditorium> aMap) throws SQLException {
        EntityList<Appointment> list = DataService.getEntities(Appointment.class);
        Map<Long, Appointment> apptMap = new HashMap<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM appointment")) {
            while (rs.next()) {
                Appointment a = list.add();
                a.setId(rs.getLong("id"));
                a.setEnable(rs.getBoolean("enable"));
                a.setSubject(s.get(rs.getLong("subject_id")));
                apptMap.put(a.getId(), a);
            }
        }
        // Load teachers and groups
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM appointment_teacher")) {
            while (rs.next()) {
                Appointment a = apptMap.get(rs.getLong("appointment_id"));
                Teacher t = tMap.get(rs.getLong("teacher_id"));
                if (a != null && t != null) a.getTeachers().add(t);
            }
        }
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM appointment_group")) {
            while (rs.next()) {
                Appointment a = apptMap.get(rs.getLong("appointment_id"));
                Group g = gMap.get(rs.getLong("group_id"));
                if (a != null && g != null) a.getGroups().add(g);
            }
        }
        // Load auditorium map
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM appointment_entry")) {
            while (rs.next()) {
                Appointment a = apptMap.get(rs.getLong("appointment_id"));
                Day day = dMap.get(rs.getLong("day_id"));
                Time time = tmMap.get(rs.getLong("time_slot_id"));
                Auditorium aud = aMap.get(rs.getLong("auditorium_id"));
                
                if (a != null && day != null && time != null && aud != null) {
                    Date date = new Date(day, time);
                    List<Auditorium> audList = a.getAuditoriumMap().computeIfAbsent(date, k -> new ArrayList<>());
                    audList.add(aud);
                }
            }
        }
    }

    // --- Save Methods ---

    private static void saveTimes(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO time_slot (start_time, end_time, enable) VALUES (?, ?, ?) RETURNING id")) {
            for (Time t : DataService.getEntities(Time.class)) {
                stmt.setString(1, t.getStart());
                stmt.setString(2, t.getEnd());
                stmt.setBoolean(3, t.isActive());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) t.setId(rs.getLong(1));
            }
        }
    }

    private static void saveDays(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO day (name, enable) VALUES (?, ?) RETURNING id")) {
            try (PreparedStatement linkStmt = conn.prepareStatement("INSERT INTO day_time_slot (day_id, time_slot_id) VALUES (?, ?)")) {
                for (Day d : DataService.getEntities(Day.class)) {
                    stmt.setString(1, d.getName());
                    stmt.setBoolean(2, d.isActive());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) d.setId(rs.getLong(1));
                    
                    for (Time t : d.getTimes()) {
                        linkStmt.setLong(1, d.getId());
                        linkStmt.setLong(2, t.getId());
                        linkStmt.addBatch();
                    }
                }
                linkStmt.executeBatch();
            }
        }
    }

    private static void saveFaculties(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO faculty (name, description, enable) VALUES (?, ?, ?) RETURNING id")) {
            for (Faculty f : DataService.getEntities(Faculty.class)) {
                stmt.setString(1, f.getName());
                stmt.setString(2, f.getDescription());
                stmt.setBoolean(3, f.isActive());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) f.setId(rs.getLong(1));
            }
        }
    }

    private static void saveEarmarks(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO earmark (name, size, enable) VALUES (?, ?, ?) RETURNING id")) {
            for (Earmark e : DataService.getEntities(Earmark.class)) {
                stmt.setString(1, e.getName());
                stmt.setInt(2, e.getSize());
                stmt.setBoolean(3, e.isActive());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) e.setId(rs.getLong(1));
            }
        }
    }

    private static void saveSubjects(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO subject (name, enable) VALUES (?, ?) RETURNING id")) {
            for (Subject s : DataService.getEntities(Subject.class)) {
                stmt.setString(1, s.getName());
                stmt.setBoolean(2, s.isActive());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) s.setId(rs.getLong(1));
            }
        }
    }

    private static void saveChairs(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO chair (name, description, enable, faculty_id) VALUES (?, ?, ?, ?) RETURNING id")) {
            for (Chair c : DataService.getEntities(Chair.class)) {
                stmt.setString(1, c.getName());
                stmt.setString(2, c.getDescription());
                stmt.setBoolean(3, c.isActive());
                stmt.setLong(4, c.getFaculty() != null ? c.getFaculty().getId() : 0);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) c.setId(rs.getLong(1));
            }
        }
    }

    private static void saveSpecialities(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO speciality (name, description, enable, faculty_id) VALUES (?, ?, ?, ?) RETURNING id")) {
            for (Speciality s : DataService.getEntities(Speciality.class)) {
                stmt.setString(1, s.getName());
                stmt.setString(2, s.getDescription());
                stmt.setBoolean(3, s.isActive());
                stmt.setLong(4, s.getFaculty() != null ? s.getFaculty().getId() : 0);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) s.setId(rs.getLong(1));
            }
        }
    }

    private static void saveAuditoriums(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO auditorium (name, earmark_id, enable) VALUES (?, ?, ?) RETURNING id")) {
            for (Auditorium a : DataService.getEntities(Auditorium.class)) {
                stmt.setString(1, a.getName());
                stmt.setLong(2, a.getEarmark() != null ? a.getEarmark().getId() : 0);
                stmt.setBoolean(3, a.isActive());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) a.setId(rs.getLong(1));
            }
        }
    }

    private static long saveRestriction(Connection conn, Restriction r) throws SQLException {
        if (r == null) return 0;
        long id = 0;
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO restriction DEFAULT VALUES RETURNING id")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) id = rs.getLong(1);
        }
        r.setId(id);
        
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO restriction_entry (restriction_id, day_id, time_slot_id, grade) VALUES (?, ?, ?, ?)")) {
            for (Map.Entry<Date, Grade> entry : r.getGradeMap().entrySet()) {
                Date date = entry.getKey();
                Grade grade = entry.getValue();
                
                stmt.setLong(1, id);
                stmt.setLong(2, date.getDay().getId());
                stmt.setLong(3, date.getTime().getId());
                stmt.setInt(4, grade.ordinal());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
        return id;
    }

    private static void saveTeachers(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO teacher (name, enable, department_id, restriction_id) VALUES (?, ?, ?, ?) RETURNING id")) {
            for (Teacher t : DataService.getEntities(Teacher.class)) {
                // Save restriction first
                // TODO: Need access to Restriction keys. Assuming it is handled.
                long resId = saveRestriction(conn, t.getRestriction());
                
                stmt.setString(1, t.getName());
                stmt.setBoolean(2, t.isActive());
                stmt.setLong(3, t.getDepartment() != null ? t.getDepartment().getId() : 0);
                stmt.setLong(4, resId);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) t.setId(rs.getLong(1));
            }
        }
    }

    private static void saveGroups(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO student_group (name, enable, department_id, year_val, size, restriction_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING id")) {
            for (Group g : DataService.getEntities(Group.class)) {
                long resId = saveRestriction(conn, g.getRestriction());
                
                stmt.setString(1, g.getName());
                stmt.setBoolean(2, g.isActive());
                stmt.setLong(3, g.getDepartment() != null ? g.getDepartment().getId() : 0);
                stmt.setInt(4, g.getYear().ordinal());
                stmt.setInt(5, g.getSize());
                stmt.setLong(6, resId);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) g.setId(rs.getLong(1));
            }
        }
    }

    private static void saveLessons(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO lesson (enable, subject_id, earmark_id, count) VALUES (?, ?, ?, ?) RETURNING id")) {
            try (PreparedStatement tStmt = conn.prepareStatement("INSERT INTO lesson_teacher (lesson_id, teacher_id) VALUES (?, ?)");
                 PreparedStatement gStmt = conn.prepareStatement("INSERT INTO lesson_group (lesson_id, group_id) VALUES (?, ?)")) {
                
                for (Lesson l : DataService.getEntities(Lesson.class)) {
                    stmt.setBoolean(1, l.isActive());
                    stmt.setLong(2, l.getSubject() != null ? l.getSubject().getId() : 0);
                    stmt.setLong(3, l.getEarmark() != null ? l.getEarmark().getId() : 0);
                    stmt.setInt(4, l.getCount());
                    
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) l.setId(rs.getLong(1));
                    
                    for (Teacher t : l.getTeachers()) {
                        tStmt.setLong(1, l.getId());
                        tStmt.setLong(2, t.getId());
                        tStmt.addBatch();
                    }
                    
                    for (Group g : l.getGroups()) {
                        gStmt.setLong(1, l.getId());
                        gStmt.setLong(2, g.getId());
                        gStmt.addBatch();
                    }
                }
                tStmt.executeBatch();
                gStmt.executeBatch();
            }
        }
    }

    private static void saveAppointments(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO appointment (enable, subject_id) VALUES (?, ?) RETURNING id")) {
            try (PreparedStatement tStmt = conn.prepareStatement("INSERT INTO appointment_teacher (appointment_id, teacher_id) VALUES (?, ?)");
                 PreparedStatement gStmt = conn.prepareStatement("INSERT INTO appointment_group (appointment_id, group_id) VALUES (?, ?)");
                 PreparedStatement eStmt = conn.prepareStatement("INSERT INTO appointment_entry (appointment_id, day_id, time_slot_id, auditorium_id) VALUES (?, ?, ?, ?)")) {
                
                for (Appointment a : DataService.getEntities(Appointment.class)) {
                    stmt.setBoolean(1, a.isActive());
                    stmt.setLong(2, a.getSubject() != null ? a.getSubject().getId() : 0);
                    
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) a.setId(rs.getLong(1));
                    
                    for (Teacher t : a.getTeachers()) {
                        tStmt.setLong(1, a.getId());
                        tStmt.setLong(2, t.getId());
                        tStmt.addBatch();
                    }
                    
                    for (Group g : a.getGroups()) {
                        gStmt.setLong(1, a.getId());
                        gStmt.setLong(2, g.getId());
                        gStmt.addBatch();
                    }
                    
                    for (Map.Entry<Date, List<Auditorium>> entry : a.getAuditoriumMap().entrySet()) {
                        Date d = entry.getKey();
                        for (Auditorium aud : entry.getValue()) {
                            eStmt.setLong(1, a.getId());
                            eStmt.setLong(2, d.getDay().getId());
                            eStmt.setLong(3, d.getTime().getId());
                            eStmt.setLong(4, aud.getId());
                            eStmt.addBatch();
                        }
                    }
                }
                tStmt.executeBatch();
                gStmt.executeBatch();
                eStmt.executeBatch();
            }
        }
    }
}