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

import com.yandex.money.api.methods.ProcessExternalPayment;
import com.yandex.money.api.model.ExternalCard;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public final class ProcessExternalPaymentParcelable extends BaseProcessPaymentParcelable {

    public ProcessExternalPaymentParcelable(ProcessExternalPayment processExternalPayment) {
        super(processExternalPayment);
    }

    protected ProcessExternalPaymentParcelable(Parcel parcel) {
        super(parcel, new ProcessExternalPayment.Builder()
                .setExternalCard(readMoneySource(parcel)));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        writeMoneySource(dest, flags);
    }

    private void writeMoneySource(Parcel dest, int flags) {
        ProcessExternalPayment pep = (ProcessExternalPayment) baseProcessPayment;
        ExternalCard moneySource = pep.externalCard;
        ExternalCardParcelable parcelable = moneySource == null ? null :
                new ExternalCardParcelable(moneySource);
        dest.writeParcelable(parcelable, flags);
    }

    private static ExternalCard readMoneySource(Parcel parcel) {
        ExternalCardParcelable parcelable = parcel.readParcelable(
                ExternalCardParcelable.class.getClassLoader());
        return parcelable == null ? null : (ExternalCard) parcelable.moneySource;
    }

    public static final Creator<ProcessExternalPaymentParcelable> CREATOR =
            new Creator<ProcessExternalPaymentParcelable>() {
                @Override
                public ProcessExternalPaymentParcelable createFromParcel(Parcel source) {
                    return new ProcessExternalPaymentParcelable(source);
                }

                @Override
                public ProcessExternalPaymentParcelable[] newArray(int size) {
                    return new ProcessExternalPaymentParcelable[size];
                }
            };
}
