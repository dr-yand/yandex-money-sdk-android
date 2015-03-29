package ru.yandex.money.android.fragments;

import android.app.Fragment;

import com.yandex.money.api.methods.ProcessExternalPayment;
import com.yandex.money.api.model.Error;
import com.yandex.money.api.model.ExternalCard;

import ru.yandex.money.android.PaymentActivity;

/**
 * @author vyasevich
 */
public abstract class PaymentFragment extends Fragment {

    protected static final String EXTRA_REQUEST_ID = "ru.yandex.money.android.extra.REQUEST_ID";
    protected static final String EXTRA_MONEY_SOURCE = "ru.yandex.money.android.extra.MONEY_SOURCE";

    protected PaymentActivity getPaymentActivity() {
        return (PaymentActivity) getActivity();
    }

    protected void showWeb() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showWeb();
            }
        });
    }

    protected void showWeb(final ProcessExternalPayment pep, final ExternalCard moneySource) {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showWeb(pep, moneySource);
            }
        });
    }

    protected void showCards() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showCards();
            }
        });
    }

    protected void showError(final Error error, final String status) {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showError(error, status);
            }
        });
    }

    protected void showCsc(final ExternalCard moneySource) {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showCsc(moneySource);
            }
        });
    }

    protected void showSuccess(final ExternalCard moneySource) {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showSuccess(moneySource);
            }
        });
    }

    protected void showProgressBar() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.showProgressBar();
            }
        });
    }

    protected void hideProgressBar() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.hideProgressBar();
            }
        });
    }

    protected void startActionSafely(Action action) {
        PaymentActivity activity = getPaymentActivity();
        if (activity != null) {
            action.start(activity);
        }
    }

    public interface Action {
        void start(PaymentActivity activity);
    }
}
