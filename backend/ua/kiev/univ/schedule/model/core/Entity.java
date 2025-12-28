package ua.kiev.univ.schedule.model.core;

import ua.kiev.univ.schedule.service.core.DataService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class Entity implements RWable {

    protected <E extends Entity> E readEntity(Class<E> entityClass, DataInputStream is) throws IOException {
        // DataService буде створено пізніше, зараз IDE може лаятися на цей рядок
        List<E> entities = DataService.getEntities(entityClass);
        int index = is.readInt();
        return (index >= 0) ? entities.get(index) : null;
    }

    protected <E extends Entity> void writeEntity(E entity, Class<E> entityClass, DataOutputStream os) throws IOException {
        List<E> entities = DataService.getEntities(entityClass);
        int index = entities.indexOf(entity);
        os.writeInt(index);
    }

    protected <E extends Entity> List<E> readList(Class<E> entityClass, DataInputStream is) throws IOException {
        List<E> entities = DataService.getEntities(entityClass);
        List<E> list = new LinkedList<>(); // Diamond operator (Java 7+)
        int count = is.readInt();
        while (count-- > 0) {
            list.add(entities.get(is.readInt()));
        }
        return list;
    }

    protected <E extends Entity> void writeList(List<E> list, Class<E> entityClass, DataOutputStream os) throws IOException {
        List<E> entities = DataService.getEntities(entityClass);
        os.writeInt(list.size());
        for (E entity : list) {
            os.writeInt(entities.indexOf(entity));
        }
    }

    @Override
    public void read(DataInputStream is) throws IOException {
        // Заглушка, реалізується в дочірніх класах
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        // Заглушка, реалізується в дочірніх класах
    }

    public void onRemove() {
        // Метод для очистки ресурсів при видаленні
    }
}