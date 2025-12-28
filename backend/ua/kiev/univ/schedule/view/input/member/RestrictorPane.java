package ua.kiev.univ.schedule.view.input.member;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.member.Restrictor;
import ua.kiev.univ.schedule.service.member.RestrictionService;
import ua.kiev.univ.schedule.service.member.RestrictorService;
import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.SplitPane;
import ua.kiev.univ.schedule.view.input.InputPane;
import ua.kiev.univ.schedule.view.input.member.restriction.RestrictionPane;

public class RestrictorPane<D extends Department, E extends Restrictor<D>> extends SplitPane {

    private final String key;
    private final InputPane inputPane;
    public final MemberPane<D, E> memberPane;
    public final RestrictionPane<D, E> restrictionPane;

    public RestrictorPane(RestrictorService<D, E> service, InputPane inputPane, String key) {
        super(VERTICAL_SPLIT);
        this.inputPane = inputPane;
        this.key = key;

        memberPane = new MemberPane<>(service, key);
        // Створюємо RestrictionPane з новим сервісом обмежень
        restrictionPane = new RestrictionPane<>(new RestrictionService<>(service), key + ".restriction");

        service.setRestrictionPane(restrictionPane);

        this.setTopComponent(memberPane);
        this.setBottomComponent(restrictionPane);
    }

    @Override
    public void refresh() {
        memberPane.refresh();
        restrictionPane.refresh();
    }

    @Override
    public void loadLanguage() {
        if (inputPane != null) {
            // Оновлюємо заголовок вкладки
            int index = inputPane.indexOfComponent(this);
            if (index >= 0) {
                inputPane.setTitleAt(index, Language.getText(key));
            }
        }
        memberPane.loadLanguage();
        restrictionPane.loadLanguage();
    }
}