package ru.yandex.money.android.fragments;

import android.app.Fragment;

import com.yandex.money.api.model.Error;
import com.yandex.money.api.model.ExternalCard;

import ru.yandex.money.android.PaymentActivity;

/**
 * @author vyasevich
 */
public abstract class PaymentFragment extends Fragment {

    protected static final String KEY_MONEY_SOURCE = "moneySource";

    protected PaymentActivity getPaymentActivity() {
        return (PaymentActivity) getActivity();
    }

    protected void proceed() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.proceed();
            }
        });
    }

    protected void repeat() {
        startActionSafely(new Action() {
            @Override
            public void start(PaymentActivity activity) {
                activity.repeat();
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
