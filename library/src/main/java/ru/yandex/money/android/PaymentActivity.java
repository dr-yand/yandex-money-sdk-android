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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;

import com.squareup.okhttp.Call;
import com.yandex.money.api.methods.BaseProcessPayment;
import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.methods.InstanceId;
import com.yandex.money.api.methods.ProcessExternalPayment;
import com.yandex.money.api.methods.RequestExternalPayment;
import com.yandex.money.api.methods.params.P2pParams;
import com.yandex.money.api.methods.params.PhoneParams;
import com.yandex.money.api.model.Error;
import com.yandex.money.api.model.ExternalCard;
import com.yandex.money.api.model.MoneySource;
import com.yandex.money.api.net.ApiClient;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Session;
import com.yandex.money.api.net.OnResponseReady;
import com.yandex.money.api.processes.ExternalPaymentProcess;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.fragments.CardsFragment;
import ru.yandex.money.android.fragments.CscFragment;
import ru.yandex.money.android.fragments.ErrorFragment;
import ru.yandex.money.android.fragments.SuccessFragment;
import ru.yandex.money.android.fragments.WebFragment;
import ru.yandex.money.android.parcelables.ExternalCardParcelable;
import ru.yandex.money.android.parcelables.ExternalPaymentProcessSavedStateParcelable;
import ru.yandex.money.android.utils.Keyboards;
import ru.yandex.money.android.utils.ResponseReady;

/**
 * @author vyasevich
 */
public final class PaymentActivity extends Activity {

    public static final String EXTRA_INVOICE_ID = "ru.yandex.money.android.extra.INVOICE_ID";
    public static final String EXTRA_ARGUMENTS = "ru.yandex.money.android.extra.ARGUMENTS";
    public static final String EXTRA_TEST_URL = "ru.yandex.money.android.extra.TEST_URL";

    private static final String KEY_PROCESS_SAVED_STATE = "processSavedState";
    private static final String KEY_SELECTED_CARD = "selectedCard";

    private ExternalPaymentProcess process;
    private ExternalPaymentProcess.ParameterProvider parameterProvider;
    private PaymentArguments arguments;
    private List<ExternalCard> cards;
    private ExternalCard selectedCard;
    private boolean immediateProceed = true;
    private Call call;

    public static void startActivityForResult(Activity activity, String clientId,
                                              P2pParams params, int requestCode) {

        startActivityForResult(activity, new PaymentArguments(clientId, params.getPatternId(),
                params.makeParams()), requestCode);
    }

    public static void startActivityForResult(Activity activity, String clientId,
                                              PhoneParams params, int requestCode) {

        startActivityForResult(activity, new PaymentArguments(clientId, params.getPatternId(),
                params.makeParams()), requestCode);
    }

    public static void startActivityForResult(Activity activity, String clientId, String patternId,
                                              Map<String, String> params, int requestCode) {

        startActivityForResult(activity, new PaymentArguments(clientId, patternId, params),
                requestCode);
    }

    private static void startActivityForResult(Activity activity, PaymentArguments arguments,
                                               int requestCode) {

        Intent intent = new Intent(activity, PaymentActivity.class);
        intent.putExtra(EXTRA_ARGUMENTS, arguments.toBundle());
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.ym_payment_activity);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // we hide progress bar because on some devices we have it shown right from the start
        hideProgressBar();

        arguments = new PaymentArguments(getIntent().getBundleExtra(EXTRA_ARGUMENTS));
        cards = new DatabaseStorage(this).selectMoneySources();

        boolean ready = initPaymentProcess();
        if (!ready) {
            return;
        }

