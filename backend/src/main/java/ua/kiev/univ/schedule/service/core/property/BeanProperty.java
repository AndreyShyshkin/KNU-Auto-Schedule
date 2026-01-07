package ua.kiev.univ.schedule.service.core.property;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.util.ExecutionException;

import java.beans.PropertyDescriptor;

public class BeanProperty<T, E extends Entity> extends Property<T, E> {

    public BeanProperty(String name, Class<T> type) {
        super(name, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getValue(E entity) {
        try {
            // Викликаємо геттер (наприклад, getName()) через рефлексію
            return (T) new PropertyDescriptor(name, entity.getClass()).getReadMethod().invoke(entity);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public void setValue(T value, E entity) {
        try {
            // Викликаємо сеттер (наприклад, setName(...)) через рефлексію
            new PropertyDescriptor(name, entity.getClass()).getWriteMethod().invoke(entity, value);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
}