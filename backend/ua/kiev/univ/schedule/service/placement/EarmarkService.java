package ua.kiev.univ.schedule.service.placement;

import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.service.core.NamedEntityService;
import ua.kiev.univ.schedule.service.core.property.BeanProperty;
import ua.kiev.univ.schedule.service.core.property.Property;
import ua.kiev.univ.schedule.view.input.table.TablePane;

public class EarmarkService extends NamedEntityService<Earmark> {

    private TablePane<Auditorium> auditoriumPane;
    protected Property<Integer, Earmark> sizeProperty = new BeanProperty<>("size", Integer.class);

    public EarmarkService() {
        super(Earmark.class);
        properties.add(sizeProperty);
    }

    @Override
    public void selectRow(int index) {
        super.selectRow(index);
        if (auditoriumPane != null) {
            auditoriumPane.refresh();
        }
    }

    @Override
    public boolean isCellErroneous(Earmark earmark, Property<?, Earmark> property) {
        return (property != sizeProperty) && super.isCellErroneous(earmark, property);
    }

    public void setAuditoriumPane(TablePane<Auditorium> auditoriumPane) {
        this.auditoriumPane = auditoriumPane;
    }
}