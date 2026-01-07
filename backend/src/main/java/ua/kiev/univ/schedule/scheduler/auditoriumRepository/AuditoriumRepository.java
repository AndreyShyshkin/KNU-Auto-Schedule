package ua.kiev.univ.schedule.scheduler.auditoriumRepository;

import ua.kiev.univ.schedule.model.placement.Auditorium;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class AuditoriumRepository {

    private final int[][] amount;
    private final List<Auditorium>[] lists;
    private final ListIterator<Auditorium>[][] iterators;

    public AuditoriumRepository(int[][] amount, List<Auditorium>[] lists, ListIterator<Auditorium>[][] iterators) {
        this.amount = amount;
        this.lists = lists;
        this.iterators = iterators;
    }

    // get free amount of auditoriums from 'size' auditoriums
    protected abstract int getSize(int color, int type, int size);

    // occupy 'count' auditoriums of specified 'type' for specified 'color'
    public boolean get(int color, int type, int count) {
        int value = amount[type][color] + count;
        int size = getSize(color, type, lists[type].size());
        if (value > size) {
            return false;
        }
        amount[type][color] = value;
        return true;
    }

    // deliver 'count' auditoriums of specified 'type' for specified 'color'
    public void put(int color, int type, int count) {
        amount[type][color] -= count;
    }

    protected abstract Auditorium getAuditorium(ListIterator<Auditorium> iterator);

    // occupy auditoriums from list
    public List<Auditorium> getAuditoriums(int color, int type, int count) {
        List<Auditorium> auditoriums = new LinkedList<>();
        ListIterator<Auditorium> iterator = iterators[type][color];
        while (count-- > 0) {
            auditoriums.add(getAuditorium(iterator));
        }
        return auditoriums;
    }

    protected abstract void putAuditorium(ListIterator<Auditorium> iterator);

    // deliver auditoriums to list
    public void putAuditoriums(int color, int type, int count) {
        ListIterator<Auditorium> iterator = iterators[type][color];
        while (count-- > 0) {
            putAuditorium(iterator);
        }
    }
}