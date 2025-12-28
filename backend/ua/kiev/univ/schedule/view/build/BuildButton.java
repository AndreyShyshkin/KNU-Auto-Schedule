package ua.kiev.univ.schedule.view.build;

import ua.kiev.univ.schedule.scheduler.Scheduler;
import ua.kiev.univ.schedule.view.core.Button;

public class BuildButton extends Button {

    public BuildButton(final Scheduler scheduler, String key) {
        super(key);

        addActionListener(e -> scheduler.build());
    }

    @Override
    public void refresh() {

    }
}