        if (savedInstanceState == null) {
            proceed();
        } else {
            process.restoreSavedState(savedInstanceState
                    .<ExternalPaymentProcessSavedStateParcelable>getParcelable(
                            KEY_PROCESS_SAVED_STATE)
                    .savedState);
            if (savedInstanceState.containsKey(KEY_SELECTED_CARD)) {
                selectedCard = savedInstanceState
                        .<ExternalCardParcelable>getParcelable(KEY_SELECTED_CARD)
                        .externalCard;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PROCESS_SAVED_STATE,
                new ExternalPaymentProcessSavedStateParcelable(process.getSavedState()));
        if (selectedCard != null) {
            outState.putParcelable(KEY_SELECTED_CARD, new ExternalCardParcelable(selectedCard));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment();
        super.onBackPressed();
        cancel();

        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof CscFragment) {
            super.onBackPressed();
            currentFragment = getCurrentFragment();
        }
        if (fragment instanceof WebFragment && currentFragment instanceof CardsFragment) {
            if (cards.size() == 0) {
                immediateProceed = false;
            }
            getFragmentManager()
                    .beginTransaction()
                    .remove(currentFragment)
                    .commit();
            reset();
        }
        applyResult();
    }

    public List<ExternalCard> getCards() {
        return cards;
    }

    public void showWeb(String url, Map<String, String> postData) {
        Fragment fragment = getCurrentFragment();
        boolean clearBackStack = !(fragment instanceof CardsFragment ||
                fragment instanceof CscFragment);
        replaceFragment(WebFragment.newInstance(url, postData), clearBackStack);
    }

    public void showCards() {
        RequestExternalPayment rep = (RequestExternalPayment) process.getRequestPayment();
        replaceFragment(CardsFragment.newInstance(rep.title, rep.contractAmount), true);
    }

    public void showError(Error error, String status) {
        replaceFragment(ErrorFragment.newInstance(error, status), true);
    }

    public void showUnknownError() {
        replaceFragment(ErrorFragment.newInstance(), true);
    }

    public void showSuccess(ExternalCard moneySource) {
        replaceFragment(SuccessFragment.newInstance(process.getRequestPayment().contractAmount,
                moneySource), true);
    }

    public void showCsc(ExternalCard externalCard) {
        selectedCard = externalCard;
        replaceFragment(CscFragment.newInstance(externalCard), false);
    }

    public void showProgressBar() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void hideProgressBar() {
        setProgressBarIndeterminateVisibility(false);
    }

    public void proceed() {
        call = performOperation(new Callable<Call>() {
            @Override
            public Call call() throws Exception {
                return process.proceedAsync();
            }
        });
    }

    public void repeat() {
        call = performOperation(new Callable<Call>() {
            @Override
            public Call call() throws Exception {
                return process.repeatAsync();
            }
        });
    }

    public void reset() {
        selectedCard = null;
        process.reset();
        proceed();
    }

