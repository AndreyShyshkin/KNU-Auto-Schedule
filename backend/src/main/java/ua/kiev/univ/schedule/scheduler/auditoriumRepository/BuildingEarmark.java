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
        
        Long b1 = (building != null) ? building.getId() : null;
        Long b2 = (that.building != null) ? that.building.getId() : null;
        if (!Objects.equals(b1, b2)) return false;
        
        Long e1 = (earmark != null) ? earmark.getId() : null;
        Long e2 = (that.earmark != null) ? that.earmark.getId() : null;
        return Objects.equals(e1, e2);
    }

    @Override
    public int hashCode() {
        Long bId = (building != null) ? building.getId() : null;
        Long eId = (earmark != null) ? earmark.getId() : null;
        return Objects.hash(bId, eId);
    }
}
