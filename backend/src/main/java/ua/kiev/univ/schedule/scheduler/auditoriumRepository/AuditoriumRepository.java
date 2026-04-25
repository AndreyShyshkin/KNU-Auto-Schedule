package ua.kiev.univ.schedule.scheduler.auditoriumRepository;

import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.placement.Auditorium;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public abstract class AuditoriumRepository {

    private final int[][] amount;
    private final List<Auditorium>[] lists;
    private final ListIterator<Auditorium>[][] iterators;
    private final List<Date> dates;
    private final List<BuildingEarmark> types;

    public AuditoriumRepository(int[][] amount, List<Auditorium>[] lists, ListIterator<Auditorium>[][] iterators, List<Date> dates, List<BuildingEarmark> types) {
        this.amount = amount;
        this.lists = lists;
        this.iterators = iterators;
        this.dates = dates;
        this.types = types;
    }

    // get free amount of auditoriums from 'size' auditoriums
    protected abstract int getSize(int color, int type, int size);

    // occupy 'count' auditoriums of specified 'type' for specified 'color'
    public boolean get(int color, int type, int count) {
        if (type < 0) {
            return false;
        }
        // Заборона розміщення: якщо будівля дати вказана і не збігається з будівлею предмета
        ua.kiev.univ.schedule.model.placement.Building slotBuilding = dates.get(color).getTime().getBuilding();
        ua.kiev.univ.schedule.model.placement.Building lessonBuilding = types.get(type).getBuilding();
        
        if (slotBuilding != null) {
            Long slotBuildingId = slotBuilding.getId();
            Long lessonBuildingId = (lessonBuilding != null) ? lessonBuilding.getId() : null;
            if (!Objects.equals(slotBuildingId, lessonBuildingId)) {
                return false;
            }
        }

        int value = amount[type][color] + count;
        int totalAuds = lists[type].size();
        int size = getSize(color, type, totalAuds);
        if (value > size) {
            System.out.println("DEBUG: Repository REJECTED Color " + color + " for Type " + type + 
                ". Value (" + value + ") > Size (" + size + "). Total auds in list: " + totalAuds);
            return false;
        }
        amount[type][color] = value;
        return true;
    }

    // deliver 'count' auditoriums of specified 'type' for specified 'color'
    public void put(int color, int type, int count) {
        if (type < 0) {
            return;
        }
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