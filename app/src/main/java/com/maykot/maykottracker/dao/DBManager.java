package com.maykot.maykottracker.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "maykot.sqlite";
    private static final int CURRENT_VERSION = 1;

    private static DBManager sInstance;

    private DBManager(Context context, String dbName) {
        super(context, dbName, null, CURRENT_VERSION);
    }

    public static DBManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBManager(context, DATABASE_NAME);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PointDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

}
