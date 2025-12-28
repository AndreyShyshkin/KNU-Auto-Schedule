package ua.kiev.univ.schedule.service.lesson;

import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.service.core.property.BeanProperty;
import ua.kiev.univ.schedule.service.core.property.Property;

import javax.swing.table.AbstractTableModel;

public class LessonService extends MemberedEntityService<Lesson> {

    protected Property<Earmark, Lesson> earmarkProperty = new BeanProperty<>("earmark", Earmark.class);
    protected Property<Integer, Lesson> countProperty = new BeanProperty<>("count", Integer.class);

    public LessonService() {
        super(Lesson.class);
        properties.add(earmarkProperty);
        properties.add(countProperty);
    }

    @Override
    protected boolean isCellErroneous(Lesson lesson, Property<?, Lesson> property) {
        if (property == earmarkProperty) {
            Earmark earmark = earmarkProperty.getValue(lesson);
            if (earmark == null) {
                return true;
            }
            int size = 0;
            for (Group group : lesson.getGroups()) {
                size += group.getSize();
            }

            int teacherCount = lesson.getTeachers().size();

            int multiplier = (teacherCount > 0) ? teacherCount : 1;

            return (size > earmark.getSize() * multiplier);
        }
        return (property != countProperty) && super.isCellErroneous(lesson, property);
    }

    @Override
    public void onMembersChanged() {
        // Оновлюємо комірку Earmark, щоб показати помилку (червоним), якщо група стала завеликою
        if (selectedRow != null && tablePane != null) {
            int row = rows.indexOf(selectedRow);
            int column = properties.indexOf(earmarkProperty);
            if (row >= 0 && column >= 0) {
                ((AbstractTableModel) tablePane.table.getModel()).fireTableCellUpdated(row, column);
            }
        }
    }
}