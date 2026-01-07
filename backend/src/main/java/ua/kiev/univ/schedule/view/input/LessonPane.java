package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.model.lesson.Lesson;
import ua.kiev.univ.schedule.service.lesson.LessonService;
import ua.kiev.univ.schedule.view.input.lesson.MemberedPane;

public class LessonPane extends MemberedPane<Lesson> {

    public LessonPane(InputPane inputPane, String key) {
        super(new LessonService(), inputPane, key);
    }
}