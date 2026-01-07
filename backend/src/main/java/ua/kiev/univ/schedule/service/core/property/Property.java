package ua.kiev.univ.schedule.service.core.property;

import ua.kiev.univ.schedule.model.core.Entity;

public abstract class Property<T, E extends Entity> {

    protected String name;
    protected Class<T> type;

    public Property(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public abstract T getValue(E entity);

    public abstract void setValue(T value, E entity);

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }
}