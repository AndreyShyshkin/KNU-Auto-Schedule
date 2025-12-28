package ua.kiev.univ.schedule.view.build;

import ua.kiev.univ.schedule.scheduler.Scheduler;
import ua.kiev.univ.schedule.view.core.Button;

public class PauseButton extends Button {

    public PauseButton(final Scheduler scheduler, String key) {
        super(key);
        addActionListener(e -> scheduler.pause());
    }

    @Override
    public void refresh() {
    }
}