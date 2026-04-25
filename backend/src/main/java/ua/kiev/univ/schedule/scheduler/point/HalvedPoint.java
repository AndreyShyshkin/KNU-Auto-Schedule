package ua.kiev.univ.schedule.scheduler.point;

import ua.kiev.univ.schedule.model.appointment.HalvedAppointment;
import ua.kiev.univ.schedule.model.appointment.Part;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.scheduler.ColorMap;
import ua.kiev.univ.schedule.scheduler.Progress;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepository;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepositoryFactory;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.BuildingEarmark;

import java.util.List;

public class HalvedPoint extends Point {

    public int color;
    public int max;
    public Part part;

    protected HalvedPoint(Lesson lesson, List<Date> dates, List<BuildingEarmark> types, RestrictionMap restrictionMap) {
        super(lesson, dates, types, restrictionMap);
        // В календарному режимі ми поки що не підтримуємо поділ на чисельник/знаменник,
        // бо кожна дата вже унікальна. Тому HalvedPoint просто працює як звичайна точка.
    }

    @Override
    public HalvedAppointment getAppointment(List<Date> dates, ColorMap colorMap, AuditoriumRepositoryFactory repositoryFactory) {
        HalvedAppointment appointment = new HalvedAppointment();
        initAppointment(appointment, dates, colorMap, repositoryFactory);
        return appointment;
    }
}
