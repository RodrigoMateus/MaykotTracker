package com.maykot.maykottracker.radio;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.maykot.maykottracker.radio.interfaces.MessageListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Radio implements MqttCallback, Serializable {

    public static MqttClient mqttClient;
    public static MqttConnectOptions mqttConnectOptions;
    public static int QoS = 2;
    public static String MQTT_CLIENT_ID = null;
    public static String SUBSCRIBED_TOPIC = null;
    public static final String TOPIC_HTTP_POST = "maykot/http_post/";

    public String urlMQTT = null;

    private static Radio radio;
    private static Monitor monitor = null;


    public static Radio getInstance() {
        if (radio == null)
            radio = new Radio();

        return radio;
    }

    public void mqttConnect(String urlMQTT) throws Exception {
        this.urlMQTT = urlMQTT;

        if (urlMQTT == null) {
            throw new Exception("Url Mqtt invalid");
        }

        try {
            mqttConnectOptions = new MqttConnectOptions();

            MQTT_CLIENT_ID = MqttClient.generateClientId();
            Log.i("MQTT_CLIENT_ID", MQTT_CLIENT_ID);

            SUBSCRIBED_TOPIC = "maykot/" + MQTT_CLIENT_ID + "/#";
            Log.i("SUBSCRIBED_TOPIC", SUBSCRIBED_TOPIC);

            mqttClient = new MqttClient(urlMQTT, MQTT_CLIENT_ID, null);
            mqttClient.setCallback(this);
            mqttClient.connect();
            mqttClient.subscribe(SUBSCRIBED_TOPIC, QoS);
        } catch (MqttException e1) {
            //throw new Exception(e1);
        }
    }

    public void startMonitor() {
        if (monitor == null)
            monitor = new Monitor(getInstance());

        monitor.run();

    }

    public void stopMonitor() {
        if (monitor != null) {
            monitor.setActive(false);
            monitor = null;
        }
    }

    public static boolean mqttConnected() {
        try {
            if (!mqttClient.isConnected()) {
                Log.i("Radio.mqttConnected", "not mqqt connected");
                return false;
            }
        } catch (Exception e) {
            Log.i("Radio.mqttConnected", "not mqqt connected");
            return false;
        }
        Log.i("Radio.mqttConnected", "mqqt connected");
        return true;

    }

    public boolean routerConnected() {
        try {
            if (!mqttClient.isConnected()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean sinkConnected() {
        try {
            if (!mqttClient.isConnected()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean internetConnected() {
        try {
            if (!mqttClient.isConnected()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public void sendPost(String endPoint, ContentType contentType, byte[] data, MessageListener messageListener)
            throws Exception {

        if (!mqttClient.isConnected()) {
            throw new Exception("Client MQTT not conntect");
        }

        Payload dataToSend;

        try {
            dataToSend = HttpPostSerializer.dataToPost(endPoint, contentType.getType(),
                    data, messageListener);
        } catch (Exception e) {
            throw new Exception("Serializer Payload - " + e.getMessage());
        }
        if (mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(QoS);
                mqttMessage.setPayload(dataToSend.data);
                mqttClient.publish(TOPIC_HTTP_POST + MQTT_CLIENT_ID + "/" + dataToSend.message, mqttMessage);
            } catch (MqttException e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                throw new Exception("Publish failed with reason code = " + e.getReasonCode());
            }
        }
    }

    public void sendGet(String endPoint, ContentType contentType, MessageListener messageListener)
            throws Exception {


        Payload dataToSend;

        try {
            dataToSend = HttpPostSerializer.dataToGet(endPoint, contentType.getType(), messageListener);
        } catch (Exception e) {
            throw new Exception("Serializer Payload - " + e.getMessage());
        }
        if (mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(QoS);
                mqttMessage.setPayload(dataToSend.data);
                mqttClient.publish(TOPIC_HTTP_POST + MQTT_CLIENT_ID + "/" + dataToSend.message, mqttMessage);
            } catch (MqttException e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                throw new Exception("Publish failed with reason code = " + e.getReasonCode());
            }
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    /**
     * Este callback roda em uma thread separada. Atualizações na view deverão ser feitas chamando
     * o método runOnUiThread().
     */
    @Override
    public void messageArrived(String topic, final MqttMessage mqttMessage) throws Exception {
        returnResult(mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    private void returnResult(MqttMessage mqttMessage) {

        try {
            ProxyResponse response = HttpPostSerializer.deserialize(mqttMessage.getPayload());
            MessageListener messageListener = CacheMessage.getInstance().findMessage(response.getIdMessage());
            ProxyRequest request = CacheMessage.getInstance().findRequest(response.getIdMessage());

            messageListener.result(request, response);

            CacheMessage.getInstance().removeMessage(response.getIdMessage());
            CacheMessage.getInstance().removeRequest(response.getIdMessage());
        } catch (Exception e) {
            e.getMessage();
        }
    }

}
