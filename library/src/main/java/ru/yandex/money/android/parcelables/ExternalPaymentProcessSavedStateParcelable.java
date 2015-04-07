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
import com.yandex.money.api.methods.RequestExternalPayment;
import com.yandex.money.api.processes.ExternalPaymentProcess;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public final class ExternalPaymentProcessSavedStateParcelable implements Parcelable {

    public final ExternalPaymentProcess.SavedState savedState;

    public ExternalPaymentProcessSavedStateParcelable(ExternalPaymentProcess.SavedState savedState) {
        if (savedState == null) {
            throw new NullPointerException("savedState is null");
        }
        this.savedState = savedState;
    }

    private ExternalPaymentProcessSavedStateParcelable(Parcel parcel) {
        RequestExternalPaymentParcelable rep = parcel.readParcelable(
                RequestExternalPaymentParcelable.class.getClassLoader());
        ProcessExternalPaymentParcelable pep = parcel.readParcelable(
                ProcessExternalPaymentParcelable.class.getClassLoader());
        savedState = new ExternalPaymentProcess.SavedState(
                rep == null ? null : rep.rep,
                pep == null ? null : pep.pep,
                parcel.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        RequestExternalPayment requestPayment = savedState.getRequestPayment();
        dest.writeParcelable(requestPayment == null ? null :
                new RequestExternalPaymentParcelable(requestPayment), flags);

        ProcessExternalPayment processPayment = savedState.getProcessPayment();
        dest.writeParcelable(processPayment == null ? null :
                new ProcessExternalPaymentParcelable(processPayment), flags);

        dest.writeInt(savedState.getFlags());
    }

    public static final Creator<ExternalPaymentProcessSavedStateParcelable> CREATOR =
            new Creator<ExternalPaymentProcessSavedStateParcelable>() {

        @Override
        public ExternalPaymentProcessSavedStateParcelable createFromParcel(Parcel source) {
            return new ExternalPaymentProcessSavedStateParcelable(source);
        }

        @Override
        public ExternalPaymentProcessSavedStateParcelable[] newArray(int size) {
            return new ExternalPaymentProcessSavedStateParcelable[size];
        }
    };
}
