package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.service.placement.AuditoriumService;
import ua.kiev.univ.schedule.service.placement.EarmarkService;
import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.SplitPane;
import ua.kiev.univ.schedule.view.input.table.EntityTablePane;

public class PlacementPane extends SplitPane {

    private final String key;
    private final InputPane inputPane;
    public final EntityTablePane<Earmark> earmarkPane;
    public final EntityTablePane<Auditorium> auditoriumPane;

    public PlacementPane(InputPane inputPane, String key) {
        super(HORIZONTAL_SPLIT);
        this.inputPane = inputPane;
        this.key = key;

        EarmarkService earmarkService = new EarmarkService();
        AuditoriumService auditoriumService = new AuditoriumService(earmarkService);

        earmarkPane = new EntityTablePane<>(earmarkService, key + ".earmark");
        auditoriumPane = new EntityTablePane<>(auditoriumService, key + ".auditorium");

        earmarkService.setAuditoriumPane(auditoriumPane);

        this.setLeftComponent(earmarkPane);
        this.setRightComponent(auditoriumPane);
        this.setDividerSize(5);
        this.setResizeWeight(0.5);
    }

    @Override
    public void refresh() {
        earmarkPane.refresh();
        auditoriumPane.refresh();
    }

    @Override
    public void loadLanguage() {
        inputPane.setTitleAt(inputPane.indexOfComponent(this), Language.getText(key));
        earmarkPane.loadLanguage();
        auditoriumPane.loadLanguage();
    }
}