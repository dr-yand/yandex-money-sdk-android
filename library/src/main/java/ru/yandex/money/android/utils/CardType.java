package ru.yandex.money.android.utils;

import com.yandex.money.api.model.Card;

import ru.yandex.money.android.R;

/**
 * @author vyasevich
 */
public enum CardType {

    VISA(R.drawable.ym_visa, R.drawable.ym_visa_card),
    MASTER_CARD(R.drawable.ym_mc, R.drawable.ym_mc_card),
    AMERICAN_EXPRESS(R.drawable.ym_ae, R.drawable.ym_ae_card),
    JCB(R.drawable.ym_default_card, R.drawable.ym_default_card),
    UNKNOWN(R.drawable.ym_default_card, R.drawable.ym_default_card);

    public final int icoResId;
    public final int cardResId;

    CardType(int icoResId, int cardResId) {
        this.icoResId = icoResId;
        this.cardResId = cardResId;
    }

    public static CardType get(Card.Type type) {
        if (type == null) {
            return UNKNOWN;
        }
        switch (type) {
            case VISA:
                return VISA;
            case MASTER_CARD:
                return MASTER_CARD;
            case AMERICAN_EXPRESS:
                return AMERICAN_EXPRESS;
            case JCB:
                return JCB;
            default:
                return UNKNOWN;
        }
    }
}
