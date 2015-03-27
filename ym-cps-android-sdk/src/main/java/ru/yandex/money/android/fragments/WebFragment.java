package ru.yandex.money.android.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yandex.money.api.methods.ProcessExternalPayment;
import com.yandex.money.api.model.Error;
import com.yandex.money.api.model.ExternalCard;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import ru.yandex.money.android.R;
import ru.yandex.money.android.parcelables.ExtendedCardParcelable;
import ru.yandex.money.android.parcelables.ProcessExternalPaymentParcelable;
import ru.yandex.money.android.services.DataServiceHelper;

/**
 * @author vyasevich
 */
public class WebFragment extends PaymentFragment {

    private static final String EXTRA_PROCESS_EXTERNAL_PAYMENT = "ru.yandex.money.android.extra.PROCESS_EXTERNAL_PAYMENT";

    private WebView webView;

    private String requestId;
    private ProcessExternalPayment pep;
    private ExternalCard moneySource;

    public static WebFragment newInstance(String requestId) {
        return newInstance(requestId, null, null);
    }

    public static WebFragment newInstance(String requestId, ProcessExternalPayment pep,
                                          ExternalCard moneySource) {

        Bundle args = new Bundle();
        args.putString(EXTRA_REQUEST_ID, requestId);
        if (pep != null) {
            args.putParcelable(EXTRA_PROCESS_EXTERNAL_PAYMENT,
                    new ProcessExternalPaymentParcelable(pep));
        }
        if (moneySource != null) {
            args.putParcelable(EXTRA_MONEY_SOURCE, new ExtendedCardParcelable(moneySource));
        }

        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        assert args != null : "specify proper args for WebFragment";

        requestId = args.getString(EXTRA_REQUEST_ID);
        ProcessExternalPaymentParcelable pepParcelable =
                args.getParcelable(EXTRA_PROCESS_EXTERNAL_PAYMENT);
        if (pepParcelable != null) {
            pep = pepParcelable.getProcessExternalPayment();
        }
        ExtendedCardParcelable extendedCardParcelable = args.getParcelable(EXTRA_MONEY_SOURCE);
        if (extendedCardParcelable != null) {
            moneySource = extendedCardParcelable.getExtendedCard();
        }
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
    public void onResume() {
        super.onResume();
        if (pep == null) {
            processExternalPayment();
        } else {
            loadPage(pep);
        }
    }

    @Override
    protected void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        super.onExternalPaymentProcessed(pep);
        switch (pep.status) {
            case SUCCESS:
                showSuccess(moneySource);
                break;
            case EXT_AUTH_REQUIRED:
                loadPage(pep);
                break;
            default:
                showError(pep.error, pep.status.toString());
        }
    }

    private void loadPage(ProcessExternalPayment pep) {
        showWebView();
        webView.postUrl(pep.acsUri, buildPostData(pep));
    }

    private void showProgress() {
        showProgressBar();
        webView.setVisibility(View.GONE);
    }

    private void showWebView() {
        hideProgressBar();
        webView.setVisibility(View.VISIBLE);
    }

    private void processExternalPayment() {
        reqId = getPaymentActivity().getDataServiceHelper().process(requestId, false);
    }

    private byte[] buildPostData(ProcessExternalPayment pep) {
        String url = "";
        for (Map.Entry<String, String> entry : pep.acsParams.entrySet()) {
            url += entry.getKey() + "=" + safeUrlEncoding(entry.getValue()) + "&";
        }
        return EncodingUtils.getBytes(url, "BASE64");
    }

    private String safeUrlEncoding(String value) {
        try {
            return URLEncoder.encode(value, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private class Client extends WebViewClient {

        private static final String TAG = "WebViewClient";

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "page started " + url);
            if (url.contains(DataServiceHelper.SUCCESS_URI)) {
                showProgress();
                if (isAdded()) {
                    processExternalPayment();
                }
            } else if (url.contains(DataServiceHelper.FAIL_URI)) {
                showError(Error.AUTHORIZATION_REJECT, null);
            }
        }
    }

    private class Chrome extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Log.d("Chrome", "progress = " + newProgress);
            if (newProgress == 0) {
                showProgressBar();
            } else if (newProgress == 100) {
                hideProgressBar();
            }
        }
    }
}
