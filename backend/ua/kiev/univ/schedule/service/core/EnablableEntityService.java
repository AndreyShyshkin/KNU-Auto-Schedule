package ua.kiev.univ.schedule.service.core;

import ua.kiev.univ.schedule.model.core.EnablableEntity;
import ua.kiev.univ.schedule.service.core.property.BeanProperty;
import ua.kiev.univ.schedule.service.core.property.Property;
import ua.kiev.univ.schedule.service.core.table.EditableTableService;

public class EnablableEntityService<E extends EnablableEntity> extends EditableTableService<E> {

    protected Property<Boolean, E> enableProperty = new BeanProperty<>("enable", Boolean.class);

    public EnablableEntityService(Class<E> entityClass) {
        super(entityClass);
        properties.add(enableProperty);
    }
}