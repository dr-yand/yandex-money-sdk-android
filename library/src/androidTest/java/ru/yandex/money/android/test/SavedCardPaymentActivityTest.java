package ru.yandex.money.android.test;

import android.test.suitebuilder.annotation.LargeTest;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static ru.yandex.money.android.test.espresso.MoreViewActions.waitView;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
@LargeTest
public class SavedCardPaymentActivityTest extends PaymentActivityTest {

    public void testSavedCard() {
        waitForCards();
        clickOnListItem(0);

        onView(withId(R.id.ym_csc))
                .perform(typeText(localProperties.getCard().csc));
        onView(withId(R.id.ym_pay))
                .perform(click());

        onView(isRoot())
                .perform(waitView(testProperties.getNetworkTimeout(), R.id.ym_success));

        onView(withId(R.id.ym_card))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.ym_description))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.ym_success_marker))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.ym_save_card))
                .check(matches(not(isDisplayed())));
    }

    private void waitForCards() {
        onView(isRoot())
                .perform(waitView(testProperties.getNetworkTimeout(), android.R.id.list));
    }

    private void clickOnListItem(int position) {
        onData(anything())
                .inAdapterView(withId(android.R.id.list))
                .atPosition(position)
                .perform(click());
    }

    @Override
    protected boolean shouldPerformClean() {
        return false;
    }
}
