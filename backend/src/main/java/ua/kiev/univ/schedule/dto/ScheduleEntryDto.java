package ua.kiev.univ.schedule.dto;

public class ScheduleEntryDto {
    private String dayName;
    private String timeStart;
    private String timeEnd;
    private String subjectName;
    private String lessonTypeName;
    private String earmarkName;
    private String buildingName;
    private String auditoriumName;
    private String additionalInfo;
    private String actualDate;

    public ScheduleEntryDto() {}

    public ScheduleEntryDto(String dayName, String timeStart, String timeEnd, String subjectName, String lessonTypeName, String earmarkName, String buildingName, String auditoriumName, String additionalInfo) {
        this.dayName = dayName;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.subjectName = subjectName;
        this.lessonTypeName = lessonTypeName;
        this.earmarkName = earmarkName;
        this.buildingName = buildingName;
        this.auditoriumName = auditoriumName;
        this.additionalInfo = additionalInfo;
    }

    public String getDayName() { return dayName; }
    public void setDayName(String dayName) { this.dayName = dayName; }
    public String getTimeStart() { return timeStart; }
    public void setTimeStart(String timeStart) { this.timeStart = timeStart; }
    public String getTimeEnd() { return timeEnd; }
    public void setTimeEnd(String timeEnd) { this.timeEnd = timeEnd; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getLessonTypeName() { return lessonTypeName; }
    public void setLessonTypeName(String lessonTypeName) { this.lessonTypeName = lessonTypeName; }
    public String getEarmarkName() { return earmarkName; }
    public void setEarmarkName(String earmarkName) { this.earmarkName = earmarkName; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getAuditoriumName() { return auditoriumName; }
    public void setAuditoriumName(String auditoriumName) { this.auditoriumName = auditoriumName; }
    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
    public String getActualDate() { return actualDate; }
    public void setActualDate(String actualDate) { this.actualDate = actualDate; }
}
