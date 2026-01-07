package ua.kiev.univ.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kiev.univ.schedule.model.member.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}