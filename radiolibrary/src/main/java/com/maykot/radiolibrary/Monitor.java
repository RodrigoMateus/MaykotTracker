package com.maykot.radiolibrary;

public class Monitor implements Runnable {

    private Radio radio;
    private boolean active = true;

    public Monitor(Radio radio){
        this.radio = radio;
    }

    public void run() {
        while(active) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            radio.mqttConnected();
        }
    }

    public void setActive(boolean active){
        this.active = active;
    }
}