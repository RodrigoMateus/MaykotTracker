package com.maykot.maykottracker.service;

import android.util.Log;

import com.google.gson.Gson;
import com.maykot.maykottracker.radio.HttpPostSerializer;
import com.maykot.maykottracker.MainActivity;
import com.maykot.maykottracker.models.Point;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;
import java.util.Random;

public class Helper {

    public static void sendMessage(MainActivity mainActivity, String msg) {
        Point point = new Point();
        point.setAccuracy(new Random().nextInt(20));
        point.setCreatedAt(new Date());
        point.setUploaded(true);
        point.setSpeed(new Random().nextInt(20));
        point.setLatitude(-22.2222);
        point.setLatitude(-48.2222);
        point.setMsg(msg);

        Gson gson = new Gson();
        String pointJson = gson.toJson(point);

        if (!MainActivity.mqttClient.isConnected()) {
            try {
                MainActivity.mqttClient.connect(MainActivity.mqttConnectOptions);
            } catch (MqttException e) {
                Log.d("Helper.java", "Connection attempt failed with reason code = " + e.getReasonCode() + ":" + e.getCause());
            }
        }

        byte[] dataToSend = HttpPostSerializer.dataToPost("http://localhost:8000", "application/json", pointJson.getBytes());

        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(MainActivity.QoS);
            mqttMessage.setPayload(dataToSend);
            MainActivity.mqttClient.publish(MainActivity.TOPIC_HTTP_POST + MainActivity.MQTT_CLIENT_ID, mqttMessage);
        } catch (MqttException e) {
            Log.d("Error send MQTT message", "Publish failed with reason code = " + e.getReasonCode());
        }
    }
}
