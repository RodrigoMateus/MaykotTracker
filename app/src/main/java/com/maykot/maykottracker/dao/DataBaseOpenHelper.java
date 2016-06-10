/*
 * Copyright (c) eProdutiva Ltda. All rights reserved.
 *
 * This program and associated files, contains confidential
 * information and is protected by Law.
 * Any disclosure, copying, distribution of this source code, or the taking of
 * any action and/or
 * development based on it, is strictly prohibited.
 */

package com.maykot.maykottracker.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseOpenHelper";
    private static final String DATABASE_NAME = "sinal";
    private static final String DATABASE_EXTENSION = ".db";

    private static final int DATABASE_VERSION = 2;

    private static DataBaseOpenHelper sInstance = null;

    private DataBaseOpenHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");
        db.execSQL(SinalDAO.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON");
        }

    }

    public static DataBaseOpenHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBaseOpenHelper(context, DATABASE_NAME + DATABASE_EXTENSION);
        }
        return sInstance;
    }

    public SQLiteDatabase getDatabase() {
        // Abre uma sessão no Banco de Dados
        if (sInstance != null) {
            SQLiteDatabase session = sInstance.getWritableDatabase();
            return session;
        }
        return null;
    }
}
