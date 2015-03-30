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

    private final ExternalPaymentProcess.SavedState savedState;

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
                rep == null ? null : rep.getRequestExternalPayment(),
                pep == null ? null : pep.getProcessExternalPayment(),
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

    public ExternalPaymentProcess.SavedState getSavedState() {
        return savedState;
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
