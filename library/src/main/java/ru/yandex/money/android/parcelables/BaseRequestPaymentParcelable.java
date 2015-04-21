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

import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.model.Error;

import java.math.BigDecimal;

import ru.yandex.money.android.utils.Parcelables;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public abstract class BaseRequestPaymentParcelable implements Parcelable {

    public final BaseRequestPayment baseRequestPayment;

    public BaseRequestPaymentParcelable(BaseRequestPayment baseRequestPayment) {
        if (baseRequestPayment == null) {
            throw new NullPointerException("baseRequestPayment is null");
        }
        this.baseRequestPayment = baseRequestPayment;
    }

    protected BaseRequestPaymentParcelable(Parcel parcel) {
        baseRequestPayment = createBaseRequestPayment(parcel,
                (BaseRequestPayment.Status) parcel.readSerializable(),
                (Error) parcel.readSerializable(), parcel.readString(),
                Parcelables.readBigDecimal(parcel));
    }

    @Override
    public final int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(baseRequestPayment.status);
        dest.writeSerializable(baseRequestPayment.error);
        dest.writeString(baseRequestPayment.requestId);
        Parcelables.writeBigDecimal(dest, baseRequestPayment.contractAmount);
    }

    protected abstract BaseRequestPayment createBaseRequestPayment(
            Parcel parcel, BaseRequestPayment.Status status, Error error, String requestId,
            BigDecimal contractAmount);
}
