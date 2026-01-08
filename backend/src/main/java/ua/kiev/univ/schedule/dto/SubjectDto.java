package ua.kiev.univ.schedule.dto;

public class SubjectDto {
    private Long id;
    private String name;

    public SubjectDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}