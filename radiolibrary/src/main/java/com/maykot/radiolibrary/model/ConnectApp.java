package com.maykot.radiolibrary.model;

import java.io.Serializable;
import java.util.Date;

public class ConnectApp implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String user;
    public byte[] sourceRadioAddress;
    public String mqttClient;
    public Date dateConnect;
    public Date dateLastCheck;
    public boolean active;
    public Date dateDisconnect;
    public String token;
    public String messageId;

    public ConnectApp() {
        dateConnect = new Date();
        dateLastCheck = new Date();
        active = true;
    }

    public void disconnect() {
        active = false;
        dateDisconnect = new Date();
    }

    public void check(byte[] sourceRadioAddress, String mqttClient, String user) {
        this.user = user;
        this.sourceRadioAddress = sourceRadioAddress;
        this.mqttClient = mqttClient;
        dateLastCheck = new Date();
    }

}