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
import com.yandex.money.api.model.ExternalCard;

/**
 * @author Slava Yaseich (vyasevich@yamoney.ru)
 */
public final class ExternalCardParcelable extends CardParcelable {

    public ExternalCardParcelable(ExternalCard externalCard) {
        super(externalCard);
    }

    private ExternalCardParcelable(Parcel parcel) {
        super(parcel);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        ExternalCard externalCard = (ExternalCard) moneySource;
        dest.writeString(externalCard.fundingSourceType);
        dest.writeString(externalCard.moneySourceToken);
    }

    @Override
    protected Card createCard(Parcel parcel, String id, String panFragment, Card.Type type) {
        return new ExternalCard(panFragment, type, parcel.readString(), parcel.readString());
    }

    public static final Creator<ExternalCardParcelable> CREATOR =
            new Creator<ExternalCardParcelable>() {
                @Override
                public ExternalCardParcelable createFromParcel(Parcel source) {
                    return new ExternalCardParcelable(source);
                }

                @Override
                public ExternalCardParcelable[] newArray(int size) {
                    return new ExternalCardParcelable[size];
                }
            };
}
