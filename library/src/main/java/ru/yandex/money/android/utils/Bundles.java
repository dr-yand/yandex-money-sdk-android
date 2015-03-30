package ru.yandex.money.android.utils;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public final class Bundles {

    private Bundles() {
    }

    public static Bundle writeStringMapToBundle(Map<String, String> map) {
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        Bundle bundle = new Bundle();
        for (String key : map.keySet()) {
            bundle.putString(key, map.get(key));
        }
        return bundle;
    }

    public static Map<String, String> readStringMapFromBundle(Bundle bundle) {
        if (bundle == null) {
            throw new NullPointerException("bundle is null");
        }

        Map<String, String> map = new HashMap<String, String>();
        for (String key : bundle.keySet()) {
            map.put(key, bundle.getString(key));
        }
        return map;
    }
}
