package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import ua.kiev.univ.schedule.service.date.DayService;
import ua.kiev.univ.schedule.service.date.TimeService;
import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.SplitPane;
import ua.kiev.univ.schedule.view.input.table.EntityTablePane;

public class DatePane extends SplitPane {

    private final String key;
    private final InputPane inputPane;
    public final EntityTablePane<Time> timePane;
    public final EntityTablePane<Day> dayPane;

    public DatePane(InputPane inputPane, String key) {
        super(HORIZONTAL_SPLIT);
        this.inputPane = inputPane;
        this.key = key;

        DayService dayService = new DayService();
        TimeService timeService = new TimeService(dayService);

        timePane = new EntityTablePane<>(timeService, key + ".time");
        dayPane = new EntityTablePane<>(dayService, key + ".day");

        dayService.setTimePane(timePane);

        this.setLeftComponent(timePane);
        this.setRightComponent(dayPane);
    }

    @Override
    public void refresh() {
        timePane.refresh();
        dayPane.refresh();
    }

    @Override
    public void loadLanguage() {
        inputPane.setTitleAt(inputPane.indexOfComponent(this), Language.getText(key));
        timePane.loadLanguage();
        dayPane.loadLanguage();
    }
}