package ua.kiev.univ.schedule.scheduler.auditoriumRepository;

import ua.kiev.univ.schedule.model.placement.Building;
import ua.kiev.univ.schedule.model.placement.Earmark;

import java.util.Objects;

public class BuildingEarmark {
    private final Building building;
    private final Earmark earmark;

    public BuildingEarmark(Building building, Earmark earmark) {
        this.building = building;
        this.earmark = earmark;
    }

    public Building getBuilding() {
        return building;
    }

    public Earmark getEarmark() {
        return earmark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingEarmark that = (BuildingEarmark) o;
        return Objects.equals(building, that.building) && Objects.equals(earmark, that.earmark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(building, earmark);
    }
}
