package ru.yandex.money.android.test.properties;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public final class TestProperties extends BaseProperties {

    public TestProperties() {
        super("/test.properties");
    }

    public int getNetworkTimeout() {
        return parseInt("network.timeout");
    }

    public int getManualTimeout() {
        return parseInt("manual.timeout");
    }

    public int getAnimationsTimeout() {
        return parseInt("animations.timeout");
    }

    private int parseInt(String propertyName) {
        return Integer.parseInt(get(propertyName));
    }
}
