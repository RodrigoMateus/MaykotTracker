package com.maykot.maykottracker.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.maykot.maykottracker.models.Location;
import com.maykot.maykottracker.models.Sinal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 * @since 2016-06-09
 */
public class SinalDAO {

    //@formatter:off
    private static final String TABLE_NAME = "sinal";

    private static final String COLUNA_ID = "_id";
    private static final String COLUNA_DATA_CRIACAO = "data";
    private static final String COLUNA_LATITUDE = "latitude";
    private static final String COLUNA_LONGITUDE = "longitude";
    private static final String COLUNA_RSSI = "rssi";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUNA_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            COLUNA_DATA_CRIACAO + " INTEGER, " +
            COLUNA_LATITUDE + " INTEGER, " +
            COLUNA_LONGITUDE + " INTEGER, " +
            COLUNA_RSSI + " INTEGER" +
            ");";
    //@formatter:on


    public void salvarOuAlterarPorId(SQLiteDatabase session, Sinal sinal) throws SQLiteException {

        ContentValues values = contentValues(sinal);

        values.put(COLUNA_DATA_CRIACAO, System.currentTimeMillis());
        Long id = session.insertOrThrow(TABLE_NAME, null, values);
        if (id > -1) {
            sinal.setId(id);
        } else {
            throw new SQLiteException("Falha alterarPorId()");
        }
    }

    public List<Sinal> list(SQLiteDatabase session) throws SQLiteException {
        Cursor cursor = null;

        try {
            cursor = session.rawQuery("SELECT * FROM " + TABLE_NAME, null);

            List<Sinal> sinais = new ArrayList<>();
            while (cursor.moveToNext()) {
                sinais.add(toModel(cursor));
            }
            return sinais;
        } catch (SQLiteException e) {
            throw e;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean apaga(SQLiteDatabase session, Sinal sinal) throws SQLiteException {
        try {
            int results = session.delete(TABLE_NAME, COLUNA_ID + " = " + sinal.getId(), null);
            return results > 0;
        } catch (SQLiteException e) {
            throw e;
        }
    }

    private ContentValues contentValues(Sinal sinal) {
        ContentValues values = new ContentValues();

        values.put(COLUNA_ID, sinal.getId());
        values.put(COLUNA_DATA_CRIACAO, sinal.getDate().getTime());
        values.put(COLUNA_LATITUDE, sinal.getLocation().getLat());
        values.put(COLUNA_LONGITUDE, sinal.getLocation().getLon());
        values.put(COLUNA_RSSI, sinal.getRssi());

        return values;
    }

    private Sinal toModel(Cursor cursor) {
        Sinal sinal = new Sinal();

        sinal.setId(cursor.getLong(cursor.getColumnIndex(COLUNA_ID)));
        sinal.setDate(new Date(cursor.getLong(cursor.getColumnIndex(COLUNA_DATA_CRIACAO))));

        double latitude = cursor.getDouble(cursor.getColumnIndex(COLUNA_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndex(COLUNA_LONGITUDE));
        sinal.setLocation(new Location(latitude, longitude));

        sinal.setRssi(cursor.getInt(cursor.getColumnIndex(COLUNA_RSSI)));
        return sinal;
    }

}
