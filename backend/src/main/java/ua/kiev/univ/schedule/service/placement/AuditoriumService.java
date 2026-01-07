package ua.kiev.univ.schedule.service.placement;

import ua.kiev.univ.schedule.model.placement.Auditorium;
import ua.kiev.univ.schedule.model.placement.Earmark;
import ua.kiev.univ.schedule.service.core.NamedEntityService;

public class AuditoriumService extends NamedEntityService<Auditorium> {

    protected EarmarkService earmarkService;

    public AuditoriumService(EarmarkService earmarkService) {
        super(Auditorium.class);
        this.earmarkService = earmarkService;
    }

    @Override
    public void refreshRows() {
        rows.clear();
        if (earmarkService != null && earmarkService.selectedRow != null) {
            Earmark earmark = earmarkService.selectedRow;
            for (Auditorium auditorium : entities) {
                if (auditorium.getEarmark() == earmark) {
                    rows.add(auditorium);
                }
            }
        }
    }

    @Override
    public boolean isAddEnable() {
        return earmarkService != null && earmarkService.selectedRow != null;
    }

    @Override
    protected void onAdd(Auditorium auditorium) {
        if (earmarkService != null) {
            auditorium.setEarmark(earmarkService.selectedRow);
        }
    }
}