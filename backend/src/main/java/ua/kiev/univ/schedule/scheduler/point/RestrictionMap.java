package ua.kiev.univ.schedule.scheduler.point;

import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Restrictor;
import ua.kiev.univ.schedule.model.member.Teacher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictionMap {

    private final int count;
    private final List<Date> dates;
    // Використовуємо raw type Restrictor, оскільки в оригіналі Restrictor був generic,
    // але тут ключем виступає будь-який Restrictor. Можна уточнити як Restrictor<?>.
    private final Map<Restrictor<?>, int[]> map = new HashMap<>();

    public RestrictionMap(List<Date> dates, List<Group> groups, List<Teacher> teachers) {
        this.dates = dates;
        this.count = dates.size();
        putRestrictions(groups);
        putRestrictions(teachers);
    }

    private int[] getRestriction(Restrictor<?> entity) {
        int[] restriction = new int[count];
        int index = 0;
        for (Date date : dates) {
            restriction[index++] = entity.getRestriction().getGrade(date).value;
        }
        return restriction;
    }

    private void putRestrictions(List<? extends Restrictor<?>> entities) {
        for (Restrictor<?> entity : entities) {
            map.put(entity, getRestriction(entity));
        }
    }

    public void addRestrictions(List<? extends Restrictor<?>> entities, Point point) {
        for (Restrictor<?> entity : entities) {
            int[] restriction = map.get(entity);
            if (restriction != null) {
                for (int i = 0; i < count; i++) {
                    point.restriction[i] += restriction[i];
                }
            }
        }
    }
}