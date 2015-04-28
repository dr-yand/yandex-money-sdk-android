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

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.yandex.money.api.model.ExternalCard;
import ru.yandex.money.android.R;
import ru.yandex.money.android.database.DatabaseStorage;
import ru.yandex.money.android.formatters.MoneySourceFormatter;
import ru.yandex.money.android.utils.CardType;
import ru.yandex.money.android.utils.Views;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author vyasevich
 */
public class CardsFragment extends PaymentFragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTRACT_AMOUNT = "contractAmount";

    private int orientation;
    private PopupMenu menu;
    private DatabaseStorage databaseStorage;
    private ViewGroup cardsView;

    public static CardsFragment newInstance(String title, BigDecimal contractAmount) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_CONTRACT_AMOUNT, contractAmount.toPlainString());

        CardsFragment fragment = new CardsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ym_cards_fragment, container, false);
        assert view != null : "view is null";

        Bundle args = getArguments();
        assert args != null : "specify proper arguments for CardsFragment";

        Views.setText(view, R.id.ym_payment_name, args.getString(KEY_TITLE));
        Views.setText(view, R.id.ym_payment_sum, getString(R.string.ym_cards_payment_sum_value,
                new BigDecimal(args.getString(KEY_CONTRACT_AMOUNT))));

        databaseStorage = new DatabaseStorage(getPaymentActivity());
        cardsView = (ViewGroup) view.findViewById(android.R.id.list);

        final TypedArray themeResourceResolver = getActivity()
                .getTheme()
                .obtainStyledAttributes(new int[]{android.R.attr.listDivider});

        for (final ExternalCard moneySource : getCards()) {
            final View card = inflater.inflate(R.layout.ym_card_item, cardsView, false);
            cardsView.addView(card);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCsc(moneySource);
                }
            });

            final TextView panFragment = (TextView) card.findViewById(R.id.ym_pan_fragment);
            panFragment.setText(MoneySourceFormatter.formatPanFragment(moneySource.panFragment));
            panFragment.setCompoundDrawablesWithIntrinsicBounds(CardType.get(
                    moneySource.type).cardResId, 0, 0, 0);

            final ImageButton button = (ImageButton) view.findViewById(R.id.ym_actions);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, cardsView.indexOfChild(card), moneySource);
                }
            });

            final View dividerImageView = createDivider(view.getContext(),
                    themeResourceResolver.getDrawable(0));

            cardsView.addView(dividerImageView);
        }

        View cardsFooter = inflater.inflate(R.layout.ym_cards_footer, cardsView, false);
        cardsView.addView(cardsFooter);
        cardsFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });

        orientation = getResources()
                .getConfiguration()
                .orientation;
        themeResourceResolver.recycle();
        return view;
    }

    private View createDivider(Context context, Drawable divider) {
        final View dividerImageView = new View(context);

        dividerImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        dividerImageView.setBackgroundDrawable(divider);
        return dividerImageView;
    }

    private List<ExternalCard> getCards() {
        return getPaymentActivity().getCards();
    }

    private void showPopup(View v, int position, ExternalCard moneySource) {
        menu = new PopupMenu(getPaymentActivity(), v);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.ym_card_actions, menu.getMenu());
        menu.setOnMenuItemClickListener(new MenuItemClickListener(moneySource,
                position));
        menu.show();
    }

    private void deleteCard(ExternalCard moneySource, int position) {
        databaseStorage.deleteMoneySource(moneySource);
        cardsView.removeViewAt(position);
        cardsView.removeViewAt(position); // divider
        getCards().remove(moneySource);
    }

    private class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private final ExternalCard moneySource;
        private final int position;

        public MenuItemClickListener(ExternalCard moneySource,
                                     int position) {
            this.moneySource = moneySource;
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.ym_delete) {
                deleteCard(moneySource, position);
                menu = null;
                return true;
            }
            return false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (orientation != newConfig.orientation && menu != null) {
            menu.dismiss();
        }
        orientation = newConfig.orientation;
    }
}
