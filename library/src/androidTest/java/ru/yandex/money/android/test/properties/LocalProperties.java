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

    public String getHostUrl() {
        return get("host.url");
    }

    public String getClientId() {
        return get("client.id");
    }

    public String getInstanceId() {
        return get("instance.id");
    }

    public Card getCard() {
        return new Card(get("card.type"), get("card.number"), get("card.month"), get("card.year"),
                get("card.csc"), get("card.token"));
    }

    public PhoneParams getPhoneParams() {
        return new PhoneParams(get("params.phone"), getAmount());
    }

    public BigDecimal getAmount() {
        return new BigDecimal(get("params.amount"));
    }

    public static final class Card {

        public final String type;
        public final String number;
        public final String month;
        public final String year;
        public final String csc;
        public final String token;

        public Card(String type, String number, String month, String year, String csc,
                    String token) {
            this.type = type;
            this.number = number;
            this.month = month;
            this.year = year;
            this.csc = csc;
            this.token = token;
        }
    }
}
