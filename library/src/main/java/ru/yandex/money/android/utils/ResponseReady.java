package ru.yandex.money.android.utils;

import com.yandex.money.api.net.OnResponseReady;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public abstract class ResponseReady<T> implements OnResponseReady<T> {

    private final UiThreadExecutor executor = UiThreadExecutor.getInstance();

    @Override
    public final void onFailure(final Exception exception) {
        executor.post(new Runnable() {
            @Override
            public void run() {
                failure(exception);
            }
        });
    }

    @Override
    public final void onResponse(final T response) {
        executor.post(new Runnable() {
            @Override
            public void run() {
                response(response);
            }
        });
    }

    protected abstract void failure(Exception exception);

    protected abstract void response(T response);
}
