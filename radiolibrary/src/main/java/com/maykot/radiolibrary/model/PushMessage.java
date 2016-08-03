package com.maykot.radiolibrary.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

public class PushMessage implements Serializable {

    private static final long serialVersionUID = -5387268491251047957L;
    private int typeMessage;
    private byte[] body;
    private String fileName;

    public PushMessage() {}

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(int typeMessage) {
        this.typeMessage = typeMessage;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
