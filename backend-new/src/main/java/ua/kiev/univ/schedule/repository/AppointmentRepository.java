package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kiev.univ.schedule.entity.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {}
