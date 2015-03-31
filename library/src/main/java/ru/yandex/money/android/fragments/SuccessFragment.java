package ru.yandex.money.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yandex.money.api.model.ExternalCard;

import java.math.BigDecimal;

import ru.yandex.money.android.R;
import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.formatters.MoneySourceFormatter;
import ru.yandex.money.android.parcelables.ExternalCardParcelable;
import ru.yandex.money.android.utils.CardType;
import ru.yandex.money.android.utils.Views;

/**
 * @author vyasevich
 */
public class SuccessFragment extends PaymentFragment {

    private static final String KEY_CONTRACT_AMOUNT = "contractAmount";

    private ExternalCard moneySource;

    private View card;
    private Button saveCard;
    private View successMarker;
    private TextView description;

    public static SuccessFragment newInstance(BigDecimal contractAmount, ExternalCard moneySource) {
        Bundle args = new Bundle();
        args.putString(KEY_CONTRACT_AMOUNT, contractAmount.toPlainString());
        if (moneySource != null) {
            args.putParcelable(KEY_MONEY_SOURCE, new ExternalCardParcelable(moneySource));
        }

        SuccessFragment frg = new SuccessFragment();
        frg.setArguments(args);
        return frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ym_success_fragment, container, false);
        assert view != null : "view is null";

        Bundle args = getArguments();
        assert args != null : "no arguments for SuccessFragment";

        Views.setText(view, R.id.ym_comment, getString(R.string.ym_success_comment,
                new BigDecimal(args.getString(KEY_CONTRACT_AMOUNT))));

        card = view.findViewById(R.id.ym_card);
        description = (TextView) view.findViewById(R.id.ym_description);
        successMarker = view.findViewById(R.id.ym_success_marker);
        saveCard = (Button) view.findViewById(R.id.ym_save_card);
        saveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveCardClicked();
            }
        });

        moneySource = getMoneySourceFromBundle(savedInstanceState == null ? args :
                savedInstanceState);
        if (moneySource != null) {
            onCardExists();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (moneySource != null) {
            outState.putParcelable(KEY_MONEY_SOURCE, new ExternalCardParcelable(moneySource));
        }
    }

    public void saveCard(ExternalCard moneySource) {
        if (moneySource == null) {
            throw new NullPointerException("moneySource is null");
        }
        this.moneySource = moneySource;
        new DatabaseStorage(getPaymentActivity())
                .insertMoneySource(moneySource);
        onCardSaved();
    }

    private void onSaveCardClicked() {
        card.setBackgroundResource(R.drawable.ym_card_process);
        saveCard.setEnabled(false);
        saveCard.setText(R.string.ym_success_saving_card);
        saveCard.setOnClickListener(null);
        description.setText(R.string.ym_success_saving_card_description);
        repeat();
    }

    private void onCardSaved() {
        Views.setImageResource(getView(), R.id.ym_payment_card_type,
                CardType.get(moneySource.type).icoResId);
        Views.setText(getView(), R.id.ym_pan_fragment,
                MoneySourceFormatter.formatPanFragment(moneySource.panFragment));
        card.setBackgroundResource(R.drawable.ym_card_saved);
        saveCard.setVisibility(View.GONE);
        successMarker.setVisibility(View.VISIBLE);
        description.setText(getString(R.string.ym_success_card_saved_description,
                moneySource.type.cscAbbr));
    }

    private void onCardExists() {
        card.setVisibility(View.GONE);
        saveCard.setVisibility(View.GONE);
        successMarker.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        Views.setVisibility(getView(), R.id.ym_success, View.VISIBLE);
    }

    private ExternalCard getMoneySourceFromBundle(Bundle bundle) {
        ExternalCardParcelable parcelable = bundle.getParcelable(KEY_MONEY_SOURCE);
        return parcelable == null ? null : parcelable.getExternalCard();
    }
}
