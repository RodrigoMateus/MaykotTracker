package com.maykot.maykottracker.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.maykot.maykottracker.dao.DBManager;
import com.maykot.maykottracker.dao.PointDAO;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Point {

    private Date createdAt;
    private boolean uploaded;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private int accuracy = 0;
    private int speed = 0;
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

    public void savePoint(Context context, Location location) {
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
        setAccuracy((int) location.getAccuracy());
        setSpeed(msToKmh(location.getSpeed()));
        setCreatedAt(new Date());

        SQLiteDatabase db = DBManager.getInstance(context).getWritableDatabase();
        save(db);
        db.close();
    }

    private int msToKmh(float speed) {
        return Math.round(3.6f * speed);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    public String objToJson(){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("date", createdAt.getTime());
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("accuracy", accuracy);
            jsonObject.put("speed", speed);
            jsonObject.put("msg", msg);
        }catch (Exception e){
            Log.i("objToJson",e.getMessage());
        }
        return jsonObject.toString();
    }

}
