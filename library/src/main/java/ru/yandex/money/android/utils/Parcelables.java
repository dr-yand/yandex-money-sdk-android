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

package ru.yandex.money.android.utils;

import android.os.Parcel;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author vyasevich
 */
public class Parcelables {

    public static void writeBoolean(Parcel parcel, boolean value) {
        parcel.writeByte(Booleans.toByte(value));
    }

    public static void writeNullableLong(Parcel parcel, Long value) {
        boolean hasValue = writeNullableValue(parcel, value);
        if (hasValue) {
            parcel.writeLong(value);
        }
    }

    public static void writeBigDecimal(Parcel parcel, BigDecimal value) {
        boolean hasValue = writeNullableValue(parcel, value);
        if (hasValue) {
            parcel.writeDouble(value.doubleValue());
        }
    }

    public static void writeStringMap(Parcel parcel, Map<String, String> map) {
        if (parcel == null) {
            throw new NullPointerException("parcel is null");
        }
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        parcel.writeBundle(Bundles.writeStringMapToBundle(map));
    }

    public static boolean readBoolean(Parcel parcel) {
        return Booleans.toBoolean(parcel.readByte());
    }

    public static Long readNullableLong(Parcel parcel) {
        return hasNullableValue(parcel) ? parcel.readLong() : null;
    }

    public static BigDecimal readBigDecimal(Parcel parcel) {
        return hasNullableValue(parcel) ? new BigDecimal(parcel.readDouble()) : null;
    }

    public static Map<String, String> readStringMap(Parcel parcel) {
        if (parcel == null) {
            throw new NullPointerException("parcel is null");
        }
        return Bundles.readStringMapFromBundle(parcel.readBundle());
    }

    private static boolean writeNullableValue(Parcel parcel, Object value) {
        boolean hasValue = value != null;
        writeBoolean(parcel, hasValue);
        return hasValue;
    }

    private static boolean hasNullableValue(Parcel parcel) {
        return readBoolean(parcel);
    }
}
