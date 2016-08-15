package com.maykot.maykottracker.models;

import com.maykot.radiolibrary.model.ConnectApp;
import com.maykot.radiolibrary.model.Push;

/**
 * Created by sabatine on 12/08/16.
 */
public class Message {

    private String tokenid;
    private String user;
    private Push push;

    public Message(){}

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Push getPush() {
        return push;
    }

    public void setPush(Push push) {
        this.push = push;
    }
}