    public void cancel() {
        selectedCard = null;
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    private Call performOperation(Callable<Call> operation) {
        showProgressBar();
        try {
            return operation.call();
        } catch (Exception e) {
            onOperationFailed();
            return null;
        }
    }

    private boolean initPaymentProcess() {
        final String clientId = arguments.getClientId();
        final OAuth2Session session = new OAuth2Session(createApiClient(clientId));
        session.setDebugLogging(getIntent().hasExtra(EXTRA_TEST_URL));

        parameterProvider = new ExternalPaymentProcess.ParameterProvider() {
            @Override
            public String getPatternId() {
                return arguments.getPatternId();
            }

            @Override
            public Map<String, String> getPaymentParameters() {
                return arguments.getParams();
            }

            @Override
            public MoneySource getMoneySource() {
                return selectedCard;
            }

            @Override
            public String getCsc() {
                Fragment fragment = getCurrentFragment();
                return fragment instanceof CscFragment ?
                        ((CscFragment) fragment).getCsc() : null;
            }

            @Override
            public String getExtAuthSuccessUri() {
                return PaymentArguments.EXT_AUTH_SUCCESS_URI;
            }

            @Override
            public String getExtAuthFailUri() {
                return PaymentArguments.EXT_AUTH_FAIL_URI;
            }

            @Override
            public boolean isRequestToken() {
                Fragment fragment = getCurrentFragment();
                return fragment instanceof SuccessFragment;
            }
        };

        process = new ExternalPaymentProcess(session, parameterProvider);
        process.setCallbacks(new Callbacks());

        final Prefs prefs = new Prefs(this);
        String instanceId = prefs.restoreInstanceId();
        if (TextUtils.isEmpty(instanceId)) {
            call = performOperation(new Callable<Call>() {
                @Override
                public Call call() throws Exception {
                    return session.enqueue(new InstanceId.Request(clientId),
                            new ResponseReady<InstanceId>() {

                        @Override
                        public void failure(Exception exception) {
                            exception.printStackTrace();
                            onOperationFailed();
                        }

                        @Override
                        public void response(InstanceId response) {
                            if (response.isSuccess()) {
                                prefs.storeInstanceId(response.instanceId);
                                process.setInstanceId(response.instanceId);
                                proceed();
                            } else {
                                showError(response.error, response.status.code);
                            }
                            hideProgressBar();
                        }
                    });
                }
            });
            return false;
        }

        process.setInstanceId(instanceId);
        return true;
    }

    private ApiClient createApiClient(String clientId) {
        String testUrl = getIntent().getStringExtra(EXTRA_TEST_URL);
        return TextUtils.isEmpty(testUrl) ? new DefaultApiClient(clientId) :
                new TestApiClient(clientId, testUrl);
    }

    private void onExternalPaymentReceived(RequestExternalPayment rep) {
        if (rep.status == BaseRequestPayment.Status.SUCCESS) {
            if (immediateProceed && cards.size() == 0) {
                proceed();
            } else {
                showCards();
            }
        } else {
            showError(rep.error, rep.status.code);
        }
    }

    private void onExternalPaymentProcessed(ProcessExternalPayment pep) {
        switch (pep.status) {
            case SUCCESS:
                Fragment fragment = getCurrentFragment();
                if (!(fragment instanceof SuccessFragment)) {
                    showSuccess((ExternalCard) parameterProvider.getMoneySource());
                } else if (pep.moneySource != null) {
                    ((SuccessFragment) fragment).saveCard(pep.moneySource);
                }
                break;
            case EXT_AUTH_REQUIRED:
                showWeb(pep.acsUri, pep.acsParams);
                break;
            default:
                showError(pep.error, pep.status.code);
        }
    }

    private void onOperationFailed() {
        showUnknownError();
        hideProgressBar();
    }

    private void replaceFragment(Fragment fragment, boolean clearBackStack) {
        if (fragment == null) {
            return;
        }

        Fragment currentFragment = getCurrentFragment();
        FragmentManager manager = getFragmentManager();
        if (clearBackStack) {
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        @SuppressLint("CommitTransaction")
        FragmentTransaction transaction = manager.beginTransaction()
                .replace(R.id.ym_container, fragment);
        if (!clearBackStack && currentFragment != null) {
            transaction.addToBackStack(fragment.getTag());
        }
        transaction.commit();
        hideKeyboard();
    }

    private Fragment getCurrentFragment() {
        return getFragmentManager().findFragmentById(R.id.ym_container);
    }

    private void hideKeyboard() {
        Keyboards.hideKeyboard(this);
    }

    private void applyResult() {
        BaseProcessPayment pp = process.getProcessPayment();
        if (pp != null && pp.status == BaseProcessPayment.Status.SUCCESS) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_INVOICE_ID, pp.invoiceId);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
    }

    private final class Callbacks implements ExternalPaymentProcess.Callbacks {

        private final OnResponseReady<RequestExternalPayment> requestReady =
                new ResponseReady<RequestExternalPayment>() {

                    @Override
                    public void failure(Exception exception) {
                        onOperationFailed();
                    }

                    @Override
                    public void response(RequestExternalPayment response) {
                        onExternalPaymentReceived(response);
                        hideProgressBar();
                    }
                };

        private final OnResponseReady<ProcessExternalPayment> processReady =
                new ResponseReady<ProcessExternalPayment>() {

                    @Override
                    public void failure(Exception exception) {
                        onOperationFailed();
                    }

                    @Override
                    public void response(ProcessExternalPayment response) {
                        onExternalPaymentProcessed(response);
                        hideProgressBar();
                    }
                };

        @Override
        public OnResponseReady<RequestExternalPayment> getOnRequestCallback() {
            return requestReady;
        }

        @Override
        public OnResponseReady<ProcessExternalPayment> getOnProcessCallback() {
            return processReady;
        }
    }
}
