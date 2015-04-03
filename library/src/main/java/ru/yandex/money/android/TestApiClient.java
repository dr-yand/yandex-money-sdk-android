package ru.yandex.money.android;

import android.text.TextUtils;

import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.HostsProvider;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
final class TestApiClient extends DefaultApiClient {

    private final HostsProvider hostsProvider;

    TestApiClient(final String clientId, final String url) {
        super(clientId, true);
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url is null or empty");
        }
        hostsProvider = new HostsProvider(true) {
            @Override
            public String getMoney() {
                return url;
            }
        };
    }

    @Override
    public HostsProvider getHostsProvider() {
        return hostsProvider;
    }
}
