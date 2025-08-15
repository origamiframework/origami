package ru.origami.common.environment;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@Slf4j
public final class Language {

    private static final Properties LANGUAGE_PROPERTIES = new Properties();
    static final String LANGUAGE = "language";
    static final String DEFAULT_LANGUAGE = "ru";

    static void load(InputStream inputStream) {
        try {
            LANGUAGE_PROPERTIES.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLangValue(String key) {
        return getLangPropertyValue(key);
    }

    private static String getLangPropertyValue(String key) {
        String propertyValue = null;

        try {
            propertyValue = new String(LANGUAGE_PROPERTIES.getProperty(key).getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception ex) {
            log.info("Не задан параметр: {}", key);
        }

        return propertyValue;
    }
}
