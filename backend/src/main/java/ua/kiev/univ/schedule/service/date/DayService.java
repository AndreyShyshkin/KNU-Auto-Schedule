package ua.kiev.univ.schedule.service.date;

import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.service.core.NamedEntityService;
import ua.kiev.univ.schedule.view.input.table.TablePane;

public class DayService extends NamedEntityService<Day> {

    private TablePane<Time> timePane;

    public DayService() {
        super(Day.class);
    }

    @Override
    public void selectRow(int index) {
        super.selectRow(index);
        if (timePane != null) {
            timePane.refresh();
        }
    }

    public void setTimePane(TablePane<Time> timePane) {
        this.timePane = timePane;
    }
}