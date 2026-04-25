package ua.kiev.univ.schedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kiev.univ.schedule.dto.ScheduleEntryDto;
import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.AppointmentEntry;
import ua.kiev.univ.schedule.repository.AppointmentRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleQueryService {

    private final AppointmentRepository appointmentRepository;

    public ScheduleQueryService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleEntryDto> getTeacherSchedule(Long teacherId, Long versionId) {
        List<Appointment> appointments;
        if (versionId != null) {
            appointments = appointmentRepository.findByVersionId(versionId);
        } else {
            appointments = appointmentRepository.findByVersionIsCurrentTrue();
        }
        
        List<ScheduleEntryDto> result = new ArrayList<>();
        String searchId = teacherId.toString();

        for (Appointment app : appointments) {
            String ids = app.getTeacherIds();
            if (ids == null) continue;
            boolean found = false;
            for (String id : ids.split(",")) {
                if (id.equals(searchId)) { found = true; break; }
            }
            if (!found) continue;

            for (AppointmentEntry entry : app.getEntries()) {
                ScheduleEntryDto dto = new ScheduleEntryDto(
                        entry.getDayName(),
                        entry.getTimeStart(),
                        entry.getTimeEnd(),
                        app.getSubjectName(),
                        app.getLessonTypeNames(), 
                        app.getEarmarkName(),
                        entry.getBuildingName(),
                        entry.getAuditoriumName(),
                        entry.getGroupNames() != null ? entry.getGroupNames() : app.getGroupNames()
                );
                if (entry.getActualDate() != null) {
                    dto.setActualDate(entry.getActualDate().toString());
                }
                result.add(dto);
            }
        }
        return sortSchedule(result);
    }

    @Transactional(readOnly = true)
    public List<ScheduleEntryDto> getGroupSchedule(Long groupId, Long versionId) {
        List<Appointment> appointments;
        if (versionId != null) {
            appointments = appointmentRepository.findByVersionId(versionId);
        } else {
            appointments = appointmentRepository.findByVersionIsCurrentTrue();
        }
        
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
                ScheduleEntryDto dto = new ScheduleEntryDto(
                        entry.getDayName(),
                        entry.getTimeStart(),
                        entry.getTimeEnd(),
                        app.getSubjectName(),
                        app.getLessonTypeNames(),
                        app.getEarmarkName(),
                        entry.getBuildingName(),
                        entry.getAuditoriumName(),
                        entry.getTeacherNames() != null ? entry.getTeacherNames() : app.getTeacherNames()
                );
                if (entry.getActualDate() != null) {
                    dto.setActualDate(entry.getActualDate().toString());
                }
                result.add(dto);
            }
        }
        return sortSchedule(result);
    }

    @Transactional(readOnly = true)
    public List<ScheduleEntryDto> getAllSchedule(Long versionId) {
        List<Appointment> appointments;
        if (versionId != null) {
            appointments = appointmentRepository.findByVersionId(versionId);
        } else {
            appointments = appointmentRepository.findByVersionIsCurrentTrue();
        }
        
        List<ScheduleEntryDto> result = new ArrayList<>();
        for (Appointment app : appointments) {
            for (AppointmentEntry entry : app.getEntries()) {
                String tNames = entry.getTeacherNames() != null ? entry.getTeacherNames() : app.getTeacherNames();
                String gNames = entry.getGroupNames() != null ? entry.getGroupNames() : app.getGroupNames();
                ScheduleEntryDto dto = new ScheduleEntryDto(
                        entry.getDayName(),
                        entry.getTimeStart(),
                        entry.getTimeEnd(),
                        app.getSubjectName(),
                        app.getLessonTypeNames(),
                        app.getEarmarkName(),
                        entry.getBuildingName(),
                        entry.getAuditoriumName(),
                        tNames + " | " + gNames
                );
                if (entry.getActualDate() != null) {
                    dto.setActualDate(entry.getActualDate().toString());
                }
                result.add(dto);
            }
        }
        return sortSchedule(result);
    }

    private List<ScheduleEntryDto> sortSchedule(List<ScheduleEntryDto> list) {
        // Сортування за датою, а потім за часом
        list.sort((e1, e2) -> {
            if (e1.getActualDate() != null && e2.getActualDate() != null) {
                int res = e1.getActualDate().compareTo(e2.getActualDate());
                if (res != 0) return res;
            }
            return e1.getTimeStart().compareTo(e2.getTimeStart());
        });
        return list;
    }
}
