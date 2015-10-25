package com.maykot.maykottracker.radio;

class Payload {

    public String messageId;
    public byte[] messageData;

    public Payload(String messageId, byte[] messageData) {
        this.messageId = messageId;
        this.messageData = messageData;
    }

}