package ua.kiev.univ.schedule.model.department;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.service.core.DataService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class Speciality extends Department {

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> groups = new ArrayList<>();

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public void onRemove() {
        super.onRemove();
        Iterator<Group> iterator = DataService.getEntities(Group.class).iterator();
        while (iterator.hasNext()) {
            Group group = iterator.next();

            if (group.getDepartment() == this) {
                iterator.remove();
                group.onRemove();
            }
        }
    }
}