package ua.kiev.univ.schedule.dto;

public class EarmarkDto {
    private Long id;
    private String name;
    private int size;

    public EarmarkDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}