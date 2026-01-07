package ua.kiev.univ.schedule.service.core;

import ua.kiev.univ.schedule.model.core.Entity;

import java.util.LinkedList;

public class EntityList<E extends Entity> extends LinkedList<E> {

    public final Class<E> entityClass;

    public EntityList(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public E add() {
        try {
            // newInstance() є deprecated починаючи з Java 9, тому використовуємо getDeclaredConstructor()
            E entity = entityClass.getDeclaredConstructor().newInstance();
            this.add(entity);
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}