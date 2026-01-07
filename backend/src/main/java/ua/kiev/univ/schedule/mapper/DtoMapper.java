package ua.kiev.univ.schedule.mapper;

import ua.kiev.univ.schedule.dto.*;
import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.subject.Subject;

public class DtoMapper {

    public static FacultyDto toDto(Faculty faculty) {
        if (faculty == null) return null;
        return new FacultyDto(faculty.getId(), faculty.getName(), faculty.getDescription());
    }

    public static ChairDto toDto(Chair chair) {
        if (chair == null) return null;
        ChairDto dto = new ChairDto();
        dto.setId(chair.getId());
        dto.setName(chair.getName());
        dto.setDescription(chair.getDescription());
        if (chair.getFaculty() != null) {
            dto.setFacultyId(chair.getFaculty().getId());
            dto.setFacultyName(chair.getFaculty().getName());
        }
        return dto;
    }
    
    public static SubjectDto toDto(Subject subject) {
        if (subject == null) return null;
        SubjectDto dto = new SubjectDto();
        dto.setId(subject.getId());
        dto.setName(subject.getName());
        return dto;
    }

    public static TeacherDto toDto(Teacher teacher) {
        if (teacher == null) return null;
        TeacherDto dto = new TeacherDto();
        dto.setId(teacher.getId());
        dto.setName(teacher.getName());
        if (teacher.getDepartment() != null) {
            dto.setDepartmentId(teacher.getDepartment().getId());
            dto.setDepartmentName(teacher.getDepartment().getName());
        }
        return dto;
    }

    public static GroupDto toDto(Group group) {
        if (group == null) return null;
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        if (group.getYear() != null) {
            dto.setYear(group.getYear().ordinal() + 1); // 1-based year
        }
        dto.setSize(group.getSize());
        if (group.getDepartment() != null) {
            dto.setDepartmentId(group.getDepartment().getId());
            dto.setDepartmentName(group.getDepartment().getName());
        }
        return dto;
    }
}