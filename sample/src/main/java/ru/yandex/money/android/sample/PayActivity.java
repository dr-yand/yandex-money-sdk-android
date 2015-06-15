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

package ru.yandex.money.android.sample;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.methods.params.PaymentParams;
import com.yandex.money.api.methods.params.PhoneParams;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.android.sample.storage.DatabaseHelper;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class PayActivity extends ListActivity {

    private static final int REQUEST_CODE = 101;

    private static final String EXTRA_PAYMENT = "ru.yandex.money.android.sample.extra.PAYMENT";

    private Payment payment;
    private DatabaseHelper helper;

    private EditText paymentTo;
    private EditText amount;
    private TextView previous;

    public static void startP2P(Context context) {
        startActivity(context, Payment.P2P);
    }

    public static void startPhone(Context context) {
        startActivity(context, Payment.PHONE);
    }

    private static void startActivity(Context context, Payment payment) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(EXTRA_PAYMENT, payment);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        payment = (Payment) getIntent().getSerializableExtra(EXTRA_PAYMENT);
        helper = DatabaseHelper.getInstance(this);

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK &&
                !loadValues().contains(getPaymentTo())) {
            switch (payment) {
                case P2P:
                    helper.saveAccountNumber(getPaymentTo());
                    break;
                case PHONE:
                    helper.savePhoneNumber(getPaymentTo());
                    break;
            }
            updatePrevious();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pay, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.pay:
                pay();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        paymentTo.setText((String) l.getItemAtPosition(position));
    }

    private void init() {
        paymentTo = (EditText) findViewById(R.id.payment_to);
        switch (payment) {
            case P2P:
                paymentTo.setHint(R.string.activity_pay_account_hint);
                break;
            case PHONE:
                paymentTo.setHint(R.string.activity_pay_phone_hint);
                break;
        }

        amount = (EditText) findViewById(R.id.amount);

        previous = (TextView) findViewById(R.id.previous);
        updatePrevious();
    }

    private void updatePrevious() {
        List<String> values = prepareValues(loadValues());
        if (values.isEmpty()) {
            setListAdapter(null);
            getListView().setVisibility(View.GONE);
            previous.setVisibility(View.GONE);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
            getListView().setVisibility(View.VISIBLE);
            previous.setVisibility(View.VISIBLE);
        }
    }

    private List<String> prepareValues(List<String> values) {
        if (payment == Payment.PHONE) {
            for (int i = 0; i < values.size(); ++i) {
                String number = PhoneNumberUtils.formatNumber("+" + values.get(i));
                values.remove(i);
                values.add(i, number);
            }
        }
        return values;
    }

    private List<String> loadValues() {
        switch (payment) {
            case P2P:
                return helper.getAccountNumbers();
            case PHONE:
                return helper.getPhoneNumber();
            default:
                throw new IllegalArgumentException();
        }
    }

    private void pay() {
        if (isValid()) {
            switch (payment) {
                case P2P:
                    startPaymentActivityForResult(new P2pTransferParams.Builder(
                            getPaymentTo()).setAmount(getAmount()).build());
                    break;
                case PHONE:
                    startPaymentActivityForResult(PhoneParams.newInstance(getPaymentTo(),
                            getAmount()));
                    break;
            }
        } else {
            Toast.makeText(this, R.string.activity_pay_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private void startPaymentActivityForResult(PaymentParams paymentParams) {
        ApiData apiData = ApiData.getFromProperties(this);
        Intent intent = PaymentActivity.getBuilder(this).setPaymentParams(paymentParams)
                .setClientId(apiData.clientId).setHost(apiData.host).build();
        startActivityForResult(intent, REQUEST_CODE);
    }

    private String getPaymentTo() {
        return Views.getTextSafely(paymentTo).replaceAll("\\D", "");
    }

    private BigDecimal getAmount() {
        return new BigDecimal(Views.getTextSafely(amount));
    }

    private boolean isValid() {
        return !TextUtils.isEmpty(Views.getTextSafely(paymentTo)) &&
                !TextUtils.isEmpty(Views.getTextSafely(amount)) && getAmount().doubleValue() > 0;
    }

    private static class ApiData {

        public final String clientId;
        public final String host;

        public static ApiData getFromProperties(Context context) {
            Properties prop = loadProperties(context);
            return new ApiData(prop.getProperty("client_id"), prop.getProperty("host"));
        }

        private ApiData(String clientId, String host) {
            this.clientId = clientId;
            this.host = host;
        }

        private static Properties loadProperties(Context context) {
            InputStream is = null;
            try {
                is = context.getAssets().open("app.properties");
                Properties prop = new Properties();
                prop.load(is);
                return prop;
            } catch (IOException e) {
                throw new IllegalStateException("no properties file found", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // does nothing
                    }
                }
            }
        }
    }

    private enum Payment {
        P2P,
        PHONE
    }
}
