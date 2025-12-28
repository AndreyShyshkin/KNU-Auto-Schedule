package ua.kiev.univ.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Speciality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
}
