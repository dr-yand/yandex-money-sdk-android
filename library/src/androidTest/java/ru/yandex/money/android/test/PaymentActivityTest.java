package ru.yandex.money.android.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.PaymentArguments;
import ru.yandex.money.android.test.properties.LocalProperties;
import ru.yandex.money.android.test.properties.TestProperties;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
abstract class PaymentActivityTest extends ActivityInstrumentationTestCase2<PaymentActivity> {

    private static final String EXTRA_ARGUMENTS = "ru.yandex.money.android.extra.ARGUMENTS";

    protected final LocalProperties localProperties = new LocalProperties();
    protected final TestProperties testProperties = new TestProperties();

    private Solo solo;

    public PaymentActivityTest() {
        super(PaymentActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ARGUMENTS, createArguments().toBundle());
        setActivityIntent(intent);

        Instrumentation instrumentation = getInstrumentation();
        Cleaner.perform(instrumentation.getContext());
        solo = new Solo(instrumentation, getActivity());
    }

    protected abstract PaymentArguments createArguments();

    protected final Solo solo() {
        return solo;
    }
}
