package ua.kiev.univ.schedule.model.appointment;

public enum Part {

    FIRST(true, false),
    SECOND(false, true),
    BOTH(true, true);

    public final boolean isFirst;
    public final boolean isSecond;

    Part(boolean isFirst, boolean isSecond) {
        this.isFirst = isFirst;
        this.isSecond = isSecond;
    }
}