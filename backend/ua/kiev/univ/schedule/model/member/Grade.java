package ua.kiev.univ.schedule.model.member;

import java.awt.Color;

public enum Grade {

    NONE(0, new Color(255, 255, 255)),
    BAD(-1, new Color(255, 240, 240)),
    TERRIBLE(-10, new Color(255, 225, 225));

    public int value;
    public final Color color;

    Grade(int value, Color color) {
        this.value = value;
        this.color = color;
    }
}