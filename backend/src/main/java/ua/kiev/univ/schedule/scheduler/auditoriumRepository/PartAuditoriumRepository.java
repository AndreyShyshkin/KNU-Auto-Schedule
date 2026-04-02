package ua.kiev.univ.schedule.scheduler.auditoriumRepository;

import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.placement.Auditorium;

import java.util.List;
import java.util.ListIterator;

public class PartAuditoriumRepository extends AuditoriumRepository {

    private final int[][] bothAmount;

    public PartAuditoriumRepository(int[][] amount, int[][] bothAmount, List<Auditorium>[] lists, ListIterator<Auditorium>[][] iterators, List<Date> dates, List<BuildingEarmark> types) {
        super(amount, lists, iterators, dates, types);
        this.bothAmount = bothAmount;
    }

    @Override
    protected int getSize(int color, int type, int size) {
        return size - bothAmount[type][color];
    }

    @Override
    protected Auditorium getAuditorium(ListIterator<Auditorium> iterator) {
        return iterator.previous();
    }

    @Override
    protected void putAuditorium(ListIterator<Auditorium> iterator) {
        iterator.next();
    }
}