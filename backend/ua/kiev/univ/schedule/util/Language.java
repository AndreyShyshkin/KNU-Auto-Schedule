package ua.kiev.univ.schedule.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class Language {

    public static final String DEFAULT = "English";

    // Оновлений шлях під нову структуру проекту
    private static final File ROOT = new File("resources/languages");
    private static final Properties textMap = new Properties();

    public static String[] getLanguages() {
        // Повертає список файлів у папці, або порожній масив, якщо папки немає
        String[] list = ROOT.list();
        return list != null ? list : new String[0];
    }

    public static void load(String language) throws IOException {
        textMap.clear();
        // try-with-resources автоматично закриває файл після читання
        try (Reader reader = new FileReader(new File(ROOT, language))) {
            textMap.load(reader);
        }
    }

    public static String getText(String key) {
        return textMap.getProperty(key);
    }
}