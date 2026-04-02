package ua.kiev.univ.schedule.service;

import org.springframework.stereotype.Service;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.scheduler.Executor;
import ua.kiev.univ.schedule.scheduler.Progress;
import ua.kiev.univ.schedule.service.core.DataService;

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

    public String buildSchedule() {
        if (currentStatus.isBuilding()) {
            return "Build already in progress";
        }
        currentStatus.setBuilding(true);
        currentStatus.setLastResult(null);
        currentStatus.setLastError(null);
        currentStatus.setSteps(0);

        CompletableFuture.runAsync(this::runBuildProcess, threadPool)
                .thenRun(() -> {
                    currentStatus.setBuilding(false);
                    System.out.println("Build finished.");
                });

        return "Build started";
    }

    private void runBuildProcess() {
        try {
            System.out.println("Starting schedule generation...");
            
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

            Executor executor = new Executor();
            Progress progress = executor.initialize();

            int steps = 0;
            while (progress == Progress.BUILD) {
                progress = executor.step();
                steps++;
                currentStatus.setSteps(steps);
                if (steps % 1000 == 0) {
                    System.out.println("Step " + steps + "...");
                }
            }

            currentStatus.setLastResult(progress.toString());
            if (progress == Progress.DONE) {
                System.out.println("Schedule found! Steps: " + steps);
                executor.setAppointments();
                DataService.write(null);
            } else {
                System.out.println("Failed to build schedule. Final status: " + progress + ". Steps: " + steps);
                String error = "Could not find a valid combination for all lessons. ";
                if (steps <= lessonsCount) {
                    error += "Check if you have enough auditoriums or if there are conflicting restrictions for some lessons.";
                }
                currentStatus.setLastError(error);
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