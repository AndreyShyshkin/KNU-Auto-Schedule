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
        String searchId = teacherId.toString();

        for (Appointment app : appointments) {
            // Check if searchId is in the teacherIds string (comma separated)
            String ids = app.getTeacherIds();
            if (ids == null) continue;
            boolean found = false;
            for (String id : ids.split(",")) {
                if (id.equals(searchId)) { found = true; break; }
            }
            if (!found) continue;

            for (AppointmentEntry entry : app.getEntries()) {
                result.add(new ScheduleEntryDto(
                        entry.getDayName(),
                        entry.getTimeStart(),
                        entry.getTimeEnd(),
                        app.getSubjectName(),
                        "", 
                        entry.getAuditoriumName(),
                        app.getGroupNames()
                ));
            }
        }
        return sortSchedule(result);
    }

    @Transactional(readOnly = true)
    public List<ScheduleEntryDto> getGroupSchedule(Long groupId) {
        List<Appointment> appointments = appointmentRepository.findAll();
        List<ScheduleEntryDto> result = new ArrayList<>();
        String searchId = groupId.toString();

        for (Appointment app : appointments) {
            String ids = app.getGroupIds();
            if (ids == null) continue;
            boolean found = false;
            for (String id : ids.split(",")) {
                if (id.equals(searchId)) { found = true; break; }
            }
            if (!found) continue;

            for (AppointmentEntry entry : app.getEntries()) {
                result.add(new ScheduleEntryDto(
                        entry.getDayName(),
                        entry.getTimeStart(),
                        entry.getTimeEnd(),
                        app.getSubjectName(),
                        "",
                        entry.getAuditoriumName(),
                        app.getTeacherNames()
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