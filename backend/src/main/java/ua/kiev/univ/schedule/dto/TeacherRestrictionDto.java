package ua.kiev.univ.schedule.dto;

import java.util.List;

public class TeacherRestrictionDto {
    private Long teacherId;
    private List<RestrictedSlotDto> restrictedSlots;

    public TeacherRestrictionDto() {}

    public TeacherRestrictionDto(Long teacherId, List<RestrictedSlotDto> restrictedSlots) {
        this.teacherId = teacherId;
        this.restrictedSlots = restrictedSlots;
    }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public List<RestrictedSlotDto> getRestrictedSlots() { return restrictedSlots; }
    public void setRestrictedSlots(List<RestrictedSlotDto> restrictedSlots) { this.restrictedSlots = restrictedSlots; }

    public static class RestrictedSlotDto {
        private Long dayId;
        private Long timeId;

        public RestrictedSlotDto() {}

        public RestrictedSlotDto(Long dayId, Long timeId) {
            this.dayId = dayId;
            this.timeId = timeId;
        }

        public Long getDayId() { return dayId; }
        public void setDayId(Long dayId) { this.dayId = dayId; }
        public Long getTimeId() { return timeId; }
        public void setTimeId(Long timeId) { this.timeId = timeId; }
    }
}
