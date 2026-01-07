package ua.kiev.univ.schedule.service.date;

import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.service.core.property.BeanProperty;
import ua.kiev.univ.schedule.service.core.property.Property;
import ua.kiev.univ.schedule.service.core.table.EditableTableService;
import ua.kiev.univ.schedule.util.StringUtils;

public class TimeService extends EditableTableService<Time> {

    protected DayService dayService;

    // Віртуальна властивість: чи належить цей час вибраному дню
    protected Property<Boolean, Time> belongProperty = new Property<Boolean, Time>("belong", Boolean.class) {

        @Override
        public Boolean getValue(Time time) {
            Day day = dayService.selectedRow;
            return (day != null) && day.getTimes().contains(time);
        }

        @Override
        public void setValue(Boolean value, Time time) {
            Day day = dayService.selectedRow;
            if (day != null) {
                if (Boolean.TRUE.equals(value)) {
                    day.getTimes().add(time);
                } else {
                    day.getTimes().remove(time);
                }
            }
        }
    };

    protected Property<String, Time> startProperty = new BeanProperty<>("start", String.class);
    protected Property<String, Time> endProperty = new BeanProperty<>("end", String.class);

    public TimeService(DayService dayService) {
        super(Time.class);
        this.dayService = dayService;
        properties.add(belongProperty);
        properties.add(startProperty);
        properties.add(endProperty);
    }

    @Override
    public boolean isCellEditable(Time entity, Property<?, Time> property) {
        // Редагувати "галочку" приналежності можна тільки якщо вибрано день
        return super.isCellEditable(entity, property) && ((property != belongProperty) || (dayService.selectedRow != null));
    }

    @Override
    public boolean isCellErroneous(Time entity, Property<?, Time> property) {
        if (property == startProperty) {
            return StringUtils.isBlank(startProperty.getValue(entity));
        }
        if (property == endProperty) {
            return StringUtils.isBlank(endProperty.getValue(entity));
        }
        return (property != belongProperty) && super.isCellErroneous(entity, property);
    }
}