package ua.kiev.univ.schedule.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Language {

    public static final String DEFAULT = "English";

    private static final Properties textMap = new Properties();

    public static String[] getLanguages() {
        // Hardcoded list because listing directory inside JAR is not trivial in standard Java
        return new String[]{"English", "Українська"};
    }

    public static void load(String language) throws IOException {
        textMap.clear();
        String resourcePath = "/languages/" + language;
        try (InputStream is = Language.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Language file not found in classpath: " + resourcePath);
            }
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                textMap.load(reader);
            }
        }
    }

    public static String getText(String key) {
        return textMap.getProperty(key);
    }
}