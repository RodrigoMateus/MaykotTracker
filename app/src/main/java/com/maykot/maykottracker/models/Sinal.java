package com.maykot.maykottracker.models;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.maykot.maykottracker.dao.SinalDAO;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 * @since 2016-06-09
 */
public class Sinal {

    private static final String TAG = "Sinal";

    private Long id;
    private Location location;
    private Date date;
    private Integer rssi;

    public Sinal() {

    }

    public Sinal(Long id, Date date, Double latitude, Double longitude, Integer rssi) {
        this.id = id;
        this.date = date;
        this.location = new Location(latitude, longitude);
        this.rssi = rssi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long _id) {
        this.id = _id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public void salva(SQLiteDatabase database) {
        try {
            new SinalDAO().salvarOuAlterarPorId(database, this);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erro ao salvar", e);
        }
    }

    public boolean apaga(SQLiteDatabase database) {
        try {
            return new SinalDAO().apaga(database, this);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erro ao salvar", e);
            return false;
        }
    }

    public static List<Sinal> list(SQLiteDatabase database) {
        try {
            return new SinalDAO().list(database);
        } catch (SQLiteException e) {
            Log.e(TAG, "Erro ao listar todos", e);
            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return "Sinal{" +
                "id=" + id +
                ", location=" + location +
                ", date=" + date +
                ", rssi=" + rssi +
                '}';
    }
}
