package ru.yandex.money.android.test.espresso;

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
public final class MoreViewActions {

    private MoreViewActions() {
    }

    public static WaitAction waitView(int seconds, int id) {
        return new WaitView(duration(seconds), id);
    }

    public static WaitAction waitVisibilityChange(int seconds, int visibility) {
        return new WaitVisibilityChange(duration(seconds), visibility);
    }

    private static long duration(int seconds) {
        return seconds * MillisecondsIn.SECOND;
    }

    private static final class WaitView extends WaitAction {

        private final Matcher<View> matcher;

        public WaitView(long duration, int id) {
            super(duration);
            matcher = ViewMatchers.withId(id);
        }

        @Override
        public Matcher<View> getConstraints() {
            return ViewMatchers.isRoot();
        }

        @Override
        public String getDescription() {
            return "waiting for a view" + super.getDescription();
        }

        @Override
        protected boolean isConditionMet(View view) {
            for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                if (matcher.matches(child)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final class WaitVisibilityChange extends WaitAction {

        private final int visibility;

        public WaitVisibilityChange(long duration, int visibility) {
            super(duration);
            if (visibility != View.GONE && visibility != View.INVISIBLE &&
                    visibility != View.VISIBLE) {
                throw new IllegalArgumentException("wrong visibility: " + visibility);
            }
            this.visibility = visibility;
        }

        @Override
        public Matcher<View> getConstraints() {
            return MoreViewMatchers.simpleViewMatcher();
        }

        @Override
        public String getDescription() {
            String text;
            switch (visibility) {
                case View.GONE:
                    text = "gone";
                    break;
                case View.INVISIBLE:
                    text = "invisible";
                    break;
                default:
                    text = "visible";
            }
            return "waiting for view to become " + text + super.getDescription();
        }

        @Override
        protected boolean isConditionMet(View view) {
            return view.getVisibility() == visibility;
        }
    }

    private static abstract class WaitAction implements ViewAction {

        private final long duration;

        public WaitAction(long duration) {
            if (duration < 0) {
                throw new IllegalArgumentException("duration is negative: " + duration);
            }
            this.duration = duration;
        }

        @Override
        public String getDescription() {
            return " for " + duration + " ms";
        }

        @Override
        public final void perform(UiController uiController, View view) {
            uiController.loopMainThreadUntilIdle();

            long finishTime = System.currentTimeMillis() + duration;
            while (System.currentTimeMillis() < finishTime) {
                if (isConditionMet(view)) {
                    return;
                }
                uiController.loopMainThreadForAtLeast(MillisecondsIn.SECOND);
            }

            throw new PerformException.Builder()
                    .withActionDescription(this.getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(new TimeoutException())
                    .build();
        }

        protected abstract boolean isConditionMet(View view);
    }
}
