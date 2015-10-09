package com.maykot.maykottracker.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.maykot.maykottracker.models.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PointDAO {

    private static final String TABLE_NAME = "point";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPLOADED = "uploaded";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_PRECISION = "precision";
    private static final String COLUMN_SPEED = "speed";

    private static final String INTEGER_TYPE = "INTEGER";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_CREATED_AT + " " + INTEGER_TYPE + ", " +
            COLUMN_UPLOADED + " " + INTEGER_TYPE + ", " +
            COLUMN_LATITUDE + " " + INTEGER_TYPE + ", " +
            COLUMN_LONGITUDE + " " + INTEGER_TYPE + ", " +
            COLUMN_PRECISION + " " + INTEGER_TYPE + ", " +
            COLUMN_SPEED + " " + INTEGER_TYPE + " " +
            ");";

    private static final String[] FULL_PROJECTION = {
            COLUMN_CREATED_AT,
            COLUMN_UPLOADED,
            COLUMN_LATITUDE,
            COLUMN_LONGITUDE,
            COLUMN_PRECISION,
            COLUMN_SPEED
    };

    public boolean save(SQLiteDatabase db, Point point) {
        ContentValues values = pointToContentValues(point);

        try {
            long result = db.insert(TABLE_NAME, null, values);

            return result > -1;
        } catch (Exception e) {
            Log.e("PointDAO", "Error while inserting", e);
            return false;
        }
    }

    public List<Point> listNotUploaded(SQLiteDatabase db) {
        List<Point> points = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_NAME,
                    FULL_PROJECTION, // Columns
                    COLUMN_UPLOADED + " = 0", // WHERE clause
                    null, // WHERE args
                    null, // GROUP BY clause
                    null, // HAVING clause
                    null  // ORDER BY clause
            );

            while (cursor.moveToNext()) {
                points.add(cursorToPoint(cursor));
            }
            return points;
        } catch (Exception e) {
            Log.e("PointDAO", "Error while listing not updated rows", e);
            return points;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private ContentValues pointToContentValues(Point point) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_CREATED_AT, point.getCreatedAt().getTime());
        values.put(COLUMN_UPLOADED, point.isUploaded() ? 1 : 0);
        values.put(COLUMN_LATITUDE, point.getLatitude());
        values.put(COLUMN_LONGITUDE, point.getLongitude());
        values.put(COLUMN_PRECISION, point.getAccuracy());
        values.put(COLUMN_SPEED, point.getSpeed());

        return values;
    }

    private Point cursorToPoint(Cursor cursor) {
        Point point = new Point();

        point.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT))));
        point.setUploaded(cursor.getInt(cursor.getColumnIndex(COLUMN_UPLOADED)) > 0);
        point.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
        point.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
        point.setAccuracy(cursor.getInt(cursor.getColumnIndex(COLUMN_PRECISION)));
        point.setSpeed(cursor.getInt(cursor.getColumnIndex(COLUMN_SPEED)));

        return point;
    }

}
