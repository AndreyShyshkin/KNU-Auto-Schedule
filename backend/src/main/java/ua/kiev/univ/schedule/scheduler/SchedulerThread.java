package ua.kiev.univ.schedule.scheduler;

import ua.kiev.univ.schedule.util.ExecutionException;

public class SchedulerThread extends Thread {

    public volatile boolean pause, stop;
    private final Scheduler scheduler;
    private final Executor executor = new Executor();

    public SchedulerThread(Scheduler scheduler) {
        this.scheduler = scheduler;
        setDaemon(true);
    }

    @Override
    public void run() {
        scheduler.onStart();
        Progress progress = executor.initialize();
        while (progress == Progress.BUILD) {
            synchronized (this) {
                if (pause) {
                    Progress.PAUSE.value = Progress.BUILD.value;
                    scheduler.onPause();
                    pause = false;
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new ExecutionException(e);
                    }
                    scheduler.onContinue();
                }
            }
            if (stop) {
                scheduler.onStop();
                return;
            }
            progress = executor.step();
            scheduler.onBuild();
        }
        executor.setAppointments();
        scheduler.onFinish(progress);
    }
}