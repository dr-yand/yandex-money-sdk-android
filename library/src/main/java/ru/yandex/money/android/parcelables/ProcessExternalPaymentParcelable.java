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

import com.yandex.money.api.methods.ProcessExternalPayment;
import com.yandex.money.api.model.Error;
import com.yandex.money.api.model.ExternalCard;

import java.util.Map;

import ru.yandex.money.android.utils.Parcelables;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public final class ProcessExternalPaymentParcelable implements Parcelable {

    private final ProcessExternalPayment pep;

    public ProcessExternalPaymentParcelable(ProcessExternalPayment pep) {
        if (pep == null) {
            throw new NullPointerException("pep is null");
        }
        this.pep = pep;
    }

    private ProcessExternalPaymentParcelable(Parcel parcel) {
        ProcessExternalPayment.Status status =
                (ProcessExternalPayment.Status) parcel.readSerializable();
        Error error = (Error) parcel.readSerializable();
        String acsUri = parcel.readString();
        Map<String, String> acsParams = Parcelables.readStringMap(parcel);
        this.pep = new ProcessExternalPayment(status, error, acsUri, acsParams,
                readMoneySource(parcel), Parcelables.readNullableLong(parcel),
                parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(pep.status);
        dest.writeSerializable(pep.error);
        dest.writeString(pep.acsUri);
        Parcelables.writeStringMap(dest, pep.acsParams);
        writeMoneySource(dest, flags);
        Parcelables.writeNullableLong(dest, pep.nextRetry);
        dest.writeString(pep.invoiceId);
    }

    private void writeMoneySource(Parcel dest, int flags) {
        ExternalCard moneySource = pep.moneySource;
        ExternalCardParcelable parcelable = moneySource == null ? null :
                new ExternalCardParcelable(moneySource);
        dest.writeParcelable(parcelable, flags);
    }

    private ExternalCard readMoneySource(Parcel parcel) {
        ExternalCardParcelable parcelable = parcel.readParcelable(
                ExternalCardParcelable.class.getClassLoader());
        return parcelable == null ? null : parcelable.getExternalCard();
    }

    public ProcessExternalPayment getProcessExternalPayment() {
        return pep;
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
