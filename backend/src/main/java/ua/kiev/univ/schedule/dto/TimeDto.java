package ua.kiev.univ.schedule.dto;

public class TimeDto {
    private Long id;
    private String start;
    private String end;
    private Long buildingId;
    private String buildingName;

    public TimeDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }
    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }
    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
}