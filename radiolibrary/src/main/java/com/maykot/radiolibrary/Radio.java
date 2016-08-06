package com.maykot.radiolibrary;

import android.content.Context;
import android.util.Log;

import com.maykot.radiolibrary.interfaces.ConnectListener;
import com.maykot.radiolibrary.interfaces.MessageListener;
import com.maykot.radiolibrary.interfaces.PushListener;
import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.model.ProxyResponse;
import com.maykot.radiolibrary.model.Push;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Radio implements MqttCallback, Serializable {

    public static MqttClient         mqttClient;
    public static MqttConnectOptions mqttConnectOptions;
    public static       int    QoS                = 2;
    public static       String MQTT_CLIENT_ID     = null;
    public static final String REQUEST_MQTT_TOPIC = "maykot/get_rssi/";
    //    public static final String REQUEST_MQTT_TOPIC = "maykot/request/";
    public static final String TOPIC_HTTP_CHECK   = "maykot/check/";

    public static final String TOPIC_HTTP_CONNECT      = "maykot/connect/";
    public static final String TOPIC_HTTP_DISCONNECT   = "maykot/disconnect/";
    public static final String TOPIC_HTTP_CHECKCONFIRM = "maykot/checkconfirm/";

    public String urlMQTT = null;

    private static Radio radio;
    private static Monitor monitor  = null;
    private static Context activity = null;

    private String token;
    private String user;

    private ArrayList<PushListener> addPushListeners = new ArrayList<PushListener>();

    public static Radio getInstance(Context activity) {

        if (radio == null)
            radio = new Radio();

        radio.activity = activity;

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

            String SUBSCRIBED_TOPIC_RESPONSE = "maykot/response/" + MQTT_CLIENT_ID + "/#";
            String SUBSCRIBED_TOPIC_CONNECT_CONFIRM = "maykot/connect_confirm/" + MQTT_CLIENT_ID + "/#";
            String SUBSCRIBED_TOPIC_DISCONNECT_CONFIRM = "maykot/disconnect_confirm/" + MQTT_CLIENT_ID + "/#";
            String SUBSCRIBED_TOPIC_PUSH = "maykot/push/" + MQTT_CLIENT_ID + "/#";

            mqttClient = new MqttClient(urlMQTT, MQTT_CLIENT_ID, null);
            mqttClient.setCallback(this);
            mqttClient.connect();
            mqttClient.subscribe(new String[]{SUBSCRIBED_TOPIC_CONNECT_CONFIRM,
                    SUBSCRIBED_TOPIC_PUSH,
                    SUBSCRIBED_TOPIC_DISCONNECT_CONFIRM,
                    SUBSCRIBED_TOPIC_RESPONSE}, new int[]{QoS, QoS, QoS, QoS});

            return true;
        } catch (MqttException e1) {
            //throw new Exception(e1);
            return false;
        }
    }

