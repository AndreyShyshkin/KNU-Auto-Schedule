package ua.kiev.univ.schedule.view.core;

import javax.swing.JTabbedPane;

public abstract class TabbedPane extends JTabbedPane implements Refreshable, Internationalized {

    public TabbedPane() {
        this.setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
    }
}