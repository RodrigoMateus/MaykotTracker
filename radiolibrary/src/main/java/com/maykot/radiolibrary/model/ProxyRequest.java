package com.maykot.radiolibrary.model;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

public class ProxyRequest implements Serializable {

    private static final long serialVersionUID = -4707248583815599159L;
    private String verb;
    private String url;
    private HashMap<String, String> header;
    private String mqttClientId;
    private String idMessage;
    private byte[] body;

    public String getVerb() { return verb; }

    public void setVerb(String verb) { this.verb = verb; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public HashMap<String, String> getHeader() { return header; }

    public void setHeader(HashMap<String, String> header) { this.header = header; }

    public String getMqttClientId() { return mqttClientId; }

    public void setMqttClientId(String mqttClientId) { this.mqttClientId = mqttClientId; }

    public String getIdMessage() { return idMessage; }

    public void setIdMessage(String idMessage) { this.idMessage = idMessage; }

    public byte[] getBody() { return body; }

    public void setBody(byte[] body) { this.body = body; }

    public void setMultiPart(MultiPart multiPart){
        this.body = SerializationUtils.serialize(multiPart);
    }

    @Override
    public String toString() {
        return "ProxyRequest [verb=" + verb + ", url=" + url + ", idMessage="
                + idMessage + ", body=" + Arrays.toString(body) + "]";
    }

}
