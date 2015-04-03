package ru.yandex.money.android.test;

import android.content.Context;

import com.yandex.money.api.model.ExternalCard;

import ru.yandex.money.android.Prefs;
import ru.yandex.money.android.database.DatabaseStorage;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
final class Cleaner {

    private Cleaner() {
    }

    public static void perform(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }

        Prefs prefs = new Prefs(context);
        prefs.storeInstanceId("");

        DatabaseStorage storage = new DatabaseStorage(context);
        for (ExternalCard card : storage.selectMoneySources()) {
            storage.deleteMoneySource(card);
        }
    }
}
