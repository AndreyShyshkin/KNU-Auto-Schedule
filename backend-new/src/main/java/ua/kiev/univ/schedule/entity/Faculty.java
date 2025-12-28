package ua.kiev.univ.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL)
    private List<Chair> chairs;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL)
    private List<Speciality> specialities;
}
