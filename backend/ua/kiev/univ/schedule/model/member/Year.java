package ua.kiev.univ.schedule.model.member;

import ua.kiev.univ.schedule.util.Language;

public enum Year {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    FIFTH,
    SIXTH;

    @Override
    public String toString() {
        return Language.getText("year." + this.name());
    }
}