package ru.yandex.money.android.test.properties;

import com.yandex.money.api.methods.params.PhoneParams;

import java.math.BigDecimal;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public final class LocalProperties extends BaseProperties {

    public LocalProperties() {
        super("/local.properties");
    }

    public String getClientId() {
        return get("client.id");
    }

    public Card getCard() {
        return new Card(get("card.number"), get("card.month"), get("card.year"), get("card.csc"));
    }

    public PhoneParams getPhoneParams() {
        return new PhoneParams(get("params.phone"), getAmount());
    }

    public BigDecimal getAmount() {
        return new BigDecimal(get("params.amount"));
    }

    public static final class Card {

        public final String number;
        public final String month;
        public final String year;
        public final String csc;

        public Card(String number, String month, String year, String csc) {
            this.number = number;
            this.month = month;
            this.year = year;
            this.csc = csc;
        }
    }
}
