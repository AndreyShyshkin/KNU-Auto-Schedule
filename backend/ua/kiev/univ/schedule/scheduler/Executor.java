package ua.kiev.univ.schedule.scheduler;

import ua.kiev.univ.schedule.model.appointment.Appointment;
import ua.kiev.univ.schedule.model.appointment.Part;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.model.member.Group;
import ua.kiev.univ.schedule.model.member.Teacher;
import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepository;
import ua.kiev.univ.schedule.scheduler.auditoriumRepository.AuditoriumRepositoryFactory;
import ua.kiev.univ.schedule.scheduler.point.HalvedPoint;
import ua.kiev.univ.schedule.scheduler.point.Point;
import ua.kiev.univ.schedule.scheduler.point.RestrictionMap;
import ua.kiev.univ.schedule.service.core.DataService;
import ua.kiev.univ.schedule.util.EntityFilter;

import java.util.*;

public class Executor {

    private final List<Date> dates;
    private final int count;
    private final ColorMap colorMap;
    private final AuditoriumRepositoryFactory repositoryFactory;
    private final List<Point> points;
    private ListIterator<Point> iterator;
    private Point point;
    private HalvedPoint halvedPoint;
    private int index;
    private Part part;
    private int color;
    private int max;
    private int progress;
    private boolean clear;
    private AuditoriumRepository repository;

    public Executor() {
        dates = EntityFilter.getActiveDates();
        count = dates.size();
        colorMap = new ColorMap(count);

        List<Earmark> earmarks = EntityFilter.getActiveEntities(Earmark.class);
        List<Auditorium> auditoriums = EntityFilter.getActiveEntities(Auditorium.class);
        repositoryFactory = new AuditoriumRepositoryFactory(count, earmarks, auditoriums);

        points = new LinkedList<>();
        List<Group> groups = EntityFilter.getActiveEntities(Group.class);
        List<Teacher> teachers = EntityFilter.getActiveEntities(Teacher.class);
        RestrictionMap restrictionMap = new RestrictionMap(dates, groups, teachers);

        Progress.DONE.value = 0;
        Progress.BUILD.value = 0;

        List<Lesson> lessons = EntityFilter.getActiveEntities(Lesson.class);
        for (Lesson lesson : lessons) {
            points.add(Point.getPoint(lesson, dates, earmarks, restrictionMap));
        }
        Point.setVerges(points);
        // Сортуємо точки за кількістю зв'язків (евристика: починати з найскладніших)
        points.sort(Comparator.comparingInt(point1 -> point1.verges.size()));
        Point.filterVerges(points);
    }

    public Progress initialize() {
        iterator = points.listIterator();
        max = Math.min(1, count);
        return nextPoint();
    }

    private void nextMax() {
        if ((color + 1 == max) && (max < count)) {
            max++;
        }
    }

    private boolean nextColor() {
        if (part == Part.FIRST) {
            repository = repositoryFactory.getAuditoriumRepository(part = Part.SECOND);
            return true;
        }
        if (part == Part.SECOND) {
            repository = repositoryFactory.getAuditoriumRepository(part = Part.FIRST);
        }
        return (++color < max);
    }

    private void setColor() {
        if (part != Part.BOTH) {
            halvedPoint.color = color;
            halvedPoint.max = max;
            halvedPoint.part = part;
        } else {
            point.colors[index] = color;
            point.maxes[index] = max;
        }
    }

    private void setNewPart() {
        halvedPoint = (HalvedPoint) point;
        color = -1;
        part = Part.SECOND;
    }

    private Progress nextPoint() {
        if (!iterator.hasNext()) {
            return Progress.DONE;
        }
        point = iterator.next();
        if (point.colors.length == 0) {
            setNewPart();
        } else {
            index = 0;
            color = -1;
            part = Part.BOTH;
        }
        return Progress.BUILD;
    }

