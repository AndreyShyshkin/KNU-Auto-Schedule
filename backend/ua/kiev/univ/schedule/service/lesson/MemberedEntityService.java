package ua.kiev.univ.schedule.service.lesson;

import ua.kiev.univ.schedule.model.department.Chair;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.model.department.Speciality;
import ua.kiev.univ.schedule.model.lesson.MemberedEntity;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.util.EntityFilter;
import ua.kiev.univ.schedule.view.input.lesson.member.MemberPane;

import java.util.LinkedList;
import java.util.List;

public class MemberedEntityService<E extends MemberedEntity> extends SubjectedEntityService<E> {

    protected MemberPane<Chair, Teacher> teacherPane;
    protected MemberPane<Speciality, Group> groupPane;
    protected List<Faculty> faculties = new LinkedList<>();
    protected Faculty selectedFaculty;

    public MemberedEntityService(Class<E> entityClass) {
        super(entityClass);
    }

    @Override
    public void selectRow(int index) {
        super.selectRow(index);
        refreshMembers();
    }

    public void refreshFaculties() {
        faculties = EntityFilter.getActiveEntities(Faculty.class);
        if (!faculties.contains(selectedFaculty)) {
            setSelectedFaculty(null);
        }
    }

    public List<Faculty> getFaculties() {
        return faculties;
    }

    public Faculty getSelectedFaculty() {
        return selectedFaculty;
    }

    public void setSelectedFaculty(Faculty faculty) {
        selectedFaculty = faculty;
        if (teacherPane != null) teacherPane.refresh();
        if (groupPane != null) groupPane.refresh();
    }

    private void refreshMembers() {
        if (teacherPane != null && teacherPane.table != null) teacherPane.table.refresh();
        if (groupPane != null && groupPane.table != null) groupPane.table.refresh();
    }

    public void onMembersChanged() {}

    public void setTeacherPane(MemberPane<Chair, Teacher> teacherPane) {
        this.teacherPane = teacherPane;
    }

    public void setGroupPane(MemberPane<Speciality, Group> groupPane) {
        this.groupPane = groupPane;
    }
}