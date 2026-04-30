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
            
            // Діагностика перед початком
            StringBuilder diagnosticErrors = new StringBuilder();
            java.util.Map<Object, Integer> requiredLoad = new java.util.HashMap<>();
            java.util.Map<Object, java.util.Set<Integer>> availableSlots = new java.util.HashMap<>();
            
            // Статистика блокувань для детального звіту (рахуємо унікальні слоти на об'єкт)
            java.util.Map<Object, java.util.Set<Integer>> buildingBlocked = new java.util.HashMap<>();
            java.util.Map<Object, java.util.Set<Integer>> gradeBlocked = new java.util.HashMap<>();
            java.util.Map<Object, java.util.Set<Integer>> dateBlocked = new java.util.HashMap<>();
            java.util.Map<Object, java.util.Set<Integer>> weekBlocked = new java.util.HashMap<>();

            for (ua.kiev.univ.schedule.scheduler.point.Point p : executor.getPoints()) {
                if (!p.online && p.earmark == -1) {
                    diagnosticErrors.append(String.format("• '%s': Немає відповідного типу аудиторії для %d студентів.\n", p.getSubjectName(), p.getGroups().stream().mapToInt(g -> g.getSize() != null ? g.getSize() : 0).sum()));
                } else {
                    int neededForTeacher = p.colors.length;
                    int perGroup = p.getInitialPairCount();

                    for (int i = 0; i < p.restriction.length; i++) {
                        boolean isPossible = true;
                        if (p.restriction[i] <= -2000000) {
                            for (Object obj : p.getGroups()) weekBlocked.computeIfAbsent(obj, k -> new java.util.HashSet<>()).add(i);
                            for (Object obj : p.getTeachers()) weekBlocked.computeIfAbsent(obj, k -> new java.util.HashSet<>()).add(i);
                            isPossible = false;
                        } else if (p.restriction[i] <= -1000000) {
                            for (Object obj : p.getGroups()) dateBlocked.computeIfAbsent(obj, k -> new java.util.HashSet<>()).add(i);
                            for (Object obj : p.getTeachers()) dateBlocked.computeIfAbsent(obj, k -> new java.util.HashSet<>()).add(i);
                            isPossible = false;
                        } else if (p.restriction[i] <= -100000) {
                            for (Object obj : p.getGroups()) buildingBlocked.computeIfAbsent(obj, k -> new java.util.HashSet<>()).add(i);
                            for (Object obj : p.getTeachers()) buildingBlocked.computeIfAbsent(obj, k -> new java.util.HashSet<>()).add(i);
                            isPossible = false;
                        } else if (p.restriction[i] <= -10) {
                            for (Object obj : p.getGroups()) gradeBlocked.computeIfAbsent(obj, k -> new java.util.HashSet<>()).add(i);
                            for (Object obj : p.getTeachers()) gradeBlocked.computeIfAbsent(obj, k -> new java.util.HashSet<>()).add(i);
                            isPossible = false;
                        }

                        if (isPossible) {
                            for (ua.kiev.univ.schedule.model.member.Group g : p.getGroups()) {
                                availableSlots.computeIfAbsent(g, k -> new java.util.HashSet<>()).add(i);
                            }
                            for (ua.kiev.univ.schedule.model.member.Teacher t : p.getTeachers()) {
                                availableSlots.computeIfAbsent(t, k -> new java.util.HashSet<>()).add(i);
                            }
                        }
                    }

                    for (ua.kiev.univ.schedule.model.member.Group g : p.getGroups()) {
                        requiredLoad.put(g, requiredLoad.getOrDefault(g, 0) + perGroup);
                    }
                    for (ua.kiev.univ.schedule.model.member.Teacher t : p.getTeachers()) {
                        requiredLoad.put(t, requiredLoad.getOrDefault(t, 0) + neededForTeacher);
                    }
                }
            }
            
            // Перевірка сумарного перевантаження
            for (java.util.Map.Entry<Object, Integer> entry : requiredLoad.entrySet()) {
                int needed = entry.getValue();
                int available = availableSlots.getOrDefault(entry.getKey(), java.util.Collections.emptySet()).size();
                
                if (needed > available) {
                    Object key = entry.getKey();
                    String name = (key instanceof ua.kiev.univ.schedule.model.member.Group) 
                        ? ((ua.kiev.univ.schedule.model.member.Group) key).getName()
                        : ((ua.kiev.univ.schedule.model.member.Teacher) key).getName();
                    
                    int bBuild = buildingBlocked.getOrDefault(key, java.util.Collections.emptySet()).size();
                    int bGrade = gradeBlocked.getOrDefault(key, java.util.Collections.emptySet()).size();
                    int bDates = dateBlocked.getOrDefault(key, java.util.Collections.emptySet()).size();
                    int bWeeks = weekBlocked.getOrDefault(key, java.util.Collections.emptySet()).size();

                    StringBuilder reason = new StringBuilder();
                    if (bDates > 0) reason.append(String.format(" період занять заблокував %d вікон;", bDates));
                    if (bWeeks > 0) reason.append(String.format(" періодичність заблокувала %d вікон;", bWeeks));
                    if (bBuild > 0) reason.append(String.format(" корпуси заблокували %d вікон;", bBuild));
                    if (bGrade > 0) reason.append(String.format(" обмеження викладачів заблокували %d вікон;", bGrade));

                    diagnosticErrors.append(String.format("• %s: Перевантаження. Потрібно %d пар, але є лише %d вільних вікон. Причини:%s\n", 
                        name, needed, available, reason.length() > 0 ? reason.toString() : " загальний брак часу."));
                }
            }

            if (diagnosticErrors.length() > 0) {
                currentStatus.setLastResult("FAIL");
                currentStatus.setLastError("Виявлено критичні конфлікти:\n" + diagnosticErrors.toString());
                return;
            }

            Progress progress = executor.initialize();
            int steps = 0;
            while (progress == Progress.BUILD) {
                progress = executor.step();
                steps++;
                currentStatus.setSteps(steps);
                if (steps % 10000 == 0) {
                    System.out.println("Step " + steps + "...");
                }
                if (steps > 50000000) { // Safety break
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
                
                for (java.util.Map.Entry<Object, Integer> entry : requiredLoad.entrySet()) {
                    String name = (entry.getKey() instanceof ua.kiev.univ.schedule.model.member.Group) 
                        ? ((ua.kiev.univ.schedule.model.member.Group) entry.getKey()).getName()
                        : ((ua.kiev.univ.schedule.model.member.Teacher) entry.getKey()).getName();
                    int available = availableSlots.getOrDefault(entry.getKey(), java.util.Collections.emptySet()).size();
                    errorDetail.append(String.format("• %s: Навантаження %d занять на %d реально доступних слотів.\n", name, entry.getValue(), available));
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
