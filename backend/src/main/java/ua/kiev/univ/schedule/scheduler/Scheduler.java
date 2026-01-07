package ua.kiev.univ.schedule.scheduler;

import ua.kiev.univ.schedule.view.build.BuildPane;

public class Scheduler {

    private BuildPane buildPane;
    private SchedulerThread thread;

    public void build() {
        if (buildPane != null && buildPane.buildButton != null) {
            buildPane.buildButton.setEnabled(false);
        }
        if (thread == null) {
            thread = new SchedulerThread(this);
            thread.start();
        } else {
            synchronized (thread) {
                thread.notify();
            }
        }
    }

    public void pause() {
        if (buildPane != null) {
            buildPane.pauseButton.setEnabled(false);
            buildPane.stopButton.setEnabled(false);
        }
        if (thread != null) {
            thread.pause = true;
        }
    }

    public void stop() {
        if (buildPane != null) {
            buildPane.pauseButton.setEnabled(false);
            buildPane.stopButton.setEnabled(false);
        }
        if (thread != null) {
            thread.stop = true;
        }
    }

    public void onContinue() {
        if (buildPane != null) {
            buildPane.pauseButton.setEnabled(true);
            buildPane.stopButton.setEnabled(true);
        }
    }

    public void onStart() {
        onContinue();
        if (buildPane != null && buildPane.progressBar != null) {
            buildPane.progressBar.setMaximum(Progress.DONE.value);
            buildPane.progressBar.setProgress(Progress.BUILD);
        }
    }

    public void onBuild() {
        if (buildPane != null && buildPane.progressBar != null) {
            buildPane.progressBar.setProgress(Progress.BUILD);
        }
    }

    public void onPause() {
        if (buildPane != null) {
            buildPane.progressBar.setProgress(Progress.PAUSE);
            buildPane.buildButton.setEnabled(true);
        }
    }

    public void onStop() {
        thread = null;
        if (buildPane != null) {
            buildPane.progressBar.setProgress(Progress.STOP);
            buildPane.buildButton.setEnabled(true);
        }
    }

    public void onFinish(Progress progress) {
        if (buildPane != null) {
            buildPane.pauseButton.setEnabled(false);
            buildPane.stopButton.setEnabled(false);
            if (buildPane.appointmentPane != null) {
                buildPane.appointmentPane.refresh();
            }
            buildPane.progressBar.setProgress(progress);
            buildPane.buildButton.setEnabled(true);
        }
        thread = null;
    }

    public void setBuildPane(BuildPane buildPane) {
        this.buildPane = buildPane;
    }
}