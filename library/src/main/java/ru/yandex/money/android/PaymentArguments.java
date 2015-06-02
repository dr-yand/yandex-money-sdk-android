/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 NBCO Yandex.Money LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ru.yandex.money.android;

import android.os.Bundle;

import java.util.Collections;
import java.util.Map;

import ru.yandex.money.android.utils.Bundles;

/**
 * @author vyasevich
 */
public class PaymentArguments {

    public static final String EXT_AUTH_SUCCESS_URI = "yandex-money-sdk-android://success";
    public static final String EXT_AUTH_FAIL_URI = "yandex-money-sdk-android://fail";

    private static final String EXTRA_PATTERN_ID = "ru.yandex.money.android.extra.PATTERN_ID";
    private static final String EXTRA_PARAMS = "ru.yandex.money.android.extra.PARAMS";

    private final String patternId;
    private final Map<String, String> params;

    public PaymentArguments(String patternId, Map<String, String> params) {
        this.patternId = patternId;
        this.params = params;
    }

    public PaymentArguments(Bundle bundle) {
        patternId = bundle.getString(EXTRA_PATTERN_ID);
        Bundle parameters = bundle.getBundle(EXTRA_PARAMS);
        params = Collections.unmodifiableMap(Bundles.readStringMapFromBundle(parameters));
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PATTERN_ID, patternId);
        bundle.putBundle(EXTRA_PARAMS, Bundles.writeStringMapToBundle(params));
        return bundle;
    }

    public String getPatternId() {
        return patternId;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
