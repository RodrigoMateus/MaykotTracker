package com.maykot.radiolibrary.model;

import java.io.Serializable;

public class Part implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String contentType;
    private String fileName;
    private byte[] body;

    public Part(String contentType, String fileName, byte[] body) {
        this.contentType = contentType;
        this.body = body;
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }


}
