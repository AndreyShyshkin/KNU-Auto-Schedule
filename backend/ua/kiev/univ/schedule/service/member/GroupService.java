package ua.kiev.univ.schedule.service.member;

import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Year;
import ua.kiev.univ.schedule.service.core.property.BeanProperty;
import ua.kiev.univ.schedule.service.core.property.Property;

public class GroupService extends RestrictorService<Speciality, Group> {

    protected Property<Year, Group> yearProperty = new BeanProperty<>("year", Year.class);
    protected Property<Integer, Group> sizeProperty = new BeanProperty<>("size", Integer.class);

    public GroupService() {
        super(Group.class, Speciality.class);
        properties.add(yearProperty);
        properties.add(sizeProperty);
    }

    @Override
    public boolean isCellErroneous(Group group, Property<?, Group> property) {
        return (property != yearProperty) && (property != sizeProperty) && super.isCellErroneous(group, property);
    }
}