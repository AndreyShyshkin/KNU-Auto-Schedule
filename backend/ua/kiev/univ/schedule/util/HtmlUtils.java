package ua.kiev.univ.schedule.util;

import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.HalvedAppointment;
import ua.kiev.univ.schedule.model.appointment.Part;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.service.core.DataService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Cell {
    private final Subject subject;
    private final List<? extends Member> members;
    private final List<Auditorium> auditoriums;

    public Cell(Subject subject, List<? extends Member> members, List<Auditorium> auditoriums) {
        this.subject = subject;
        this.members = members;
        this.auditoriums = auditoriums;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(subject);
        buffer.append("<hr>");
        for (Member restrictor : members) {
            buffer.append(restrictor);
            buffer.append("<br>");
        }
        buffer.append("<hr>");
        for (Auditorium auditorium : auditoriums) {
            buffer.append(auditorium);
            buffer.append("<br>");
        }
        return buffer.toString();
    }
}

public class HtmlUtils {

    private static String wrapToHtml(String body) {
        return "<html><body>" + body + "</body></html>";
    }

    private static <E extends Member> void setCells(Cell[] row, List<E> members, Subject subject, List<E> columns, List<? extends Member> list, List<Auditorium> auditoriums) {
        for (E column : columns) {
            int index = members.indexOf(column);
            if (index >= 0) {
                row[index] = new Cell(subject, list, auditoriums);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Member> void setCells(Cell[] row, Class<E> memberClass, List<E> members, Subject subject, List<Group> groups, List<Teacher> teachers, List<Auditorium> auditoriums) {
        // Тут використовується небезпечне приведення типів (Unchecked cast), яке було в оригіналі.
        // Ми залишаємо логіку як є, але приглушуємо попередження IDE.
        if (Group.class.equals(memberClass)) {
            setCells(row, members, subject, (List<E>) groups, teachers, auditoriums);
        }
        if (Teacher.class.equals(memberClass)) {
            setCells(row, members, subject, (List<E>) teachers, groups, auditoriums);
        }
    }

    public static <E extends Member> String generateResultsTable(Class<E> memberClass) {
        List<Date> dates = EntityFilter.getActiveDates();
        List<E> members = EntityFilter.getActiveEntities(DataService.getEntities(memberClass));

        Map<Part, Cell[][]> tableMap = new HashMap<>();
        tableMap.put(Part.BOTH, new Cell[dates.size()][members.size()]);
        tableMap.put(Part.FIRST, new Cell[dates.size()][members.size()]);
        tableMap.put(Part.SECOND, new Cell[dates.size()][members.size()]);

        List<Appointment> appointments = DataService.getEntities(Appointment.class);

        for (Appointment appointment : appointments) {
            Subject subject = appointment.getSubject();
            List<Group> groups = appointment.getGroups();
            List<Teacher> teachers = appointment.getTeachers();
            Map<Date, List<Auditorium>> map = appointment.getAuditoriumMap();

            for (Date date : map.keySet()) {
                int row = dates.indexOf(date);
                if (row >= 0) {
                    setCells(tableMap.get(Part.BOTH)[row], memberClass, members, subject, groups, teachers, map.get(date));
                }
            }

            if (appointment instanceof HalvedAppointment) {
                HalvedAppointment halved = (HalvedAppointment) appointment;
                int row = dates.indexOf(halved.getDate());
                if (row >= 0) {
                    Part part = halved.getPart();
                    List<Auditorium> auditoriums = halved.getAuditoriums();
                    setCells(tableMap.get(part)[row], memberClass, members, subject, groups, teachers, auditoriums);
                }
            }
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append("<table border='1'>");
        buffer.append("<tr>");
        buffer.append("<td width='100px'>");
        buffer.append("</td>");
        for (E member : members) {
            buffer.append("<td width='100px'>");
            buffer.append(member);
            buffer.append("</td>");
        }
        buffer.append("</tr>");

        int row = 0;
        for (Date date : dates) {
            buffer.append("<tr height='50px'>");
            buffer.append("<td>");
            buffer.append(date);
            buffer.append("</td>");
            for (int col = 0; col < members.size(); col++) {
                buffer.append("<td>");
                Cell cell = tableMap.get(Part.BOTH)[row][col];
                if (cell != null) {
                    buffer.append(cell);
                } else {
                    Cell first = tableMap.get(Part.FIRST)[row][col];
                    Cell second = tableMap.get(Part.SECOND)[row][col];
                    if ((first != null) || (second != null)) {
                        if (first != null) {
                            buffer.append(first);
                        }
                        buffer.append("<hr><hr>");
                        if (second != null) {
                            buffer.append(second);
                        }
                    }
                }
                buffer.append("</td>");
            }
            buffer.append("</tr>");
            row++;
        }
        buffer.append("</table>");
        return buffer.toString();
    }

    public static <E extends Member> String generateResultsHtml(Class<E> memberClass) {
        return wrapToHtml(generateResultsTable(memberClass));
    }

    public static String generateResultsHtml() {
        return wrapToHtml(generateResultsTable(Teacher.class) + "<br>" + generateResultsTable(Group.class));
    }
}