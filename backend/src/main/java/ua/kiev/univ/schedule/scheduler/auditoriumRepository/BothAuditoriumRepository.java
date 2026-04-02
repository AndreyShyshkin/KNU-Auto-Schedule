package ua.kiev.univ.schedule.scheduler.auditoriumRepository;

import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.placement.Auditorium;

import java.util.List;
import java.util.ListIterator;

public class BothAuditoriumRepository extends AuditoriumRepository {

    private final int[][] firstAmount;
    private final int[][] secondAmount;

    public BothAuditoriumRepository(int[][] bothAmount, int[][] firstAmount, int[][] secondAmount, List<Auditorium>[] lists, ListIterator<Auditorium>[][] iterators, List<Date> dates, List<BuildingEarmark> types) {
        super(bothAmount, lists, iterators, dates, types);
        this.firstAmount = firstAmount;
        this.secondAmount = secondAmount;
    }

    @Override
    protected int getSize(int color, int type, int size) {
        // Доступний розмір = Всього - Максимум(ЗайнятоЧисельником, ЗайнятоЗнаменником)
        return size - Math.max(firstAmount[type][color], secondAmount[type][color]);
    }

    @Override
    protected Auditorium getAuditorium(ListIterator<Auditorium> iterator) {
        return iterator.next();
    }

    @Override
    protected void putAuditorium(ListIterator<Auditorium> iterator) {
        iterator.previous();
    }
}