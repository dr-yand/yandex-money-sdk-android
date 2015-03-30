package ru.yandex.money.android.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.yandex.money.api.model.Card;
import com.yandex.money.api.model.ExternalCard;

/**
 * @author Slava Yaseich (vyasevich@yamoney.ru)
 */
public final class ExternalCardParcelable implements Parcelable {

    private final ExternalCard externalCard;

    public ExternalCardParcelable(ExternalCard externalCard) {
        if (externalCard == null) {
            throw new NullPointerException("externalCard is null");
        }
        this.externalCard = externalCard;
    }

    private ExternalCardParcelable(Parcel parcel) {
        externalCard = new ExternalCard(parcel.readString(), (Card.Type) parcel.readSerializable(),
                parcel.readString(), parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(externalCard.panFragment);
        dest.writeSerializable(externalCard.type);
        dest.writeString(externalCard.fundingSourceType);
        dest.writeString(externalCard.moneySourceToken);
    }

    public ExternalCard getExternalCard() {
        return externalCard;
    }

    public static final Creator<ExternalCardParcelable> CREATOR =
            new Creator<ExternalCardParcelable>() {
                @Override
                public ExternalCardParcelable createFromParcel(Parcel source) {
                    return new ExternalCardParcelable(source);
                }

                @Override
                public ExternalCardParcelable[] newArray(int size) {
                    return new ExternalCardParcelable[size];
                }
            };
}
