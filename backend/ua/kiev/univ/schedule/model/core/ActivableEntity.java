package ua.kiev.univ.schedule.model.core;

import java.util.List;

public abstract class ActivableEntity extends Entity {

    public boolean isActive() {
        return true;
    }

    protected static <E extends ActivableEntity> boolean isActive(List<E> entities) {
        for (E entity : entities) {
            if (entity.isActive()) {
                return true;
            }
        }
        return false;
    }
}