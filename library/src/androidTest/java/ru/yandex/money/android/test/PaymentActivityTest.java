package ru.yandex.money.android.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.yandex.money.api.methods.params.PhoneParams;

import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.PaymentArguments;
import ru.yandex.money.android.test.properties.LocalProperties;
import ru.yandex.money.android.test.properties.TestProperties;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
abstract class PaymentActivityTest extends ActivityInstrumentationTestCase2<PaymentActivity> {

    private static final String EXTRA_ARGUMENTS = "ru.yandex.money.android.extra.ARGUMENTS";
    private static final String EXTRA_TEST_URL = "ru.yandex.money.android.extra.TEST_URL";

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
        intent.putExtra(EXTRA_TEST_URL, localProperties.getHostUrl());
        setActivityIntent(intent);

        Instrumentation instrumentation = getInstrumentation();
        Cleaner.perform(instrumentation.getContext());
        solo = new Solo(instrumentation, getActivity());
    }

    protected final Solo solo() {
        return solo;
    }

    protected String getClientId() {
        return localProperties.getClientId();
    }

    private PaymentArguments createArguments() {
        PhoneParams params = localProperties.getPhoneParams();
        return new PaymentArguments(getClientId(), params.getPatternId(), params.makeParams());
    }
}
