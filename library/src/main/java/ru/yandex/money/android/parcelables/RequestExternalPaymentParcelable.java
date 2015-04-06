/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 NBCO Yandex.Money LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
