package ua.kiev.univ.schedule.view.input.description;

import ua.kiev.univ.schedule.model.department.DescriptionedEntity;
import ua.kiev.univ.schedule.service.department.DescriptionedEntityService;
import ua.kiev.univ.schedule.view.core.TextArea;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class DescriptionTextArea<E extends DescriptionedEntity> extends TextArea {

    private final DescriptionedEntityService<E> service;

    public DescriptionTextArea(DescriptionedEntityService<E> service) {
        this.service = service;
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (service.isDescriptionEnable()) {
                    service.setDescription(getText());
                }
            }
        });
    }

    @Override
    public void refresh() {
        boolean enable = service.isDescriptionEnable();
        this.setEditable(enable);
        this.setText(enable ? service.getDescription() : null);
    }
}