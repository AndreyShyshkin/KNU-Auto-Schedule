package ua.kiev.univ.schedule.view;

import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.Internationalized;
import ua.kiev.univ.schedule.view.core.Refreshable;
import ua.kiev.univ.schedule.view.menu.MenuBar;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Frame extends JFrame implements Refreshable, Internationalized {

    private final MenuBar menuBar = new MenuBar(this, "menu");
    private final ContentPane contentPane = new ContentPane();

    public Frame() {
        this.setJMenuBar(menuBar);
        this.setContentPane(contentPane);

        // Встановлюємо дію за замовчуванням, щоб процес завершувався коректно
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Frame.this.close();
            }
        });

        this.setLanguage(Language.DEFAULT);
        this.refresh();
        this.pack();
        this.setLocationRelativeTo(null); // Центруємо вікно на екрані
        this.setVisible(true);
    }

    @Override
    public void refresh() {
        contentPane.refresh();
    }

    @Override
    public void loadLanguage() {
        this.setTitle(Language.getText("title"));
        menuBar.loadLanguage();
        contentPane.loadLanguage();
    }

    public void setLanguage(String language) {
        try {
            Language.load(language);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading language: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            // Якщо не вдалося завантажити мову, можна спробувати завантажити дефолтну або закрити
        }
        loadLanguage();
    }

    public void close() {
        // Тут можна додати діалог "Зберегти зміни?"
        this.dispose();
        System.exit(0);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(Frame::new);
    }

}
