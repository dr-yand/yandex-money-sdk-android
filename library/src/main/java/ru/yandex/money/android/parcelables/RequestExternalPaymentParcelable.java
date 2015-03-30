package ru.yandex.money.android.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import com.yandex.money.api.methods.RequestExternalPayment;
import com.yandex.money.api.model.Error;

import java.math.BigDecimal;

import ru.yandex.money.android.utils.Parcelables;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public final class RequestExternalPaymentParcelable implements Parcelable {

    private final RequestExternalPayment rep;

    public RequestExternalPaymentParcelable(RequestExternalPayment rep) {
        if (rep == null) {
            throw new NullPointerException("rep is null");
        }
        this.rep = rep;
    }

    private RequestExternalPaymentParcelable(Parcel parcel) {
        RequestExternalPayment.Status status = (RequestExternalPayment.Status) parcel.readSerializable();
        Error error = (Error) parcel.readSerializable();
        String requestId = parcel.readString();
        BigDecimal bigDecimal = Parcelables.readBigDecimal(parcel);
        rep = new RequestExternalPayment(status, error, requestId,
                bigDecimal == null ? null : bigDecimal, parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(rep.status);
        dest.writeSerializable(rep.error);
        dest.writeString(rep.requestId);
        Parcelables.writeBigDecimal(dest, rep.contractAmount);
        dest.writeString(rep.title);
    }

    public RequestExternalPayment getRequestExternalPayment() {
        return rep;
    }

    public static final Creator<RequestExternalPaymentParcelable> CREATOR =
            new Creator<RequestExternalPaymentParcelable>() {
                @Override
                public RequestExternalPaymentParcelable createFromParcel(Parcel source) {
                    return new RequestExternalPaymentParcelable(source);
                }

                @Override
                public RequestExternalPaymentParcelable[] newArray(int size) {
                    return new RequestExternalPaymentParcelable[size];
                }
            };
}
