package ua.kiev.univ.schedule.service.core;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.AppointmentEntry;
import ua.kiev.univ.schedule.model.appointment.ScheduleVersion;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.repository.AppointmentRepository;
import ua.kiev.univ.schedule.repository.ScheduleVersionRepository;
import ua.kiev.univ.schedule.scheduler.Executor;
import ua.kiev.univ.schedule.scheduler.Progress;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataInitializationService implements CommandLineRunner {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleVersionRepository scheduleVersionRepository;

    public DataInitializationService(AppointmentRepository appointmentRepository, ScheduleVersionRepository scheduleVersionRepository) {
        this.appointmentRepository = appointmentRepository;
        this.scheduleVersionRepository = scheduleVersionRepository;
    }

    @Override
    public void run(String... args) {
        initializeData();
    }

    @Transactional
    public void initializeData() {
        try {
            ua.kiev.univ.schedule.service.core.DatabaseService.loadAll();
            int lessons = DataService.getEntities(Lesson.class).size();
            int auds = DataService.getEntities(Auditorium.class).size();
            int days = DataService.getEntities(Day.class).size();
            System.out.println("Data loaded: " + lessons + " lessons, " + auds + " auditoriums, " + days + " days.");
        } catch (Exception e) {
            System.err.println("Failed to load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BuildStatus currentStatus = new BuildStatus();

    public static class BuildStatus {
        private boolean building;
        private String lastResult;
        private String lastError;
        private int steps;

        public boolean isBuilding() { return building; }
        public void setBuilding(boolean building) { this.building = building; }
        public String getLastResult() { return lastResult; }
        public void setLastResult(String lastResult) { this.lastResult = lastResult; }
        public int getSteps() { return steps; }
        public void setSteps(int steps) { this.steps = steps; }
        public String getLastError() { return lastError; }
        public void setLastError(String lastError) { this.lastError = lastError; }
    }

    public BuildStatus getBuildStatus() {
        return currentStatus;
    }

    @Transactional
    public void runBuildProcess(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        try {
            System.out.println("Starting schedule generation for period: " + startDate + " to " + endDate);
            
            int lessonsCount = DataService.getEntities(Lesson.class).size();
            int daysCount = DataService.getEntities(Day.class).size();
            int timesCount = DataService.getEntities(Time.class).size();
            
            if (lessonsCount == 0) {
                currentStatus.setLastResult("FAIL");
                currentStatus.setLastError("No lessons to schedule");
                return;
            }
            if (daysCount == 0 || timesCount == 0) {
                currentStatus.setLastResult("FAIL");
                currentStatus.setLastError("No days or time slots configured");
                return;
            }

            Executor executor = new Executor(startDate, endDate);
            Progress progress = executor.initialize();

            int steps = 0;
            while (progress == Progress.BUILD) {
                progress = executor.step();
                steps++;
                currentStatus.setSteps(steps);
                if (steps % 1000 == 0) {
                    System.out.println("Step " + steps + "...");
                }
                if (steps > 1000000) { // Safety break
                    progress = Progress.FAIL;
                    break;
                }
            }

            currentStatus.setLastResult(progress.toString());
            if (progress == Progress.DONE) {
                System.out.println("Schedule found! Steps: " + steps);
                
                // Створюємо нову версію розкладу
                ScheduleVersion version = new ScheduleVersion(
                    "Розклад від " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM HH:mm")),
                    LocalDateTime.now(),
                    true,
                    startDate,
                    endDate
                );
                
                // Деактивуємо інші версії
                scheduleVersionRepository.findAll().forEach(v -> {
                    v.setCurrent(false);
                    scheduleVersionRepository.save(v);
                });
                
                ScheduleVersion savedVersion = scheduleVersionRepository.save(version);
                
                // Зберігаємо призначення
                List<Appointment> appointments = DataService.getEntities(Appointment.class);
                appointments.clear();
                executor.setAppointments();
                
                for (Appointment app : appointments) {
                    app.setVersion(savedVersion);
                }
                
                appointmentRepository.saveAll(appointments);
                try {
                    DataService.write(null);
                } catch (Exception e) {
                    System.err.println("Failed to write to legacy storage: " + e.getMessage());
                }
            } else {
                System.out.println("Failed to build schedule. Final status: " + progress + ". Steps: " + steps);
                
                StringBuilder errorDetail = new StringBuilder();
                errorDetail.append("Не вдалося знайти рішення для всіх занять. Причини:\n");
                
                for (ua.kiev.univ.schedule.scheduler.point.Point p : executor.getPoints()) {
                    if (p.earmark == -1) {
                        int totalStudents = p.getGroups().stream().mapToInt(g -> g.getSize() != null ? g.getSize() : 0).sum();
                        errorDetail.append(String.format("• '%s': Немає аудиторій з місткістю %d ос.\n", p.getSubjectName(), totalStudents));
                    }
                }
                
                if (errorDetail.length() < 100) {
                    errorDetail.append("• Перевірте обмеження викладачів та груп або кількість доступних аудиторій.");
                }
                currentStatus.setLastError(errorDetail.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            currentStatus.setLastResult("ERROR");
            currentStatus.setLastError("Internal error: " + e.getMessage());
        }
    }
}
