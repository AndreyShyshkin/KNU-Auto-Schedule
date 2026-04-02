package ua.kiev.univ.schedule.dto;

import java.util.List;

public class LessonDto {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private Long earmarkId;
    private String earmarkName;
    private Long auditoriumId;
    private String auditoriumName;
    private Long buildingId;
    private String buildingName;
    private List<Long> teacherIds;
    private List<String> teacherNames;
    private List<Long> groupIds;
    private List<String> groupNames;
    private Integer count;

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
    public Long getAuditoriumId() { return auditoriumId; }
    public void setAuditoriumId(Long auditoriumId) { this.auditoriumId = auditoriumId; }
    public String getAuditoriumName() { return auditoriumName; }
    public void setAuditoriumName(String auditoriumName) { this.auditoriumName = auditoriumName; }
    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public List<Long> getTeacherIds() { return teacherIds; }
    public void setTeacherIds(List<Long> teacherIds) { this.teacherIds = teacherIds; }
    public List<String> getTeacherNames() { return teacherNames; }
    public void setTeacherNames(List<String> teacherNames) { this.teacherNames = teacherNames; }
    public List<Long> getGroupIds() { return groupIds; }
    public void setGroupIds(List<Long> groupIds) { this.groupIds = groupIds; }
    public List<String> getGroupNames() { return groupNames; }
    public void setGroupNames(List<String> groupNames) { this.groupNames = groupNames; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
