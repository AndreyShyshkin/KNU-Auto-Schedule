package ua.kiev.univ.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "student_group") // Group is a reserved keyword in SQL
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer courseYear; // 1, 2, 3, 4, etc.

    private Integer size;

    @ManyToOne
    @JoinColumn(name = "speciality_id")
    private Speciality speciality;
}
