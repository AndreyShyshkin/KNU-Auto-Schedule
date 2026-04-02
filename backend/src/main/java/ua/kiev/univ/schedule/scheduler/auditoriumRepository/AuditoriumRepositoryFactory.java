package ua.kiev.univ.schedule.scheduler.auditoriumRepository;

import ua.kiev.univ.schedule.model.appointment.Part;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.placement.Auditorium;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class AuditoriumRepositoryFactory {

    private final Map<Part, AuditoriumRepository> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    public AuditoriumRepositoryFactory(int count, List<BuildingEarmark> types, List<Auditorium> auditoriums, List<Date> dates) {
        int size = types.size();

        // Масиви лічильників: [ТипАудиторії][ЧасовийСлот]
        int[][] bothAmount = new int[size][count];
        int[][] firstAmount = new int[size][count];
        int[][] secondAmount = new int[size][count];

        // Створення масиву списків (Java не дозволяє new List<Auditorium>[size] напряму без warning)
        List<Auditorium>[] lists = new List[size];
        for (int i = 0; i < size; i++) {
            lists[i] = new LinkedList<>();
        }

        // Розподіл аудиторій по списках відповідно до їх типу (Earmark + Building)
        for (Auditorium auditorium : auditoriums) {
            BuildingEarmark be = new BuildingEarmark(auditorium.getBuilding(), auditorium.getEarmark());
            int i = types.indexOf(be);
            if (i >= 0) {
                lists[i].add(auditorium);
            }
        }

        // Ініціалізація ітераторів для кожного часового слоту
        ListIterator<Auditorium>[][] bothIterators = new ListIterator[size][count];
        ListIterator<Auditorium>[][] firstIterators = new ListIterator[size][count];
        ListIterator<Auditorium>[][] secondIterators = new ListIterator[size][count];

        for (int i = 0; i < size; i++) {
            List<Auditorium> list = lists[i];
            for (int j = 0; j < count; j++) {
                // Звичайні пари беруть з початку
                bothIterators[i][j] = list.listIterator();
                // Половинчасті пари беруть з кінця (list.size())
                firstIterators[i][j] = list.listIterator(list.size());
                secondIterators[i][j] = list.listIterator(list.size());
            }
        }

        map.put(Part.BOTH, new BothAuditoriumRepository(bothAmount, firstAmount, secondAmount, lists, bothIterators, dates, types));
        map.put(Part.FIRST, new PartAuditoriumRepository(firstAmount, bothAmount, lists, firstIterators, dates, types));
        map.put(Part.SECOND, new PartAuditoriumRepository(secondAmount, bothAmount, lists, secondIterators, dates, types));
    }

    public AuditoriumRepository getAuditoriumRepository(Part part) {
        return map.get(part);
    }
}