    private Progress next() {
        clear = false;
        progress++;
        // System.out.println(progress); // Можна розкоментувати для дебагу
        if (progress > Progress.BUILD.value) {
            Progress.BUILD.value = progress;
        }
        nextMax();
        if (part == Part.BOTH) {
            if (++index == point.colors.length) {
                if (point instanceof HalvedPoint) {
                    setNewPart();
                } else {
                    return nextPoint();
                }
            }
            return Progress.BUILD;
        } else {
            return nextPoint();
        }
    }

    private void getIndexColor() {
        color = point.colors[index];
        max = point.maxes[index];
    }

    private void getLastColor() {
        index = point.colors.length - 1;
        part = Part.BOTH;
        getIndexColor();
    }

    private Progress prevPoint() {
        iterator.previous();
        if (!iterator.hasPrevious()) {
            return Progress.FAIL;
        }
        point = iterator.previous();
        if (point instanceof HalvedPoint) {
            halvedPoint = (HalvedPoint) point;
            color = halvedPoint.color;
            max = halvedPoint.max;
            part = halvedPoint.part;
        } else {
            getLastColor();
        }
        iterator.next();
        return Progress.BUILD;
    }

    private Progress prev() {
        clear = true;
        progress--;
        if (part == Part.BOTH) {
            if (index-- == 0) {
                return prevPoint();
            } else {
                getIndexColor();
            }
        } else {
            if (point.colors.length == 0) {
                return prevPoint();
            } else {
                getLastColor();
            }
        }
        return Progress.BUILD;
    }

    private void addAdjacent(Point point, int[] ths, int[] tht) {
        if (ths[color]++ == 0) {
            if (tht[color] == 0) {
                point.both--;
                point.part++;
            } else {
                point.part--;
            }
        }
    }

    private void addAdjacent(Point point) {
        if (part.isFirst) {
            addAdjacent(point, point.first, point.second);
        }
        if (part.isSecond) {
            addAdjacent(point, point.second, point.first);
        }
    }

    private void removeAdjacent(Point point, int[] ths, int[] tht) {
        if (--ths[color] == 0) {
            if (tht[color] == 0) {
                point.both++;
                point.part--;
            } else {
                point.part++;
            }
        }
    }

    private void removeAdjacent(Point point) {
        if (part.isFirst) {
            removeAdjacent(point, point.first, point.second);
        }
        if (part.isSecond) {
            removeAdjacent(point, point.second, point.first);
        }
    }

    private boolean hasAdjacent() {
        return (part.isFirst && point.first[color] > 0) || (part.isSecond && point.second[color] > 0);
    }

    private boolean isAdjacentable(Point point) {
        int free = point.both - point.colors.length;
        return (free >= 0) && (!(point instanceof HalvedPoint) || (free > 0) || (point.part > 0));
    }

    public Progress step() {
        repository = repositoryFactory.getAuditoriumRepository(part);
        if (clear) {
            repository.put(color, point.earmark, point.size);
            colorMap.removeRestriction(color, point.restriction);
            removeAdjacent(point);
            for (Point verge : point.verges) {
                removeAdjacent(verge);
            }
        }
        search: while (nextColor()) {
            if (hasAdjacent()) {
                continue;
            }
            ListIterator<Point> iter = point.verges.listIterator();
            while (iter.hasNext()) {
                Point verge = iter.next();
                addAdjacent(verge);
                if (!isAdjacentable(verge)) {
                    while (iter.hasPrevious()) {
                        removeAdjacent(iter.previous());
                    }
                    continue search;
                }
            }
            addAdjacent(point);
            if (!repository.get(color, point.earmark, point.size)) {
                continue;
            }
            /*
            colorMap.addRestriction(color, point.restriction);
            if (colorMap.getEstimate() < -100) {
                colorMap.removeRestriction(color, point.restriction);
                // repository.remove (не дописано в оригіналі)
                continue;
            }
            */
            setColor();
            return next();
        }
        return prev();
    }

    public void setAppointments() {
        List<Appointment> appointments = DataService.getEntities(Appointment.class);
        appointments.clear();
        if (progress == Progress.DONE.value) {
            for (Point point : points) {
                appointments.add(point.getAppointment(dates, colorMap, repositoryFactory));
            }
        }
    }
}