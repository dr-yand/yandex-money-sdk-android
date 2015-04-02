package ru.yandex.money.android.test;

import com.robotium.solo.Condition;
import com.yandex.money.api.methods.params.PhoneParams;
import com.yandex.money.api.utils.MillisecondsIn;

import ru.yandex.money.android.PaymentArguments;
import ru.yandex.money.android.test.properties.LocalProperties;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.robotium.solo.By.id;
import static ru.yandex.money.android.test.MoreViewActions.waitView;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public class NewCardPaymentActivityTest extends PaymentActivityTest {

    @Override
    protected PaymentArguments createArguments() {
        return new PaymentArguments(localProperties.getClientId(), PhoneParams.PATTERN_ID,
                localProperties.getPhoneParams().makeParams());
    }

    public void test() {
        final String cardNumberId = "cardNumber";
        solo().waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return !solo().getWebElements(id(cardNumberId)).isEmpty();
            }
        }, sleep(testProperties.getNetworkTimeout()));

        LocalProperties.Card card = localProperties.getCard();
        solo().enterTextInWebElement(id(cardNumberId), card.number);
        solo().enterTextInWebElement(id("month"), card.month);
        solo().enterTextInWebElement(id("year"), card.year);
        solo().enterTextInWebElement(id("cardCvc"), card.csc);
        solo().clickOnWebElement(id("mobile-cps_submit-button"));

        onView(isRoot()).perform(waitView(R.id.ym_comment, testProperties.getManualTimeout()));
        onView(withId(R.id.ym_comment)).check(matches(isDisplayed()));
    }

    private int sleep(int seconds) {
        return seconds * (int) MillisecondsIn.SECOND;
    }
}
