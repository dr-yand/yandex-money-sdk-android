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

package ru.yandex.money.android.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yandex.money.api.model.Error;
import com.yandex.money.api.net.ParametersBuffer;

import java.util.Map;

import ru.yandex.money.android.PaymentArguments;
import ru.yandex.money.android.R;
import ru.yandex.money.android.utils.Bundles;

/**
 * @author vyasevich
 */
public final class WebFragment extends PaymentFragment {

    private static final String KEY_URL = "uri";
    private static final String KEY_POST_DATA = "postData";

    private WebView webView;

    public static WebFragment newInstance(String url, Map<String, String> postData) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url is null or empty");
        }
        if (postData == null) {
            throw new NullPointerException("postData is null");
        }

        Bundle args = new Bundle();
        args.putString(KEY_URL, url);
        args.putBundle(KEY_POST_DATA, Bundles.writeStringMapToBundle(postData));

        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        webView = (WebView) inflater.inflate(R.layout.ym_web_fragment, container, false);
        webView.setWebViewClient(new Client());
        webView.setWebChromeClient(new Chrome());
        webView.getSettings().setJavaScriptEnabled(true);
        return webView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        loadPage(args.getString(KEY_URL), Bundles.readStringMapFromBundle(
                args.getBundle(KEY_POST_DATA)));
    }

    private void loadPage(String url, Map<String, String> postParams) {
        showWebView();
        webView.postUrl(url, buildPostData(postParams));
    }

    private void hideWebView() {
        webView.setVisibility(View.GONE);
    }

    private void showWebView() {
        webView.setVisibility(View.VISIBLE);
    }

    private byte[] buildPostData(Map<String, String> postParams) {
        return new ParametersBuffer()
                .setParams(postParams)
                .prepareBytes();
    }

    private class Client extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("WebViewClient", "loading " + url);
            boolean completed = false;
            if (url.contains(PaymentArguments.EXT_AUTH_SUCCESS_URI)) {
                completed = true;
                hideWebView();
                proceed();
            } else if (url.contains(PaymentArguments.EXT_AUTH_FAIL_URI)) {
                completed = true;
                showError(Error.AUTHORIZATION_REJECT, null);
            }
            if (completed) {
                hideProgressBar();
            }
            return completed || super.shouldOverrideUrlLoading(view, url);
        }
    }

    private class Chrome extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Log.d("WebChromeClient", "progress = " + newProgress);
            if (newProgress == 100) {
                hideProgressBar();
            } else {
                showProgressBar();
            }
        }
    }
}
