package ua.kiev.univ.schedule.dto;

public class AuditoriumDto {
    private Long id;
    private String name;
    private Long earmarkId;
    private String earmarkName;

    public AuditoriumDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getEarmarkId() { return earmarkId; }
    public void setEarmarkId(Long earmarkId) { this.earmarkId = earmarkId; }
    public String getEarmarkName() { return earmarkName; }
    public void setEarmarkName(String earmarkName) { this.earmarkName = earmarkName; }
}