package ua.kiev.univ.schedule.service.core;

import ua.kiev.univ.schedule.model.core.NamedEntity;
import ua.kiev.univ.schedule.service.core.property.BeanProperty;
import ua.kiev.univ.schedule.service.core.property.Property;
import ua.kiev.univ.schedule.util.StringUtils;

public abstract class NamedEntityService<E extends NamedEntity> extends EnablableEntityService<E> {

    protected Property<String, E> nameProperty = new BeanProperty<>("name", String.class);

    public NamedEntityService(Class<E> entityClass) {
        super(entityClass);
        properties.add(nameProperty);
    }

    @Override
    public boolean isCellErroneous(E entity, Property<?, E> property) {
        if (property == nameProperty) {
            return StringUtils.isBlank(nameProperty.getValue(entity));
        }
        return super.isCellErroneous(entity, property);
    }
}