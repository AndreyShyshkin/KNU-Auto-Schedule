package ua.kiev.univ.schedule.dto;

public class TimeDto {
    private Long id;
    private String start;
    private String end;

    public TimeDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }
    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }
}