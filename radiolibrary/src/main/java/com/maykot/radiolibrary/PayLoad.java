package com.maykot.radiolibrary;

class Payload {

    public String messageId;
    public byte[] messageData;

    public Payload(String messageId, byte[] messageData) {
        this.messageId = messageId;
        this.messageData = messageData;
    }

}