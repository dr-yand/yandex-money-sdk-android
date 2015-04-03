package ru.yandex.money.android.test;

import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.robotium.solo.Condition;
import com.yandex.money.api.utils.MillisecondsIn;

import ru.yandex.money.android.test.properties.LocalProperties;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
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
@LargeTest
public final class NewCardPaymentActivityTest extends PaymentActivityTest {

    public void test() {
        final String cardNumberId = "cardNumber";
        // waiting for WebFragment to be attached to PaymentActivity
        solo().waitForFragmentById(R.id.ym_container);
        // check that there was no error
        onView(withId(R.id.ym_error_title)).check(doesNotExist());

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

    private int sleep(int seconds) {
        return seconds * (int) MillisecondsIn.SECOND;
    }

    private String getString(int resId, Object... params) {
        return getActivity().getString(resId, params);
    }
}
