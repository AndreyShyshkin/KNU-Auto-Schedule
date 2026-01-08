package ua.kiev.univ.schedule.dto;

import java.util.List;

public class DayDto {
    private Long id;
    private String name;
    private List<TimeDto> times;

    public DayDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<TimeDto> getTimes() { return times; }
    public void setTimes(List<TimeDto> times) { this.times = times; }
}