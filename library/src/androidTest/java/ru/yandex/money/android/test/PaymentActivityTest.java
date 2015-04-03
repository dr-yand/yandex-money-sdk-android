package ru.yandex.money.android.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;
import com.yandex.money.api.methods.params.PhoneParams;
import com.yandex.money.api.utils.MillisecondsIn;

import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.PaymentArguments;
import ru.yandex.money.android.test.properties.LocalProperties;
import ru.yandex.money.android.test.properties.TestProperties;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.robotium.solo.By.id;
import static ru.yandex.money.android.test.espresso.MoreViewActions.waitView;
import static ru.yandex.money.android.test.espresso.MoreViewActions.waitVisibilityChange;

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
        if (shouldPerformClean()) {
            Cleaner.perform(instrumentation.getContext());
        }
        solo = new Solo(instrumentation, getActivity());
    }

    protected final Solo solo() {
        return solo;
    }

    protected String getClientId() {
        return localProperties.getClientId();
    }

    protected boolean shouldPerformClean() {
        return true;
    }

    protected void payForCard() {
        final String cardNumberId = "cardNumber";
        // waiting for WebView to load CPS page
        solo().waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return !solo().getWebElements(id(cardNumberId)).isEmpty();
            }
        }, sleep(testProperties.getNetworkTimeout()));

        // entering card details
        LocalProperties.Card card = localProperties.getCard();
        solo().enterTextInWebElement(id(cardNumberId), card.number);
        solo().enterTextInWebElement(id("month"), card.month);
        solo().enterTextInWebElement(id("year"), card.year);
        solo().enterTextInWebElement(id("cardCvc"), card.csc);
        solo().clickOnWebElement(id("mobile-cps_submit-button"));

        // waiting for success payment, may require manual user input (3D Secure for instance)
        onView(isRoot()).perform(waitView(testProperties.getManualTimeout(), R.id.ym_success));

        // checking success page and trying to save a card
        onView(withId(R.id.ym_comment))
                .check(matches(withText(getString(R.string.ym_success_comment,
                        localProperties.getAmount()))));
        onView(withId(R.id.ym_description))
                .check(matches(withText(R.string.ym_success_save_card_description)));
        onView(withId(R.id.ym_save_card))
                .check(matches(withText(R.string.ym_success_save_card)))
                .perform(click());

        // waiting for a card to be saved
        onView(withId(R.id.ym_save_card))
                .perform(waitVisibilityChange(testProperties.getNetworkTimeout(), View.GONE));

        // when a card is saved
        onView(withId(R.id.ym_success_marker))
                .check(matches(isDisplayed()));
    }

    private PaymentArguments createArguments() {
        PhoneParams params = localProperties.getPhoneParams();
        return new PaymentArguments(getClientId(), params.getPatternId(), params.makeParams());
    }

    private int sleep(int seconds) {
        return seconds * (int) MillisecondsIn.SECOND;
    }

    private String getString(int resId, Object... params) {
        return getActivity().getString(resId, params);
    }
}
