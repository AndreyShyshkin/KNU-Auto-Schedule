package ua.kiev.univ.schedule.view.input.lesson;

import ua.kiev.univ.schedule.model.lesson.MemberedEntity;
import ua.kiev.univ.schedule.service.lesson.MemberedEntityService;
import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.SplitPane;
import ua.kiev.univ.schedule.view.input.InputPane;
import ua.kiev.univ.schedule.view.input.lesson.member.MembersPane;
import ua.kiev.univ.schedule.view.input.lesson.subject.SubjectedPane;

public class MemberedPane<E extends MemberedEntity> extends SplitPane {

    private final String key;
    private final InputPane inputPane;
    public SubjectedPane<E> subjectedPane;
    public MembersPane<E> membersPane;

    public MemberedPane(MemberedEntityService<E> service, InputPane inputPane, String key) {
        super(HORIZONTAL_SPLIT);
        this.inputPane = inputPane;
        this.key = key;

        subjectedPane = new SubjectedPane<>(service, key);
        membersPane = new MembersPane<>(service, key);

        this.setLeftComponent(subjectedPane);
        this.setRightComponent(membersPane);
    }

    @Override
    public void refresh() {
        subjectedPane.refresh();
        membersPane.refresh();
    }

    @Override
    public void loadLanguage() {
        inputPane.setTitleAt(inputPane.indexOfComponent(this), Language.getText(key));
        subjectedPane.loadLanguage();
        membersPane.loadLanguage();
    }
}