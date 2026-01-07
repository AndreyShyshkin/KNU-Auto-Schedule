package ua.kiev.univ.schedule.service.core;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.core.Entity;
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

import java.util.List;

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

    @PostConstruct
    @Transactional
    public void init() {
        DataService.setPersistenceService(this);
        System.out.println("Initializing DataService from Spring Repositories...");
        
        load(Time.class, timeRepository.findAll());
        load(Day.class, dayRepository.findAll());
        load(Faculty.class, facultyRepository.findAll());
        load(Earmark.class, earmarkRepository.findAll());
        load(Subject.class, subjectRepository.findAll());
        load(Chair.class, chairRepository.findAll());
        load(Speciality.class, specialityRepository.findAll());
        load(Auditorium.class, auditoriumRepository.findAll());
        load(Teacher.class, teacherRepository.findAll());
        load(Group.class, groupRepository.findAll());
        load(Lesson.class, lessonRepository.findAll());
        load(Appointment.class, appointmentRepository.findAll());
        
        System.out.println("DataService initialized.");
    }

    private <E extends Entity> void load(Class<E> clazz, List<E> entities) {
        EntityList<E> list = DataService.getEntities(clazz);
        list.clear();
        list.addAll(entities);
    }
    
    // Метод для сохранения (вызывается из DataService)
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
        appointmentRepository.saveAll(DataService.getEntities(Appointment.class));
    }
}