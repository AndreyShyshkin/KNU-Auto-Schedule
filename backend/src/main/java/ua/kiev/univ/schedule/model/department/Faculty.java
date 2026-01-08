package ua.kiev.univ.schedule.model.department;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import ua.kiev.univ.schedule.service.core.DataService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class Faculty extends DescriptionedEntity {

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chair> chairs = new ArrayList<>();

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Speciality> specialities = new ArrayList<>();

    public List<Chair> getChairs() {
        return chairs;
    }

    public void setChairs(List<Chair> chairs) {
        this.chairs = chairs;
    }

    public List<Speciality> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(List<Speciality> specialities) {
        this.specialities = specialities;
    }

    @Override
    public void onRemove() {
        super.onRemove();
        Iterator<Speciality> iterator = DataService.getEntities(Speciality.class).iterator();
        while (iterator.hasNext()) {
            Speciality speciality = iterator.next();
            if (speciality.getFaculty() == this) {
                iterator.remove();
                speciality.onRemove();
            }
        }
    }
}