package ua.kiev.univ.schedule.dto;

import java.util.List;

public class LessonDto {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private Long earmarkId;
    private String earmarkName;
    private int count;
    private List<Long> teacherIds;
    private List<String> teacherNames;
    private List<Long> groupIds;
    private List<String> groupNames;

    public LessonDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public Long getEarmarkId() { return earmarkId; }
    public void setEarmarkId(Long earmarkId) { this.earmarkId = earmarkId; }
    public String getEarmarkName() { return earmarkName; }
    public void setEarmarkName(String earmarkName) { this.earmarkName = earmarkName; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public List<Long> getTeacherIds() { return teacherIds; }
    public void setTeacherIds(List<Long> teacherIds) { this.teacherIds = teacherIds; }
    public List<String> getTeacherNames() { return teacherNames; }
    public void setTeacherNames(List<String> teacherNames) { this.teacherNames = teacherNames; }
    public List<Long> getGroupIds() { return groupIds; }
    public void setGroupIds(List<Long> groupIds) { this.groupIds = groupIds; }
    public List<String> getGroupNames() { return groupNames; }
    public void setGroupNames(List<String> groupNames) { this.groupNames = groupNames; }
}