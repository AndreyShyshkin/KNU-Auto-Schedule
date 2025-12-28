package ua.kiev.univ.schedule.service.department;

import ua.kiev.univ.schedule.model.department.DescriptionedEntity;
import ua.kiev.univ.schedule.service.core.NamedEntityService;
import ua.kiev.univ.schedule.view.input.description.DescriptionPane;

public abstract class DescriptionedEntityService<E extends DescriptionedEntity> extends NamedEntityService<E> {

    protected DescriptionPane<E> descriptionPane;

    public DescriptionedEntityService(Class<E> entityClass) {
        super(entityClass);
    }

    @Override
    public void selectRow(int index) {
        super.selectRow(index);
        if (descriptionPane != null) {
            descriptionPane.refresh();
        }
    }

    public boolean isDescriptionEnable() {
        return selectedRow != null;
    }

    public void setDescription(String description) {
        if (selectedRow != null) {
            selectedRow.setDescription(description);
        }
    }

    public String getDescription() {
        return (selectedRow != null) ? selectedRow.getDescription() : "";
    }

    public void setDescriptionPane(DescriptionPane<E> descriptionPane) {
        this.descriptionPane = descriptionPane;
    }
}