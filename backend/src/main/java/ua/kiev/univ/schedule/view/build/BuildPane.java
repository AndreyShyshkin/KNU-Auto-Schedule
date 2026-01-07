package ua.kiev.univ.schedule.view.build;

import ua.kiev.univ.schedule.scheduler.Scheduler;
import ua.kiev.univ.schedule.view.core.Panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class BuildPane extends Panel {

    private final Scheduler scheduler = new Scheduler();
    public final AppointmentPane appointmentPane;
    public final BuildButton buildButton;
    public final PauseButton pauseButton;
    public final StopButton stopButton;
    public final ProgressBar progressBar;

    public BuildPane(String key) {
        appointmentPane = new AppointmentPane(key);
        buildButton = new BuildButton(scheduler, key + ".build");
        pauseButton = new PauseButton(scheduler, key + ".pause");
        stopButton = new StopButton(scheduler, key + ".stop");
        progressBar = new ProgressBar(key + ".progress");

        scheduler.setBuildPane(this);

        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        // Таблиця результатів (займає весь верх)
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        add(appointmentPane, constraints);

        // Кнопки керування
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.weightx = 0.5;
        add(buildButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 0;
        add(pauseButton, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.weightx = 0.5;
        add(stopButton, constraints);

        // Прогрес бар
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 0;
        add(progressBar, constraints);

        refresh();
    }

    @Override
    public void refresh() {
        appointmentPane.refresh();
    }

    @Override
    public void loadLanguage() {
        appointmentPane.loadLanguage();
        buildButton.loadLanguage();
        pauseButton.loadLanguage();
        stopButton.loadLanguage();
        progressBar.loadLanguage();
    }
}