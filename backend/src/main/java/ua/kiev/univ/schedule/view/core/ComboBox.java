package ua.kiev.univ.schedule.view.core;

import javax.swing.JComboBox;
import java.awt.Dimension;

public abstract class ComboBox<E> extends JComboBox<E> implements Refreshable {

    protected ComboBox() {
        this.setPreferredSize(new Dimension(100, 20));
    }
}