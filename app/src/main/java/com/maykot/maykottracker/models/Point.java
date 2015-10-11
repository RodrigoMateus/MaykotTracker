package com.maykot.maykottracker.models;

import android.database.sqlite.SQLiteDatabase;

import com.maykot.maykottracker.dao.PointDAO;

import java.util.Date;
import java.util.List;

public class Point {

    private Date createdAt;
    private boolean uploaded;
    private double latitude;
    private double longitude;
    private int accuracy;
    private int speed;
    private String msg;

    public Point() {
        createdAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getMsg() { return msg; }

    public void setMsg(String msg) { this.msg = msg; }

    public boolean save(SQLiteDatabase db) {
        return new PointDAO().save(db, this);
    }

    public static List<Point> listNotUploaded(SQLiteDatabase db) {
        return new PointDAO().listNotUploaded(db);
    }

    @Override
    public String toString() {
        return "Point{" +
                "; Date=;" + createdAt +
                "; uploaded=;" + uploaded +
                "; latitude=;" + latitude +
                "; longitude=;" + longitude +
                "; accuracy=;" + accuracy +
                "; speed=;" + speed +
                "; msg=;" + msg +
                ";}";
    }
}
