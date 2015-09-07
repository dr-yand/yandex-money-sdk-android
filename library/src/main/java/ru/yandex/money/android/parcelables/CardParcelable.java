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

package ru.yandex.money.android.parcelables;

import android.os.Parcel;

import com.yandex.money.api.model.Card;

/**
 * @author Slava Yasevich (vyasevich@yamoney.ru)
 */
public class CardParcelable extends MoneySourceParcelable {

    public CardParcelable(Card card) {
        super(card);
    }

    protected CardParcelable(Parcel parcel, Card.Builder builder) {
        super(parcel, builder.setPanFragment(parcel.readString())
                .setType((Card.Type) parcel.readSerializable()));
    }

    private CardParcelable(Parcel parcel) {
        this(parcel, new Card.Builder());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Card card = (Card) moneySource;
        dest.writeString(card.panFragment);
        dest.writeSerializable(card.type);
        super.writeToParcel(dest, flags);
    }

    public static final Creator<CardParcelable> CREATOR = new Creator<CardParcelable>() {
        @Override
        public CardParcelable createFromParcel(Parcel source) {
            return new CardParcelable(source);
        }

        @Override
        public CardParcelable[] newArray(int size) {
            return new CardParcelable[size];
        }
    };
}