//    public void startMonitor() {
//        if (monitor == null)
//            monitor = new Monitor(getInstance());
//
//        monitor.run();
//
//    }
//
//    public void stopMonitor() {
//        if (monitor != null) {
//            monitor.setActive(false);
//            monitor = null;
//        }
//    }

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

        if (!mqttConnected()) {
            throw new Exception("Client MQTT not conntect");
        }

        Payload dataToSend;

        try {
            dataToSend = HttpPostSerializer.dataToPost(urlCloud, header, MQTT_CLIENT_ID,
                    data, messageListener);
        } catch (Exception e) {
            throw new Exception("Serializer Payload - " + e.getMessage());
        }
        if (mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(QoS);
                mqttMessage.setPayload(dataToSend.messageData);
                mqttClient.publish(REQUEST_MQTT_TOPIC + MQTT_CLIENT_ID + "/" + dataToSend.messageId, mqttMessage);
            } catch (MqttException e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                throw new Exception("Publish failed with reason code = " + e.getReasonCode());
            }
        }
    }

    public void sendCheckRadio(MessageListener messageListener)
            throws Exception {

        if (!mqttClient.isConnected()) {
            throw new Exception("Client MQTT not conntect");
        }

        Payload dataToCheck = null;

        try {
            dataToCheck = HttpPostSerializer.dataToCheck(ContentType.JSON.type, "".getBytes(), messageListener);
        } catch (Exception e) {
            throw new Exception("Serializer Payload - " + e.getMessage());
        }
        if (mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(QoS);
                mqttMessage.setPayload(dataToCheck.messageData);
                mqttClient.publish(TOPIC_HTTP_CHECK + MQTT_CLIENT_ID + "/" + dataToCheck.messageId, mqttMessage);
            } catch (MqttException e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                throw new Exception("Publish failed with reason code = " + e.getReasonCode());
            } catch (Exception e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = ");
                throw new Exception("Publish failed with reason code = ");

            }
        }
    }

    public void connect(String token, String user, ConnectListener connectListener)
            throws Exception {

        this.token = token;
        this.user = user;

        if (!mqttClient.isConnected()) {
            throw new Exception("Client MQTT not conntect");
        }

        long messageId = new Date().getTime();

        String message = MQTT_CLIENT_ID + "#" + messageId + "#" + user + "#" + token;

        CacheConnect.getInstance().addMessage(messageId, connectListener);

        if (mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(QoS);
                mqttMessage.setPayload(SerializationUtils.serialize(message));
                mqttClient.publish(TOPIC_HTTP_CONNECT + MQTT_CLIENT_ID + "/" + messageId, mqttMessage);
            } catch (MqttException e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                throw new Exception("Publish failed with reason code = " + e.getReasonCode());
            } catch (Exception e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = ");
                throw new Exception("Publish failed with reason code = ");

            }
        }
    }

    public void disconnect(String token, ConnectListener connectListener)
            throws Exception {

        if (!mqttClient.isConnected()) {
            throw new Exception("Client MQTT not conntect");
        }

        long messageId = new Date().getTime();

        String message = MQTT_CLIENT_ID + "#" + messageId + "#" + token;

        CacheConnect.getInstance().addMessage(messageId, connectListener);

        if (mqttClient.isConnected()) {
            try {
                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setQos(QoS);
                mqttMessage.setPayload(SerializationUtils.serialize(message));
                mqttClient.publish(TOPIC_HTTP_DISCONNECT + MQTT_CLIENT_ID + "/" + messageId, mqttMessage);
            } catch (MqttException e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                throw new Exception("Publish failed with reason code = " + e.getReasonCode());
            } catch (Exception e) {
                Log.d(getClass().getCanonicalName(), "Publish failed with reason code = ");
                throw new Exception("Publish failed with reason code = ");

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
                mqttClient.publish(REQUEST_MQTT_TOPIC + MQTT_CLIENT_ID + "/" + dataToSend.messageId, mqttMessage);
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

        Log.i("topic", topic);

        if (topic.contains("commandResult")) {
            Log.i("commandResult", new String(mqttMessage.getPayload()));
        } else if (topic.contains("response")) {
            Log.i("Return TOPIC:", topic);
            response(mqttMessage);
        } else if (topic.contains("connect_confirm")) {
            Log.i("Return TOPIC:", topic);
            connectConfirm(mqttMessage);
        } else if (topic.contains("disconnect_confirm")) {
            Log.i("Return TOPIC:", topic);
            disconnectConfirm(mqttMessage);
        } else if (topic.contains("push")) {
            push(mqttMessage);
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

    private void push(MqttMessage mqttMessage) {
        Push pushMessage = SerializationUtils.deserialize(mqttMessage.getPayload());
            for (PushListener pushListener : addPushListeners) {
                pushListener.push(pushMessage.getBody(), pushMessage.getContentType());
            }
    }

    private void disconnectConfirm(MqttMessage mqttMessage) {
        String response = SerializationUtils.deserialize(mqttMessage.getPayload());

        String mqttClient = response.split("#")[0];
        String messageId = response.split("#")[1];
        String status = response.split("#")[2];

        ConnectListener messageListener = CacheConnect.getInstance().findMessage(Long.parseLong(messageId));
        messageListener.result(response, Integer.parseInt(status));
    }

    private void connectConfirm(MqttMessage mqttMessage) {
        String response = SerializationUtils.deserialize(mqttMessage.getPayload());

        String mqttClient = response.split("#")[0];
        String messageId = response.split("#")[1];
        String status = response.split("#")[2];

        ConnectListener messageListener = CacheConnect.getInstance().findMessage(Long.parseLong(messageId));
        messageListener.result(response, Integer.parseInt(status));
    }

    private void response(MqttMessage mqttMessage) {

        try {
            ProxyResponse response = HttpPostSerializer.deserialize(mqttMessage.getPayload());
            Log.i("Response ClientId", new String(response.getMqttClientId()));
            Log.i("Response MessageId", new String(response.getIdMessage()));

            MessageListener messageListener = CacheMessage.getInstance().findMessage(response.getIdMessage());
            ProxyRequest request = CacheMessage.getInstance().findRequest(response.getIdMessage());

            if (response.getVerb().contains("check")) {
                checkMessageResult(response);
            }

            messageListener.result(request, response);

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void checkMessageResult(ProxyResponse response) {

        String body = new String(response.getBody());

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(body);

            if (jsonObject.has("rssi")) {
                jsonObject.put("proxy", "ok");
                jsonObject.put("radio_local", "ok");
            } else if (jsonObject.has("error")) {
                if (response.getStatusCode() == 608) {  //falha radio local
                    jsonObject.put("rssi", "-");
                    jsonObject.put("proxy", "-");
                    jsonObject.put("radio_local", "fail");
                } else if (response.getStatusCode() == 603) { //falha na trasmissao para proxy
                    jsonObject.put("rssi", "-");
                    jsonObject.put("proxy", "fail");
                    jsonObject.put("radio_local", "ok");
                }
            }
        } catch (Exception e) {
            e.getMessage();
            jsonObject = new JSONObject();
        }

        response.setBody(jsonObject.toString().getBytes());
    }

    public ArrayList<PushListener> getAddPushListeners() {
        return addPushListeners;
    }

    public void clearPushListeners() {
        addPushListeners.clear();
    }

    public void addPushListeners(PushListener pushListeners) {
        this.addPushListeners.add(pushListeners);
    }
}
