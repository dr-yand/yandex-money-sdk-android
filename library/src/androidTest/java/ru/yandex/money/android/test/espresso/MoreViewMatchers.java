package ru.yandex.money.android.test.espresso;

import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public final class MoreViewMatchers {

    private MoreViewMatchers() {
    }

    public static SimpleViewMatcher simpleViewMatcher() {
        return new SimpleViewMatcher();
    }

    private static final class SimpleViewMatcher extends BaseMatcher<View> {

        @Override
        public boolean matches(Object o) {
            return true;
        }

        @Override
        public void describeTo(Description description) {
            // does nothing
        }
    }
}
