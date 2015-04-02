package ru.yandex.money.android.test;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.view.View;

import com.yandex.money.api.utils.MillisecondsIn;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeoutException;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
final class MoreViewActions {

    private MoreViewActions() {
    }

    public static WaitView waitView(int id, int seconds) {
        return new WaitView(id, seconds * MillisecondsIn.SECOND);
    }

    public static final class WaitView implements ViewAction {

        private final int id;
        private final long duration;

        public WaitView(int id, long duration) {
            this.id = id;
            this.duration = duration;
        }

        @Override
        public Matcher<View> getConstraints() {
            return ViewMatchers.isRoot();
        }

        @Override
        public String getDescription() {
            return "wait for viewId=" + id + " for " + duration + " ms";
        }

        @Override
        public void perform(UiController uiController, View view) {
            uiController.loopMainThreadUntilIdle();

            Matcher<View> matcher = ViewMatchers.withId(id);
            long finishTime = System.currentTimeMillis() + duration;

            while (System.currentTimeMillis() < finishTime) {
                for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                    if (matcher.matches(child)) {
                        return;
                    }
                }
                uiController.loopMainThreadForAtLeast(MillisecondsIn.SECOND);
            }

            throw new PerformException.Builder()
                    .withActionDescription(this.getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(new TimeoutException())
                    .build();
        }
    }
}
