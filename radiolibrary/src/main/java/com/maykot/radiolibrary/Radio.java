package com.maykot.radiolibrary;

import android.util.Log;

import com.maykot.radiolibrary.interfaces.MessageListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;
import java.util.HashMap;

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

    public boolean mqttConnect(String urlMQTT) throws Exception {
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
            return true;
        } catch (MqttException e1) {
            //throw new Exception(e1);
            return false;
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


    public void sendPost(String urlCloud, HashMap<String, String> header, byte[] data, MessageListener messageListener)
            throws Exception {

        if (!mqttClient.isConnected()) {
            throw new Exception("Client MQTT not conntect");
        }

        Payload dataToSend;

        try {
            dataToSend = HttpPostSerializer.dataToPost(urlCloud, header,
                    data, messageListener);
        } catch (Exception e) {
            throw new Exception("Serializer Payload - " + e.getMessage());
        }
        if (mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(QoS);
                mqttMessage.setPayload(dataToSend.messageData);
                mqttClient.publish(TOPIC_HTTP_POST + MQTT_CLIENT_ID + "/" + dataToSend.messageId, mqttMessage);
            } catch (MqttException e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                throw new Exception("Publish failed with reason code = " + e.getReasonCode());
            }
        }
    }

    public void sendGet(String urlCloud, HashMap<String, String> header, MessageListener messageListener)
            throws Exception {

        if (!mqttClient.isConnected()) {
            throw new Exception("Client MQTT not conntect");
        }

        Payload dataToSend;

        try {
            dataToSend = HttpPostSerializer.dataToGet(urlCloud, header, messageListener);
        } catch (Exception e) {
            throw new Exception("Serializer Payload - " + e.getMessage());
        }
        if (mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(QoS);
                mqttMessage.setPayload(dataToSend.messageData);
                mqttClient.publish(TOPIC_HTTP_POST + MQTT_CLIENT_ID + "/" + dataToSend.messageId, mqttMessage);
            } catch (MqttException e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                throw new Exception("Publish failed with reason code = " + e.getReasonCode());
            } catch (Exception ex) {
                Log.i("sendGet.exception", "Falhou!", ex);
            }
        }
    }

    public boolean sendCommand(String command) {

        if (mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(QoS);
                mqttMessage.setPayload(command.getBytes());
                mqttClient.publish("maykot/" + MQTT_CLIENT_ID + "/command", mqttMessage);
                return true;
            } catch (MqttException e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                return false;
            } catch (Exception ex) {
                Log.i("sendCommand.exception", "Falhou!", ex);
                return false;
            }
        }
        return false;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.i("mqqt lost ", throwable.getMessage());
    }

    @Override
    public void messageArrived(String topic, final MqttMessage mqttMessage) throws Exception {

        if (topic.contains("commandResult")) {
            Log.i("commandResult", new String(mqttMessage.getPayload()));
        } else {
            returnResult(mqttMessage);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//        try {
//            Log.i("mqqt ", iMqttDeliveryToken.getMessage().toString());
//        } catch (MqttException e) {
//            Log.i("mqqt deliveryComplete", e.getMessage());
//        } catch (Exception ex) {
//            Log.i("mqqt deliveryComplete", ex.getMessage(), ex);
//        }
    }

    private void returnResult(MqttMessage mqttMessage) {

        try {
            ProxyResponse response = HttpPostSerializer.deserialize(mqttMessage.getPayload());
            MessageListener messageListener = CacheMessage.getInstance().findMessage(response.getIdMessage());
            ProxyRequest request = CacheMessage.getInstance().findRequest(response.getIdMessage());

            messageListener.result(request, response);

        } catch (Exception e) {
            e.getMessage();
        }
    }
}
