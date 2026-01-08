package ua.kiev.univ.schedule.model.lesson;

import jakarta.persistence.*;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Member;
import ua.kiev.univ.schedule.model.member.Teacher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@MappedSuperclass
public abstract class MemberedEntity extends SubjectedEntity {

    @ManyToMany
    @JoinTable(
        name = "lesson_teachers",
        joinColumns = @JoinColumn(name = "lesson_id"),
        inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private List<Teacher> teachers = new LinkedList<>();

    @ManyToMany
    @JoinTable(
        name = "lesson_groups",
        joinColumns = @JoinColumn(name = "lesson_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<Group> groups = new LinkedList<>();

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        teachers = readList(Teacher.class, is);
        groups = readList(Group.class, is);
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        super.write(os);
        writeList(teachers, Teacher.class, os);
        writeList(groups, Group.class, os);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && isActive(teachers) && isActive(groups);
    }

    @SuppressWarnings("unchecked")
    public <E extends Member> List<E> getMembers(Class<E> entityClass) {
        if (Teacher.class.equals(entityClass)) {
            return (List<E>) teachers;
        }
        if (Group.class.equals(entityClass)) {
            return (List<E>) groups;
        }
        return null;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}