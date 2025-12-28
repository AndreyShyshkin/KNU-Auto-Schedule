package ua.kiev.univ.schedule.view.core;

import javax.swing.JSplitPane;

public abstract class SplitPane extends JSplitPane implements Refreshable, Internationalized {

    public SplitPane(int orientation) {
        super(orientation);
        this.setDividerSize(5);
        this.setResizeWeight(0.5);
    }
}