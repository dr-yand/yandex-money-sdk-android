package ru.yandex.money.android.test;

import android.test.suitebuilder.annotation.LargeTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
@LargeTest
public final class NewCardPaymentActivityTest extends PaymentActivityTest {

    public void test() {
        // waiting for WebFragment to be attached to PaymentActivity
        solo().waitForFragmentById(R.id.ym_container);
        // check that there was no error
        onView(withId(R.id.ym_error_title)).check(doesNotExist());

        payForCard();
    }
}
