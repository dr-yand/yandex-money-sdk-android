package ru.yandex.money.android.utils;

import android.os.Handler;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public class UiThreadExecutor {

    private static UiThreadExecutor instance;

    private final Handler handler = new Handler();

    private UiThreadExecutor() {
    }

    public static synchronized UiThreadExecutor getInstance() {
        if (instance == null) {
            instance = new UiThreadExecutor();
        }
        return instance;
    }

    public void post(Runnable runnable) {
        handler.post(runnable);
    }
}
