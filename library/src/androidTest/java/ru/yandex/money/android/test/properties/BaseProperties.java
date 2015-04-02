package ru.yandex.money.android.test.properties;

import java.util.Properties;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public abstract class BaseProperties {

    private final Properties properties = new Properties();

    public BaseProperties(String resource) {
        if (resource == null || resource.isEmpty()) {
            throw new NullPointerException("resource is null or empty");
        }

        try {
            properties.load(BaseProperties.class.getResourceAsStream(resource));
        } catch (Exception e) {
            throw new RuntimeException("properties not found", e);
        }
    }

    protected final String get(String propertyName) {
        return properties.getProperty(propertyName, "");
    }
}
