package ua.kiev.univ.schedule.dto;

public class AuditoriumDto {
    private Long id;
    private String name;
    private Long earmarkId;
    private String earmarkName;
    private Long buildingId;
    private String buildingName;

    public AuditoriumDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getEarmarkId() { return earmarkId; }
    public void setEarmarkId(Long earmarkId) { this.earmarkId = earmarkId; }
    public String getEarmarkName() { return earmarkName; }
    public void setEarmarkName(String earmarkName) { this.earmarkName = earmarkName; }
    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
}