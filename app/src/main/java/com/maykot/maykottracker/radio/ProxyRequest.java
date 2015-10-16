package com.maykot.maykottracker.radio;

import java.io.Serializable;

public class ProxyRequest implements Serializable {

    private static final long serialVersionUID = -4707248583815599159L;
    private String url;
    private String contentType;
    private String idMessage;
    private String verb;
    private byte[] body;

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
