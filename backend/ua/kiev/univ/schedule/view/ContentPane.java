package ua.kiev.univ.schedule.view;

import ua.kiev.univ.schedule.view.build.BuildPane;
import ua.kiev.univ.schedule.view.core.Internationalized;
import ua.kiev.univ.schedule.view.core.Refreshable;
import ua.kiev.univ.schedule.view.core.SplitPane;
import ua.kiev.univ.schedule.view.input.InputPane;

public class ContentPane extends SplitPane implements Refreshable, Internationalized {

    public final InputPane inputPane = new InputPane("input");
    public final BuildPane buildPane = new BuildPane("build");

    public ContentPane() {
        super(HORIZONTAL_SPLIT);
        this.setLeftComponent(inputPane);
        this.setRightComponent(buildPane);
    }

    @Override
    public void refresh() {
        inputPane.refresh();
        buildPane.refresh();
    }

    @Override
    public void loadLanguage() {
        inputPane.loadLanguage();
        buildPane.loadLanguage();
    }
}