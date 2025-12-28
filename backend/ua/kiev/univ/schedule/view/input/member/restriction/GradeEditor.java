package ua.kiev.univ.schedule.view.input.member.restriction;

import ua.kiev.univ.schedule.model.member.Grade;
import ua.kiev.univ.schedule.util.Language;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;

public class GradeEditor extends AbstractCellEditor implements TableCellEditor {

    private final String key;
    private final JComboBox<Grade> comboBox;

    public GradeEditor(String key) {
        this.key = key;
        comboBox = new JComboBox<>(Grade.values());

        // Кастомний рендерер для елементів списку (щоб вони теж були кольоровими)
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // Викликаємо super, щоб налаштувати базові параметри (шрифт, виділення)
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Grade) {
                    Grade grade = (Grade) value;
                    this.setBackground(grade.color);
                    this.setText(Language.getText(GradeEditor.this.key + "." + grade.name()));
                }
                return this;
            }
        });

        comboBox.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Grade grade = (Grade) value;
        comboBox.setSelectedItem(grade);
        return comboBox;
    }
}