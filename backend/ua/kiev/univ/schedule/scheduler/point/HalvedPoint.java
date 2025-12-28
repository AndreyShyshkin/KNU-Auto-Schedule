package ua.kiev.univ.schedule.scheduler.point;

import ua.kiev.univ.schedule.model.appointment.HalvedAppointment;
import ua.kiev.univ.schedule.model.appointment.Part;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.scheduler.ColorMap;
import ua.kiev.univ.schedule.scheduler.Progress;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepository;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepositoryFactory;

import java.util.List;

public class HalvedPoint extends Point {

    public int color;
    public int max;
    public Part part;

    protected HalvedPoint(Lesson lesson, List<Date> dates, List<Earmark> earmarks, RestrictionMap restrictionMap) {
        super(lesson, dates, earmarks, restrictionMap);
        Progress.DONE.value++;
    }

    @Override
    public HalvedAppointment getAppointment(List<Date> dates, ColorMap colorMap, AuditoriumRepositoryFactory repositoryFactory) {
        HalvedAppointment appointment = new HalvedAppointment();
        // Спочатку ініціалізуємо базову частину (повні пари, якщо вони є в цьому уроці)
        initAppointment(appointment, dates, colorMap, repositoryFactory);

        // Потім додаємо специфіку половинки
        appointment.setDate(dates.get(colorMap.getDate(color)));
        appointment.setPart(part);

        AuditoriumRepository repository = repositoryFactory.getAuditoriumRepository(part);
        List<Auditorium> auditoriums = repository.getAuditoriums(color, earmark, size);
        appointment.setAuditoriums(auditoriums);

        return appointment;
    }
}