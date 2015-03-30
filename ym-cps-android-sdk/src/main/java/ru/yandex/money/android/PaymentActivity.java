package ru.yandex.money.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Session;
import com.yandex.money.api.processes.BasePaymentProcess;
import com.yandex.money.api.processes.ExternalPaymentProcess;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.fragments.CardsFragment;
import ru.yandex.money.android.fragments.CscFragment;
import ru.yandex.money.android.fragments.ErrorFragment;
import ru.yandex.money.android.fragments.SuccessFragment;
import ru.yandex.money.android.fragments.WebFragment;
import ru.yandex.money.android.utils.Keyboards;

/**
 * @author vyasevich
 */
public class PaymentActivity extends Activity {

    public static final String EXTRA_INVOICE_ID = "ru.yandex.money.android.extra.INVOICE_ID";

    private static final String EXTRA_ARGUMENTS = "ru.yandex.money.android.extra.ARGUMENTS";

    private ExternalPaymentProcess process;
    private PaymentArguments arguments;
    private List<ExternalCard> cards;

    public static void startActivityForResult(Activity activity, String clientId,
                                              P2pParams params, int requestCode) {

        startActivityForResult(activity, new PaymentArguments(clientId, P2pParams.PATTERN_ID,
                params.makeParams()), requestCode);
    }

    public static void startActivityForResult(Activity activity, String clientId,
                                              PhoneParams params, int requestCode) {

        startActivityForResult(activity, new PaymentArguments(clientId, PhoneParams.PATTERN_ID,
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

        arguments = new PaymentArguments(getIntent().getBundleExtra(EXTRA_ARGUMENTS));
        cards = new DatabaseStorage(this).selectMoneySources();

        initPaymentProcess();
        if (savedInstanceState == null) {
            proceed();
        } else {
            // TODO restore state
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        applyResult();
        hideProgressBar();
        super.onBackPressed();
    }

    public List<ExternalCard> getCards() {
        return cards;
    }

    public void showWeb(String url, Map<String, String> postData) {
        replaceFragmentAddingToBackStack(WebFragment.newInstance(url, postData));
    }

    public void showCards() {
        RequestExternalPayment rep = (RequestExternalPayment) process.getRequestPayment();
        replaceFragmentClearBackStack(CardsFragment.newInstance(rep.title, rep.contractAmount));
    }

    public void showError(Error error, String status) {
        replaceFragmentClearBackStack(ErrorFragment.newInstance(error, status));
    }

    public void showUnknownError() {
        replaceFragmentClearBackStack(ErrorFragment.newInstance());
    }

    public void showSuccess(ExternalCard moneySource) {
        BaseRequestPayment rp = process.getRequestPayment();
        replaceFragmentClearBackStack(SuccessFragment.newInstance(rp.requestId, rp.contractAmount,
                moneySource));
    }

    public void showCsc(ExternalCard moneySource) {
        replaceFragmentAddingToBackStack(CscFragment.newInstance(moneySource));
    }

    public void showProgressBar() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void hideProgressBar() {
        setProgressBarIndeterminateVisibility(false);
    }

    public void proceed() {
        performOperation(new Callable<Call>() {
            @Override
            public Call call() throws Exception {
                return process.proceedAsync();
            }
        });
    }

    public void repeat() {
        performOperation(new Callable<Call>() {
            @Override
            public Call call() throws Exception {
                return process.repeatAsync();
            }
        });
    }

    private void performOperation(Callable<Call> operation) {
        showProgressBar();
        try {
            operation.call();
        } catch (Exception e) {
            onOperationFailed();
        }
    }

    private void initPaymentProcess() {
        String clientId = arguments.getClientId();
        OAuth2Session session = new OAuth2Session(new DefaultApiClient(clientId));

        final Prefs prefs = new Prefs(this);
        String instanceId = prefs.restoreInstanceId();
        if (TextUtils.isEmpty(instanceId)) {
            showProgressBar();
            try {
                session.enqueue(new InstanceId.Request(clientId),
                        new OAuth2Session.OnResponseReady<InstanceId>() {

                            @Override
                            public void onFailure(Exception exception) {
                                onOperationFailed();
                            }

                            @Override
                            public void onResponse(InstanceId response) {
                                if (response.isSuccess()) {
                                    prefs.storeInstanceId(response.instanceId);
                                    initPaymentProcess();
                                } else {
                                    showError(response.error, response.status.code);
                                }
                                hideProgressBar();
                            }
                });
            } catch (IOException e) {
                onOperationFailed();
            }
            return;
        }

        process = new ExternalPaymentProcess(session, new BasePaymentProcess.ParameterProvider() {
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
                return getCscFragment().getMoneySource();
            }

            @Override
            public String getCsc() {
                return getCscFragment().getCsc();
            }

            @Override
            public String getExtAuthSuccessUri() {
                return PaymentArguments.EXT_AUTH_SUCCESS_URI;
            }

            @Override
            public String getExtAuthFailUri() {
                return PaymentArguments.EXT_AUTH_FAIL_URI;
            }
        });

        process.setInstanceId(instanceId);
        process.setCallbacks(new Callbacks());
    }

    private void onExternalPaymentReceived(RequestExternalPayment rep) {
        if (rep.status == BaseRequestPayment.Status.SUCCESS) {
            if (cards.size() == 0) {
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
                showSuccess(pep.moneySource);
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

    private void replaceFragmentClearBackStack(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        hideProgressBar();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.ym_container, fragment)
                .commit();
        hideKeyboard();
    }

    private void replaceFragmentAddingToBackStack(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        hideProgressBar();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.ym_container, fragment)
                .addToBackStack(fragment.getTag())
                .commit();
        hideKeyboard();
    }

    private CscFragment getCscFragment() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.ym_container);
        if (fragment instanceof CscFragment) {
            return (CscFragment) fragment;
        } else {
            throw new IllegalStateException("current fragment: " + fragment);
        }
    }

    private void hideKeyboard() {
        Keyboards.hideKeyboard(this);
    }

    private void applyResult() {
        BaseProcessPayment pp = process.getProcessPayment();
        if (pp.status == BaseProcessPayment.Status.SUCCESS) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_INVOICE_ID, pp.invoiceId);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
    }

    private final class Callbacks implements ExternalPaymentProcess.Callbacks {

        private final OAuth2Session.OnResponseReady<RequestExternalPayment> requestReady =
                new OAuth2Session.OnResponseReady<RequestExternalPayment>() {

                    @Override
                    public void onFailure(Exception exception) {
                        onOperationFailed();
                    }

                    @Override
                    public void onResponse(RequestExternalPayment response) {
                        onExternalPaymentReceived(response);
                        hideProgressBar();
                    }
                };

        private final OAuth2Session.OnResponseReady<ProcessExternalPayment> processReady =
                new OAuth2Session.OnResponseReady<ProcessExternalPayment>() {

                    @Override
                    public void onFailure(Exception exception) {
                        onOperationFailed();
                    }

                    @Override
                    public void onResponse(ProcessExternalPayment response) {
                        onExternalPaymentProcessed(response);
                        hideProgressBar();
                    }
                };

        @Override
        public OAuth2Session.OnResponseReady<RequestExternalPayment> getOnRequestCallback() {
            return requestReady;
        }

        @Override
        public OAuth2Session.OnResponseReady<ProcessExternalPayment> getOnProcessCallback() {
            return processReady;
        }
    }
}
