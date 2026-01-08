package ua.kiev.univ.schedule.dto;

public class GroupDto {
    private Long id;
    private String name;
    private Long departmentId; // Speciality ID
    private String departmentName;
    private int year;
    private int size;

    public GroupDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}