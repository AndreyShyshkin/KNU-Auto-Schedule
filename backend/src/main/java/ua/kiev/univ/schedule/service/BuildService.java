package ua.kiev.univ.schedule.service;

import org.springframework.stereotype.Service;
import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.ScheduleVersion;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.repository.AppointmentRepository;
import ua.kiev.univ.schedule.repository.ScheduleVersionRepository;
import ua.kiev.univ.schedule.scheduler.Executor;
import ua.kiev.univ.schedule.scheduler.Progress;
import ua.kiev.univ.schedule.service.core.DataInitializationService;
import ua.kiev.univ.schedule.service.core.DataService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BuildService {

    public static class BuildStatus {
        private boolean isBuilding;
        private String lastResult;
        private int steps;
        private String lastError;

        public boolean isBuilding() { return isBuilding; }
        public void setBuilding(boolean building) { isBuilding = building; }
        public String getLastResult() { return lastResult; }
        public void setLastResult(String lastResult) { this.lastResult = lastResult; }
        public int getSteps() { return steps; }
        public void setSteps(int steps) { this.steps = steps; }
        public String getLastError() { return lastError; }
        public void setLastError(String lastError) { this.lastError = lastError; }
    }

    private final ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private final BuildStatus currentStatus = new BuildStatus();
    private final AppointmentRepository appointmentRepository;
    private final ScheduleVersionRepository scheduleVersionRepository;
    private final DataInitializationService dataInitializationService;

    public BuildService(AppointmentRepository appointmentRepository, ScheduleVersionRepository scheduleVersionRepository, DataInitializationService dataInitializationService) {
        this.appointmentRepository = appointmentRepository;
        this.scheduleVersionRepository = scheduleVersionRepository;
        this.dataInitializationService = dataInitializationService;
    }

    public String buildSchedule(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (currentStatus.isBuilding()) {
            return "Build already in progress";
        }
        currentStatus.setBuilding(true);
        currentStatus.setLastResult(null);
        currentStatus.setLastError(null);
        currentStatus.setSteps(0);

        CompletableFuture.runAsync(() -> runBuildProcess(startDate, endDate), threadPool)
                .thenRun(() -> {
                    currentStatus.setBuilding(false);
                    System.out.println("Build finished.");
                });

        return "Build started";
    }

    private void runBuildProcess(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        try {
            System.out.println("Refreshing data before build...");
            dataInitializationService.initializeData();

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
                if (steps > 1000000) { // Extended safety break for calendar mode
                    progress = Progress.FAIL;
                    break;
                }
            }

            currentStatus.setLastResult(progress.toString());
            if (progress == Progress.DONE) {
                System.out.println("Schedule found! Steps: " + steps);
                
                // Create new version
                ScheduleVersion version = new ScheduleVersion(
                    "Розклад " + (startDate != null ? startDate.format(DateTimeFormatter.ofPattern("dd.MM")) : "") + 
                    " - " + (endDate != null ? endDate.format(DateTimeFormatter.ofPattern("dd.MM")) : ""),
                    LocalDateTime.now(),
                    true,
                    startDate,
                    endDate
                );
                
                // Deactivate others
                scheduleVersionRepository.findAll().forEach(v -> {
                    v.setCurrent(false);
                    scheduleVersionRepository.save(v);
                });
                
                ScheduleVersion savedVersion = scheduleVersionRepository.save(version);
                
                // Clear memory appointments and regenerate from points
                List<Appointment> appointments = DataService.getEntities(Appointment.class);
                appointments.clear();
                executor.setAppointments();
                
                // Link each appointment to the new version
                for (Appointment app : appointments) {
                    app.setVersion(savedVersion);
                }
                
                appointmentRepository.saveAll(appointments);
                DataService.write(null);
            } else {
                System.out.println("Failed to build schedule. Final status: " + progress + ". Steps: " + steps);
                
                StringBuilder errorDetail = new StringBuilder();
                errorDetail.append("Не вдалося знайти рішення для всіх занять. Причини:\n");
                
                for (ua.kiev.univ.schedule.scheduler.point.Point p : executor.getPoints()) {
                    if (p.earmark == -1) {
                        int totalStudents = p.getGroups().stream().mapToInt(g -> g.getSize() != null ? g.getSize() : 0).sum();
                        errorDetail.append(String.format("• '%s': Студентів (%d) забагато для наявних аудиторій.\n", p.getSubjectName(), totalStudents));
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
    
    public BuildStatus getStatus() {
        return currentStatus;
    }

    public boolean isBuilding() {
        return currentStatus.isBuilding();
    }
}
