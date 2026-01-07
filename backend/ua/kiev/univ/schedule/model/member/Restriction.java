package ua.kiev.univ.schedule.model.member;

import ua.kiev.univ.schedule.model.core.Entity;
import ua.kiev.univ.schedule.model.date.Date;
import ua.kiev.univ.schedule.model.date.Day;
import ua.kiev.univ.schedule.model.date.Time;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Restriction extends Entity {

    private final Map<Date, Grade> gradeMap = new HashMap<>();

    public Map<Date, Grade> getGradeMap() {
        return gradeMap;
    }

    @Override
    public void read(DataInputStream is) throws IOException {
        gradeMap.clear();
        int count = is.readInt();
        while (count-- > 0) {
            Date date = new Date(is);
            Grade grade = Grade.values()[is.readInt()];
            gradeMap.put(date, grade);
        }
    }

    @Override
    public void write(DataOutputStream os) throws IOException {
        Set<Date> dates = gradeMap.keySet();
        os.writeInt(dates.size());
        for (Date date : dates) {
            date.write(os);
            Grade grade = gradeMap.get(date);
            os.writeInt(grade.ordinal());
        }
    }

    public void setGrade(Grade grade, Date date) {
        if (grade == Grade.NONE) {
            gradeMap.remove(date);
        } else {
            gradeMap.put(date, grade);
        }
    }

    public Grade getGrade(Date date) {
        Grade grade = gradeMap.get(date);
        return (grade == null) ? Grade.NONE : grade;
    }

    public void removeDay(Day day) {
        Iterator<Date> iterator = gradeMap.keySet().iterator();
        while (iterator.hasNext()) {
            Date date = iterator.next();
            if (date.getDay() == day) {
                iterator.remove();
            }
        }
    }

    public void removeTime(Time time) {
        Iterator<Date> iterator = gradeMap.keySet().iterator();
        while (iterator.hasNext()) {
            Date date = iterator.next();
            if (date.getTime() == time) {
                iterator.remove();
            }
        }
    }
}