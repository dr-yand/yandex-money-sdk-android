package ru.yandex.money.android.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    private void showProgress() {
        showProgressBar();
        webView.setVisibility(View.GONE);
    }

    private void showWebView() {
        hideProgressBar();
        webView.setVisibility(View.VISIBLE);
    }

    private byte[] buildPostData(Map<String, String> postParams) {
        String url = "";
        for (Map.Entry<String, String> entry : postParams.entrySet()) {
            url += entry.getKey() + "=" + safeUrlEncoding(entry.getValue()) + "&";
        }
        //noinspection deprecation
        return EncodingUtils.getBytes(url, "BASE64");
    }

    private String safeUrlEncoding(String value) {
        try {
            //noinspection deprecation
            return URLEncoder.encode(value, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private class Client extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("WebViewClient", "page started " + url);
            if (url.contains(PaymentArguments.EXT_AUTH_SUCCESS_URI)) {
                showProgress();
                proceed();
            } else if (url.contains(PaymentArguments.EXT_AUTH_FAIL_URI)) {
                showError(Error.AUTHORIZATION_REJECT, null);
            }
        }
    }

    private class Chrome extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Log.d("Chrome", "progress = " + newProgress);
            showProgressBar();
            if (newProgress == 100) {
                hideProgressBar();
            }
        }
    }
}
