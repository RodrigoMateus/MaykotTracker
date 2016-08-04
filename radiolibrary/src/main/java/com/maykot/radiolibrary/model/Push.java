package com.maykot.radiolibrary.model;

import java.io.Serializable;

public class Push implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mqttClientId;

    private String messageId;

    private byte[] body;

    private String contentType;

    public String getMqttClientId() {

        return mqttClientId;

    }

    public void setMqttClientId(String mqttClientId) {

        this.mqttClientId = mqttClientId;

    }

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

    public String getMessageId() {

        return messageId;

    }

    public void setMessageId(String messageId) {

        this.messageId = messageId;

    }
}