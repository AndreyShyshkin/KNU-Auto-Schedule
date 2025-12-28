package ua.kiev.univ.schedule.view.input.lesson.subject;

import ua.kiev.univ.schedule.model.lesson.SubjectedEntity;
import ua.kiev.univ.schedule.service.lesson.SubjectedEntityService;
import ua.kiev.univ.schedule.view.core.Label;
import ua.kiev.univ.schedule.view.core.Panel;
import ua.kiev.univ.schedule.view.input.table.TablePane;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class SubjectedPane<E extends SubjectedEntity> extends Panel {

    public final Label subjectLabel;
    public final SubjectComboBox<E> subjectComboBox;
    public final TablePane<E> tablePane;

    public SubjectedPane(SubjectedEntityService<E> service, String key) {
        subjectLabel = new Label(key + ".subject.label");
        subjectComboBox = new SubjectComboBox<>(service);
        tablePane = new TablePane<>(service, key);

        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.weightx = 0.25;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.gridx = 0;
        add(subjectLabel, constraints);

        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 1;
        add(subjectComboBox, constraints);

        constraints = new GridBagConstraints();
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        add(tablePane, constraints);
    }

    @Override
    public void refresh() {
        subjectComboBox.refresh();
        tablePane.refresh(); // Не забуваємо оновити і таблицю
    }

    @Override
    public void loadLanguage() {
        subjectLabel.loadLanguage();
        tablePane.loadLanguage();
    }
}