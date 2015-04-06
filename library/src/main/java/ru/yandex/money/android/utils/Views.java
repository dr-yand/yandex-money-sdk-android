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

package ru.yandex.money.android.utils;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author vyasevich
 */
public class Views {

    public static void setText(View container, int viewId, String text) {
        TextView textView = (TextView) container.findViewById(viewId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    public static String getTextSafely(EditText editText) {
        Editable text = editText.getText();
        return text == null ? null : text.toString();
    }

    public static void setImageResource(View container, int viewId, int resId) {
        ImageView imageView = (ImageView) container.findViewById(viewId);
        if (imageView != null) {
            imageView.setImageResource(resId);
        }
    }

    public static void setVisibility(View container, int viewId, int visibility) {
        View view = container.findViewById(viewId);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }
}
