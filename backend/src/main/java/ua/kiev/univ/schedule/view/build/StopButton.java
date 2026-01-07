package ua.kiev.univ.schedule.view.build;

import ua.kiev.univ.schedule.scheduler.Scheduler;
import ua.kiev.univ.schedule.view.core.Button;

public class StopButton extends Button {

    public StopButton(final Scheduler scheduler, String key) {
        super(key);
        addActionListener(e -> scheduler.stop());
    }

    @Override
    public void refresh() {
    }
}