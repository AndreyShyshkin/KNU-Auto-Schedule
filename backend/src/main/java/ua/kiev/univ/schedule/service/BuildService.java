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

    private final ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private volatile boolean isBuilding = false;

    public String buildSchedule() {
        if (isBuilding) {
            return "Build already in progress";
        }
        isBuilding = true;

        CompletableFuture.runAsync(this::runBuildProcess, threadPool)
                .thenRun(() -> {
                    isBuilding = false;
                    System.out.println("Build finished.");
                });

        return "Build started";
    }

    private void runBuildProcess() {
        try {
            System.out.println("Starting schedule generation...");
            
            System.out.println("Lessons: " + DataService.getEntities(Lesson.class).size());
            System.out.println("Days: " + DataService.getEntities(Day.class).size());
            System.out.println("Times: " + DataService.getEntities(Time.class).size());
            System.out.println("Earmarks: " + DataService.getEntities(Earmark.class).size());
            System.out.println("Auditoriums: " + DataService.getEntities(Auditorium.class).size());
            
            Executor executor = new Executor();
            Progress progress = executor.initialize();

            int steps = 0;
            while (progress == Progress.BUILD) {
                progress = executor.step();
                steps++;
                if (steps % 1000 == 0) {
                    System.out.println("Step " + steps + "...");
                }
            }

            if (progress == Progress.DONE) {
                System.out.println("Schedule found! Steps: " + steps);
                executor.setAppointments();
                DataService.write(null);
            } else {
                System.out.println("Failed to build schedule. Final status: " + progress + ". Steps: " + steps);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean isBuilding() {
        return isBuilding;
    }
}