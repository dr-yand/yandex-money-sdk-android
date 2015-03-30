package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yandex.money.api.model.ExternalCard;
import com.yandex.money.api.model.MoneySource;

import ru.yandex.money.android.R;
import ru.yandex.money.android.formatters.MoneySourceFormatter;
import ru.yandex.money.android.parcelables.ExtendedCardParcelable;
import ru.yandex.money.android.utils.CardType;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class CscFragment extends PaymentFragment {

    private ExternalCard moneySource;
    private String csc;

    private LinearLayout error;
    private TextView errorTitle;
    private TextView errorMessage;
    private EditText cscEditText;
    private Button cancel;
    private Button pay;

    public static CscFragment newInstance(ExternalCard moneySource) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_MONEY_SOURCE, new ExtendedCardParcelable(moneySource));

        CscFragment fragment = new CscFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        assert args != null : "provide correct arguments for CscFragment";

        ExtendedCardParcelable extendedCardParcelable = args.getParcelable(KEY_MONEY_SOURCE);
        assert extendedCardParcelable != null : "provide money source for CscFragment";

        moneySource = extendedCardParcelable.getExtendedCard();
        CardType cardType = CardType.get(moneySource.type);

        View view = inflater.inflate(R.layout.ym_csc_fragment, container, false);
        assert view != null : "unable to inflate view in CscFragment";

        error = (LinearLayout) view.findViewById(R.id.ym_error);
        errorTitle = (TextView) view.findViewById(R.id.ym_error_title);
        errorMessage = (TextView) view.findViewById(R.id.ym_error_message);

        cscEditText = (EditText) view.findViewById(R.id.ym_csc);
        cscEditText.setHint(getString(R.string.ym_csc_code, moneySource.type.cscAbbr));
        cscEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(moneySource.type.cscLength)});

        Views.setText(view, R.id.ym_csc_hint, getString(R.string.ym_csc_hint,
                getString(MoneySourceFormatter.getCscNumberType(cardType)),
                getString(MoneySourceFormatter.getCscNumberLocation(cardType))));

        cancel = (Button) view.findViewById(R.id.ym_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });

        pay = (Button) view.findViewById(R.id.ym_pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPayClicked();
            }
        });

        return view;
    }

    public MoneySource getMoneySource() {
        return moneySource;
    }

    public String getCsc() {
        return csc;
    }

    private void setErrorGone() {
        setError(View.GONE, null, null);
    }

    private void setErrorVisible(String title, String message) {
        setError(View.VISIBLE, title, message);
    }

    private void setError(int visibility, String title, String message) {
        error.setVisibility(visibility);
        errorTitle.setText(title);
        errorMessage.setText(message);
    }

    private void onCancelClicked() {
        showCards();
    }

    private void onPayClicked() {
        if (valid()) {
            setErrorGone();
            cancel.setEnabled(false);
            pay.setEnabled(false);
            cscEditText.setEnabled(false);
            proceed();
        } else {
            setErrorVisible(getString(R.string.ym_error_oops_title),
                    getString(R.string.ym_error_csc_invalid));
        }
    }

    private boolean valid() {
        csc = Views.getTextSafely(cscEditText);
        return csc != null && csc.length() == moneySource.type.cscLength;
    }
}
