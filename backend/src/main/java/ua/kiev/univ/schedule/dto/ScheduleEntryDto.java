package ua.kiev.univ.schedule.dto;

public class ScheduleEntryDto {
    private String dayName;
    private String timeStart;
    private String timeEnd;
    private String subjectName;
    private String type; // Lecture/Practice (from Earmark)
    private String auditoriumName;
    private String additionalInfo; // Group name (for teacher schedule) or Teacher name (for group schedule)

    public ScheduleEntryDto() {}

    public ScheduleEntryDto(String dayName, String timeStart, String timeEnd, String subjectName, String type, String auditoriumName, String additionalInfo) {
        this.dayName = dayName;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.subjectName = subjectName;
        this.type = type;
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
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAuditoriumName() { return auditoriumName; }
    public void setAuditoriumName(String auditoriumName) { this.auditoriumName = auditoriumName; }
    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
}