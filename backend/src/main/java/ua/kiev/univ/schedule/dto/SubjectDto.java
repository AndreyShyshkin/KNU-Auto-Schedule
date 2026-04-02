package ua.kiev.univ.schedule.dto;

public class SubjectDto {
    private Long id;
    private String name;
    private Long facultyId;
    private String facultyName;

    public SubjectDto() {}

    public SubjectDto(Long id, String name, Long facultyId, String facultyName) {
        this.id = id;
        this.name = name;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getFacultyId() { return facultyId; }
    public void setFacultyId(Long facultyId) { this.facultyId = facultyId; }
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
}
