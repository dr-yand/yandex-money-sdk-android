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

package ru.yandex.money.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yandex.money.api.model.Card;
import com.yandex.money.api.model.ExternalCard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vyasevich
 */
public class DatabaseStorage {

    private static final String TAG = "DatabaseStorage";

    private final DatabaseHelper helper;

    public DatabaseStorage(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    public List<ExternalCard> selectMoneySources() {
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + MoneySourceTable.NAME, null);
        final int fundingSourceTypeIndex = cursor.getColumnIndex(MoneySourceTable.FUNDING_SOURCE_TYPE);
        final int typeIndex = cursor.getColumnIndex(MoneySourceTable.TYPE);
        final int panFragmentIndex = cursor.getColumnIndex(MoneySourceTable.PAN_FRAGMENT);
        final int tokenIndex = cursor.getColumnIndex(MoneySourceTable.TOKEN);

        List<ExternalCard> moneySources = new ArrayList<ExternalCard>();
        while (cursor.moveToNext()) {
            moneySources.add(new ExternalCard(cursor.getString(panFragmentIndex),
                    Card.Type.parse(cursor.getString(typeIndex)),
                    cursor.getString(fundingSourceTypeIndex),
                    cursor.getString(tokenIndex)));
        }

        cursor.close();
        database.close();
        return moneySources;
    }

    public void insertMoneySource(ExternalCard moneySource) {
        if (moneySource == null) {
            Log.w(TAG, "trying to insert null money source");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MoneySourceTable.FUNDING_SOURCE_TYPE, moneySource.fundingSourceType);
        values.put(MoneySourceTable.TYPE, moneySource.type.name);
        values.put(MoneySourceTable.PAN_FRAGMENT, moneySource.panFragment);
        values.put(MoneySourceTable.TOKEN, moneySource.moneySourceToken);

        if (values.size() != 0) {
            SQLiteDatabase database = getWritableDatabase();
            database.insertOrThrow(MoneySourceTable.NAME, null, values);
            database.close();
        }
    }

    public void deleteMoneySource(ExternalCard moneySource) {
        if (moneySource == null) {
            Log.w(TAG, "trying to delete null money source");
            return;
        }

        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM " + MoneySourceTable.NAME +
                " WHERE " + MoneySourceTable.TOKEN + " = \"" +
                moneySource.moneySourceToken + "\"");
        database.close();
    }

    private SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase database = helper.getReadableDatabase();
        assert database != null : "cannot obtain readable database";
        return database;
    }

    private SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase database = helper.getWritableDatabase();
        assert database != null : "cannot obtain writable database";
        return database;
    }
}
