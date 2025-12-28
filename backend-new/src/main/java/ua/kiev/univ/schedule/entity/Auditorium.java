package ua.kiev.univ.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Auditorium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    // Example: Lecture, Lab, Computer Class (Mapped from Earmark)
    private String type; 
    
    private Integer capacity;
}
