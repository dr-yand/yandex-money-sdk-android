package ru.yandex.money.android.test;

import android.content.Context;
import android.text.TextUtils;

import com.yandex.money.api.model.Card;
import com.yandex.money.api.model.ExternalCard;

import ru.yandex.money.android.Prefs;
import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.test.properties.LocalProperties;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
final class AppData {

    private AppData() {
    }

    public static void clean(Context context) {
        checkContext(context);

        Prefs prefs = new Prefs(context);
        prefs.storeInstanceId("");

        DatabaseStorage storage = new DatabaseStorage(context);
        for (ExternalCard card : storage.selectMoneySources()) {
            storage.deleteMoneySource(card);
        }
    }

    public static void addSavedCard(Context context, String instanceId, LocalProperties.Card card) {
        checkContext(context);
        if (TextUtils.isEmpty(instanceId)) {
            throw new IllegalArgumentException("instanceId is null or empty");
        }
        if (card == null) {
            throw new NullPointerException("card is null");
        }

        Prefs prefs = new Prefs(context);
        prefs.storeInstanceId(instanceId);

        DatabaseStorage storage = new DatabaseStorage(context);
        storage.insertMoneySource(new ExternalCard(card.number, Card.Type.parse(card.type),
                "payment-card", card.token));
    }

    private static void checkContext(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
    }
}
