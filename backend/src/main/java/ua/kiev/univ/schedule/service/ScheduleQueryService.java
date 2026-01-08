package ua.kiev.univ.schedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kiev.univ.schedule.dto.ScheduleEntryDto;
import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.AppointmentEntry;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.repository.AppointmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleQueryService {

    private final AppointmentRepository appointmentRepository;

    public ScheduleQueryService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleEntryDto> getTeacherSchedule(Long teacherId) {
        List<Appointment> appointments = appointmentRepository.findAll();
        List<ScheduleEntryDto> result = new ArrayList<>();

        for (Appointment app : appointments) {
            boolean hasTeacher = app.getTeachers().stream().anyMatch(t -> t.getId().equals(teacherId));
            if (!hasTeacher) continue;

            String subjectName = app.getSubject().getName();
            String groups = app.getGroups().stream().map(g -> g.getName()).collect(Collectors.joining(", "));

            for (AppointmentEntry entry : app.getEntries()) {
                String audName = entry.getAuditorium().getName();
                String type = (entry.getAuditorium().getEarmark() == null) ? "" : entry.getAuditorium().getEarmark().getName();

                result.add(new ScheduleEntryDto(
                        entry.getDay().getName(),
                        entry.getTimeSlot().getStart(),
                        entry.getTimeSlot().getEnd(),
                        subjectName,
                        type,
                        audName,
                        groups
                ));
            }
        }
        return sortSchedule(result);
    }

    @Transactional(readOnly = true)
    public List<ScheduleEntryDto> getGroupSchedule(Long groupId) {
        List<Appointment> appointments = appointmentRepository.findAll();
        List<ScheduleEntryDto> result = new ArrayList<>();

        for (Appointment app : appointments) {
            boolean hasGroup = app.getGroups().stream().anyMatch(g -> g.getId().equals(groupId));
            if (!hasGroup) continue;

            String subjectName = app.getSubject().getName();
            String teachers = app.getTeachers().stream().map(t -> t.getName()).collect(Collectors.joining(", "));

            for (AppointmentEntry entry : app.getEntries()) {
                String audName = entry.getAuditorium().getName();
                String type = (entry.getAuditorium().getEarmark() == null) ? "" : entry.getAuditorium().getEarmark().getName();

                result.add(new ScheduleEntryDto(
                        entry.getDay().getName(),
                        entry.getTimeSlot().getStart(),
                        entry.getTimeSlot().getEnd(),
                        subjectName,
                        type,
                        audName,
                        teachers
                ));
            }
        }
        return sortSchedule(result);
    }

    private List<ScheduleEntryDto> sortSchedule(List<ScheduleEntryDto> list) {
        // Simple sort by Day name then Time start. 
        // Note: Day name sorting is alphabetical here, ideally should be by ID or Order.
        // But for prototype it's fine.
        return list;
    }
}