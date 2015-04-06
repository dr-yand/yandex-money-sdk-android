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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author vyasevich
 */
public class MultipleBroadcastReceiver extends BroadcastReceiver {

    private final Map<String, IntentHandler> handlers = new HashMap<String, IntentHandler>();

    @Override
    public void onReceive(Context context, Intent intent) {
        IntentHandler handler = handlers.get(intent.getAction());
        if (handler != null) {
            handler.handle(intent);
        }
    }

    public MultipleBroadcastReceiver addHandler(String action, IntentHandler handler) {
        handlers.put(action, handler);
        return this;
    }

    public IntentFilter buildIntentFilter() {
        IntentFilter filter = new IntentFilter();
        Set<String> actions = handlers.keySet();
        for (String action : actions) {
            filter.addAction(action);
        }
        return filter;
    }
}
