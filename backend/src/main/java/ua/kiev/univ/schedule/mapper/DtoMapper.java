package ua.kiev.univ.schedule.mapper;

import ua.kiev.univ.schedule.dto.*;
import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.subject.Subject;

import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;

import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;
import java.util.List;
import java.util.stream.Collectors;

import ua.kiev.univ.schedule.model.lesson.Lesson;

public class DtoMapper {

    public static Time toEntity(TimeDto dto) {
        Time time = new Time();
        time.setId(dto.getId());
        time.setStart(dto.getStart());
        time.setEnd(dto.getEnd());
        return time;
    }

    public static Day toEntity(DayDto dto, List<Time> times) {
        Day day = new Day();
        day.setId(dto.getId());
        day.setName(dto.getName());
        if (times != null) {
            day.setTimes(times);
        }
        return day;
    }

    public static LessonDto toDto(Lesson lesson) {
        if (lesson == null) return null;
        LessonDto dto = new LessonDto();
        dto.setId(lesson.getId());
        dto.setCount(lesson.getCount());
        if (lesson.getSubject() != null) {
            dto.setSubjectId(lesson.getSubject().getId());
            dto.setSubjectName(lesson.getSubject().getName());
        }
        if (lesson.getEarmark() != null) {
            dto.setEarmarkId(lesson.getEarmark().getId());
            dto.setEarmarkName(lesson.getEarmark().getName());
        }
        dto.setTeacherIds(lesson.getTeachers().stream().map(Teacher::getId).collect(Collectors.toList()));
        dto.setTeacherNames(lesson.getTeachers().stream().map(Teacher::getName).collect(Collectors.toList()));
        dto.setGroupIds(lesson.getGroups().stream().map(Group::getId).collect(Collectors.toList()));
        dto.setGroupNames(lesson.getGroups().stream().map(Group::getName).collect(Collectors.toList()));
        return dto;
    }

    public static TimeDto toDto(Time time) {
        if (time == null) return null;
        TimeDto dto = new TimeDto();
        dto.setId(time.getId());
        dto.setStart(time.getStart());
        dto.setEnd(time.getEnd());
        return dto;
    }

    public static DayDto toDto(Day day) {
        if (day == null) return null;
        DayDto dto = new DayDto();
        dto.setId(day.getId());
        dto.setName(day.getName());
        if (day.getTimes() != null) {
            dto.setTimes(day.getTimes().stream()
                    .map(DtoMapper::toDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public static EarmarkDto toDto(Earmark earmark) {
        if (earmark == null) return null;
        EarmarkDto dto = new EarmarkDto();
        dto.setId(earmark.getId());
        dto.setName(earmark.getName());
        dto.setSize(earmark.getSize());
        return dto;
    }

    public static AuditoriumDto toDto(Auditorium auditorium) {
        if (auditorium == null) return null;
        AuditoriumDto dto = new AuditoriumDto();
        dto.setId(auditorium.getId());
        dto.setName(auditorium.getName());
        if (auditorium.getEarmark() != null) {
            dto.setEarmarkId(auditorium.getEarmark().getId());
            dto.setEarmarkName(auditorium.getEarmark().getName());
        }
        return dto;
    }

    public static Faculty toEntity(FacultyDto dto) {
        Faculty faculty = new Faculty();
        faculty.setId(dto.getId());
        faculty.setName(dto.getName());
        faculty.setDescription(dto.getDescription());
        return faculty;
    }

    public static Chair toEntity(ChairDto dto, Faculty faculty) {
        Chair chair = new Chair();
        chair.setId(dto.getId());
        chair.setName(dto.getName());
        chair.setDescription(dto.getDescription());
        chair.setFaculty(faculty);
        return chair;
    }

    public static Speciality toEntity(SpecialityDto dto, Faculty faculty) {
        Speciality speciality = new Speciality();
        speciality.setId(dto.getId());
        speciality.setName(dto.getName());
        speciality.setDescription(dto.getDescription());
        speciality.setFaculty(faculty);
        return speciality;
    }

    public static Subject toEntity(SubjectDto dto) {
        Subject subject = new Subject();
        subject.setId(dto.getId());
        subject.setName(dto.getName());
        return subject;
    }

    public static Teacher toEntity(TeacherDto dto, Chair department) {
        Teacher teacher = new Teacher();
        teacher.setId(dto.getId());
        teacher.setName(dto.getName());
        teacher.setDepartment(department);
        return teacher;
    }

    public static Group toEntity(GroupDto dto, Speciality department) {
        Group group = new Group();
        group.setId(dto.getId());
        group.setName(dto.getName());
        group.setDepartment(department);
        group.setSize(dto.getSize());
        // Year mapping (1-based index to 0-based enum ordinal)
        if (dto.getYear() > 0 && dto.getYear() <= ua.kiev.univ.schedule.model.member.Year.values().length) {
            group.setYear(ua.kiev.univ.schedule.model.member.Year.values()[dto.getYear() - 1]);
        }
        return group;
    }

    public static Earmark toEntity(EarmarkDto dto) {
        Earmark earmark = new Earmark();
        earmark.setId(dto.getId());
        earmark.setName(dto.getName());
        earmark.setSize(dto.getSize());
        return earmark;
    }

    public static Auditorium toEntity(AuditoriumDto dto, Earmark earmark) {
        Auditorium auditorium = new Auditorium();
        auditorium.setId(dto.getId());
        auditorium.setName(dto.getName());
        auditorium.setEarmark(earmark);
        return auditorium;
    }

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

    public static SpecialityDto toDto(Speciality speciality) {
        if (speciality == null) return null;
        SpecialityDto dto = new SpecialityDto();
        dto.setId(speciality.getId());
        dto.setName(speciality.getName());
        dto.setDescription(speciality.getDescription());
        if (speciality.getFaculty() != null) {
            dto.setFacultyId(speciality.getFaculty().getId());
            dto.setFacultyName(speciality.getFaculty().getName());
        }
        return dto;
    }
}