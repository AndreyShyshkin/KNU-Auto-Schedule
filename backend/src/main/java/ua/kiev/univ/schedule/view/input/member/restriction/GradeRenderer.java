package ua.kiev.univ.schedule.view.input.member.restriction;

import ua.kiev.univ.schedule.model.member.Grade;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class GradeRenderer extends DefaultTableCellRenderer {

    private static final Color DEFAULT_COLOR = new Color(220, 220, 220);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Color color;
        if (value == null) {
            color = DEFAULT_COLOR;
        } else {
            Grade grade = (Grade) value;
            color = grade.color;
        }
        this.setBackground(color);
        // null замість value, щоб не малювати текст поверх кольору (якщо це потрібно)
        // або super...Component(table, "", ...)
        return super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
    }
}