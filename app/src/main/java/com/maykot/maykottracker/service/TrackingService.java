package com.maykot.maykottracker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.maykot.maykottracker.radio.ContentType;
import com.maykot.maykottracker.radio.HttpPostSerializer;
import com.maykot.maykottracker.MainActivity;
import com.maykot.maykottracker.R;
import com.maykot.maykottracker.dao.DBManager;
import com.maykot.maykottracker.models.Point;
import com.maykot.maykottracker.radio.ProxyRequest;
import com.maykot.maykottracker.radio.ProxyResponse;
import com.maykot.maykottracker.radio.Radio;
import com.maykot.maykottracker.radio.interfaces.MessageListener;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Handler;

public class TrackingService extends Service {

    private static final String TAG = "TrackingService";
    private static final int TRACKING_INTERVAL = 5 * 1000; // Milliseconds
    private static final int TRACKING_DISTANCE = 0; // Meters

    private SharedPreferences mSharedPreferences;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private boolean isServiceStarted = false;
    private final IBinder locationTrackerBinder = new TrackingBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Binding");

        if (!isServiceStarted) {
            startMe();
        }
        return locationTrackerBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Start signal");

        mSharedPreferences = getSharedPreferences(MainActivity.DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        if (!isServiceStarted) {
            startMe();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Stop signal");

        if (isServiceStarted) {
            stopMe();
        }
    }

    private void startMe() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Acquire a reference to the system Location Manager
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // Define a listener that responds to location updates
                mLocationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.
                        savePoint(location);
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    public void onProviderEnabled(String provider) {

                    }

                    public void onProviderDisabled(String provider) {

                    }
                };
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                // Register the listener with the Location Manager to receive location updates
                mLocationManager.requestLocationUpdates(getBestProvider(), TRACKING_INTERVAL,
                        TRACKING_DISTANCE, mLocationListener);

                isServiceStarted = true;
            }
        }.execute();
    }

    private void stopMe() {
        mLocationManager.removeUpdates(mLocationListener);
        mLocationListener = null;
        mLocationManager = null;
        isServiceStarted = false;
    }

    /**
     * Tenta definir automaticamente o melhor provedor de localizacoes se existir um manager.
     * Caso contrario seta como o GPS.
     */
    private String getBestProvider() {
        if (mLocationManager != null) {
            Criteria criteria = new Criteria();
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            return mLocationManager.getBestProvider(criteria, true);
        } else {
            return LocationManager.GPS_PROVIDER;
        }
    }

    private void savePoint(Location location) {
        Point point = new Point();
        point.setLatitude(location.getLatitude());
        point.setLongitude(location.getLongitude());
        point.setAccuracy((int) location.getAccuracy());
        point.setSpeed(msToKmh(location.getSpeed()));

        SQLiteDatabase db = DBManager.getInstance(getApplicationContext()).getWritableDatabase();
        point.save(db);
        db.close();

        notifyPoint(point);
        publishPoint(point);
        Log.i(TAG, point.toString());
    }

    private void publishPoint(Point point) {
        Gson gson = new Gson();
        String pointJson = gson.toJson(point);

        try {
            HashMap<String, String> header = new HashMap<>();
            header.put("content-type", ContentType.JSON.getType());

            Radio.getInstance().sendPost(
                    mSharedPreferences.getString(MainActivity.URL_APP_SERVER, "http://localhost:8000"),
                    header, pointJson.getBytes(),
                    new MessageListener() {

                        @Override
                        public void result(ProxyRequest request, final ProxyResponse response) {
                            Log.i("Tracking", "POST Response: " + new String(response.getBody()));
                            Toast.makeText(getApplicationContext(), "Mensagem MQTT: " + new String(response.getBody()), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void fail() {
                            Log.i("Main.MessageListener", "Fail.sendPost");
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.i("Main.sendGetMessage", "Fail.sendPost");
        }
    }

    private void notifyPoint(final Point point) {

        if (mSharedPreferences.getBoolean(MainActivity.NOTIFY_LOCATION, false)) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setWhen(Calendar.getInstance().getTimeInMillis())
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentTitle("Nova localização salva!")
                            .setContentText(
                                    "Speed: " + point.getSpeed() +
                                            ", Acc: " + point.getAccuracy() +
                                            ", Time: " + formatDate(point.getCreatedAt()));

            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(123456, mBuilder.build());
        }
    }

    /**
     * Converte a velocidade de metros/segundo para kilometros/hora
     */
    private int msToKmh(float speed) {
        return Math.round(3.6f * speed);
    }

    public class TrackingBinder extends Binder {
        public TrackingService getService() {
            return TrackingService.this;
        }
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }
}
