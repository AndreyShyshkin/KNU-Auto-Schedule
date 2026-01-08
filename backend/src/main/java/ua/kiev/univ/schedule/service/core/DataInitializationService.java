package ua.kiev.univ.schedule.service.core;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.AppointmentEntry;
import ua.kiev.univ.schedule.model.appointment.HalvedAppointment;
import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.model.date.Date;
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
import ua.kiev.univ.schedule.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataInitializationService {

    private final TimeRepository timeRepository;
    private final DayRepository dayRepository;
    private final FacultyRepository facultyRepository;
    private final EarmarkRepository earmarkRepository;
    private final SubjectRepository subjectRepository;
    private final ChairRepository chairRepository;
    private final SpecialityRepository specialityRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final TeacherRepository teacherRepository;
    private final GroupRepository groupRepository;
    private final LessonRepository lessonRepository;
    private final AppointmentRepository appointmentRepository;

    public DataInitializationService(TimeRepository timeRepository, DayRepository dayRepository,
                                     FacultyRepository facultyRepository, EarmarkRepository earmarkRepository,
                                     SubjectRepository subjectRepository, ChairRepository chairRepository,
                                     SpecialityRepository specialityRepository, AuditoriumRepository auditoriumRepository,
                                     TeacherRepository teacherRepository, GroupRepository groupRepository,
                                     LessonRepository lessonRepository, AppointmentRepository appointmentRepository) {
        this.timeRepository = timeRepository;
        this.dayRepository = dayRepository;
        this.facultyRepository = facultyRepository;
        this.earmarkRepository = earmarkRepository;
        this.subjectRepository = subjectRepository;
        this.chairRepository = chairRepository;
        this.specialityRepository = specialityRepository;
        this.auditoriumRepository = auditoriumRepository;
        this.teacherRepository = teacherRepository;
        this.groupRepository = groupRepository;
        this.lessonRepository = lessonRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public void initializeData() {
        DataService.setPersistenceService(this);
        System.out.println("Initializing DataService from Spring Repositories...");
        
        load(Time.class, timeRepository.findAll());
        
        List<Day> days = dayRepository.findAll();
        for (Day day : days) {
            day.getTimes().size(); 
        }
        load(Day.class, days);
        
        load(Faculty.class, facultyRepository.findAll());
        load(Earmark.class, earmarkRepository.findAll());
        load(Subject.class, subjectRepository.findAll());
        load(Chair.class, chairRepository.findAll());
        load(Speciality.class, specialityRepository.findAll());
        load(Auditorium.class, auditoriumRepository.findAll());
        load(Teacher.class, teacherRepository.findAll());
        load(Group.class, groupRepository.findAll());
        
        List<Lesson> lessons = lessonRepository.findAll();
        for (Lesson l : lessons) {
            l.getTeachers().size();
            l.getGroups().size();
        }
        load(Lesson.class, lessons);
        
        List<Appointment> appointments = appointmentRepository.findAll();
        for (Appointment a : appointments) {
            // Reconstruct transient fields for legacy logic if needed
            // But display usually relies on snapshots now.
        }
        load(Appointment.class, appointments);
        
        System.out.println("DataService initialized.");
    }

    private <E extends Entity> void load(Class<E> clazz, List<E> entities) {
        EntityList<E> list = DataService.getEntities(clazz);
        list.clear();
        list.addAll(entities);
    }
    
    @Transactional
    public void saveAll() {
        timeRepository.saveAll(DataService.getEntities(Time.class));
        dayRepository.saveAll(DataService.getEntities(Day.class));
        facultyRepository.saveAll(DataService.getEntities(Faculty.class));
        earmarkRepository.saveAll(DataService.getEntities(Earmark.class));
        subjectRepository.saveAll(DataService.getEntities(Subject.class));
        chairRepository.saveAll(DataService.getEntities(Chair.class));
        specialityRepository.saveAll(DataService.getEntities(Speciality.class));
        auditoriumRepository.saveAll(DataService.getEntities(Auditorium.class));
        teacherRepository.saveAll(DataService.getEntities(Teacher.class));
        groupRepository.saveAll(DataService.getEntities(Group.class));
        lessonRepository.saveAll(DataService.getEntities(Lesson.class));
        
        List<Appointment> appointments = DataService.getEntities(Appointment.class);
        for (Appointment app : appointments) {
            if (app.getSubject() != null) {
                app.setSubjectName(app.getSubject().getName());
            }
            app.setTeacherNames(app.getTeachers().stream().map(Teacher::getName).collect(Collectors.joining(", ")));
            app.setGroupNames(app.getGroups().stream().map(Group::getName).collect(Collectors.joining(", ")));
            
            app.setTeacherIds(app.getTeachers().stream().map(t -> t.getId().toString()).collect(Collectors.joining(",")));
            app.setGroupIds(app.getGroups().stream().map(g -> g.getId().toString()).collect(Collectors.joining(",")));

            if (app instanceof HalvedAppointment) {
                HalvedAppointment happ = (HalvedAppointment) app;
                if (happ.getDate() != null) {
                    happ.setHalvedDayName(happ.getDate().getDay().getName());
                    happ.setHalvedTimeRange(happ.getDate().getTime().getStart() + " - " + happ.getDate().getTime().getEnd());
                }
                if (happ.getAuditoriums() != null) {
                    happ.setHalvedAuditoriumNames(happ.getAuditoriums().stream().map(Auditorium::getName).collect(Collectors.joining(", ")));
                }
                if (happ.getPart() != null) {
                    happ.setHalvedPartName(happ.getPart().name());
                }
            }

            app.getEntries().clear();
            for (Map.Entry<Date, List<Auditorium>> mapEntry : app.getAuditoriumMap().entrySet()) {
                Date date = mapEntry.getKey();
                String dayName = date.getDay().getName();
                String start = date.getTime().getStart();
                String end = date.getTime().getEnd();
                
                for (Auditorium aud : mapEntry.getValue()) {
                    AppointmentEntry entry = new AppointmentEntry(app, dayName, start, end, aud.getName());
                    app.getEntries().add(entry);
                }
            }
        }
        appointmentRepository.saveAll(appointments);
    }
}