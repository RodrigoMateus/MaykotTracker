package com.maykot.radiolibrary.model;

import java.io.Serializable;

public class Push implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mqttClientIdSend;
    private String messageIdSend;
    private String userSend;
    private String tokenIdSend;

    private String mqttClientIdReceive;
    private String messageIdReceive;
    private byte[] addressRadioReceive;
    private String tokenIdReceive;

    private byte[] body;
    private String contentType;

    public byte[] getBody() {
        return body;
    }
    public void setBody(byte[] body) {
        this.body = body;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public String getMqttClientIdSend() {
        return mqttClientIdSend;
    }
    public void setMqttClientIdSend(String mqttClientIdSend) {
        this.mqttClientIdSend = mqttClientIdSend;
    }
    public String getMessageIdSend() {
        return messageIdSend;
    }
    public void setMessageIdSend(String messageIdSend) {
        this.messageIdSend = messageIdSend;
    }
    public String getUserSend() {
        return userSend;
    }
    public void setUserSend(String userSend) {
        this.userSend = userSend;
    }
    public String getTokenIdSend() {
        return tokenIdSend;
    }
    public void setTokenIdSend(String tokenIdSend) {
        this.tokenIdSend = tokenIdSend;
    }
    public String getMqttClientIdReceive() {
        return mqttClientIdReceive;
    }
    public void setMqttClientIdReceive(String mqttClientIdReceive) {
        this.mqttClientIdReceive = mqttClientIdReceive;
    }
    public String getMessageIdReceive() {
        return messageIdReceive;
    }
    public void setMessageIdReceive(String messageIdReceive) {
        this.messageIdReceive = messageIdReceive;
    }
    public byte[] getAddressRadioReceive() {
        return addressRadioReceive;
    }
    public void setAddressRadioReceive(byte[] addressRadioReceive) {
        this.addressRadioReceive = addressRadioReceive;
    }
    public String getTokenIdReceive() {
        return tokenIdReceive;
    }
    public void setTokenIdReceive(String tokenIdReceive) {
        this.tokenIdReceive = tokenIdReceive;
    }

}
