package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.appointment.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}