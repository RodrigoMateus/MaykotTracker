package com.maykot.maykottracker.helper;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.maykot.maykottracker.MainActivity;
import com.maykot.maykottracker.R;
import com.maykot.maykottracker.models.Sinal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rodrigo on 10/05/16.
 */
public class Notifcation {

    public static void notificacao(Activity activity, String db) {
        Log.i("ProxyResponse", db);

        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(activity)
                        .setSmallIcon(android.support.v7.appcompat.R.drawable.notification_template_icon_bg)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle("Potencia")
                        .setContentText(db);

        NotificationManager mNotifyMgr =
                (NotificationManager) activity.getSystemService(activity.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1234567, mBuilder.build());

    }

    public static void notifyPoint(Activity activity, final Sinal sinal, SharedPreferences mSharedPreferences) {

        if (mSharedPreferences.getBoolean(MainActivity.NOTIFY_LOCATION, false)) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(activity)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setWhen(Calendar.getInstance().getTimeInMillis())
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentText(
                                    "Lat: " + sinal.getLocation().getLat() +
                                            ", Lon: " + sinal.getLocation().getLon());

            NotificationManager mNotifyMgr =
                    (NotificationManager) activity.getSystemService(activity.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(123456, mBuilder.build());
        }
    }

    private static String formatDate(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

}
