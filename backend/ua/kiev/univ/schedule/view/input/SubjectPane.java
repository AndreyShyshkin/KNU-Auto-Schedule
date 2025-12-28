package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.model.subject.Subject;
import ua.kiev.univ.schedule.service.subject.SubjectService;
import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.input.table.EntityTablePane;

public class SubjectPane extends EntityTablePane<Subject> {

    private final String key;
    private final InputPane inputPane;

    public SubjectPane(InputPane inputPane, String key) {
        super(new SubjectService(), key);
        this.inputPane = inputPane;
        this.key = key;
    }

    @Override
    public void loadLanguage() {
        inputPane.setTitleAt(inputPane.indexOfComponent(this), Language.getText(key));
        super.loadLanguage();
    }
}