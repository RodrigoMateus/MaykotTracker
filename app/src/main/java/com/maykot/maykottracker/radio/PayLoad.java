package com.maykot.maykottracker.radio;

class Payload{

    public byte[] data;
    public String message;

    public Payload(String message,byte[] data){
        this.data = data;
        this.message = message;
    }

}