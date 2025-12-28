package ua.kiev.univ.schedule.view.input.description;

import ua.kiev.univ.schedule.model.department.DescriptionedEntity;
import ua.kiev.univ.schedule.service.department.DescriptionedEntityService;
import ua.kiev.univ.schedule.view.core.Label;
import ua.kiev.univ.schedule.view.core.Panel;

import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class DescriptionPane<E extends DescriptionedEntity> extends Panel {

    private final Label label;
    private final DescriptionTextArea<E> textArea;

    public DescriptionPane(DescriptionedEntityService<E> service, String key) {
        label = new Label(key + ".label");
        textArea = new DescriptionTextArea<>(service);
        JScrollPane scrollPane = new JScrollPane(textArea);

        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1.0;

        constraints.gridy = 0;
        add(label, constraints);

        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        add(scrollPane, constraints);
    }

    @Override
    public void refresh() {
        textArea.refresh();
    }

    @Override
    public void loadLanguage() {
        label.loadLanguage();
    }
}