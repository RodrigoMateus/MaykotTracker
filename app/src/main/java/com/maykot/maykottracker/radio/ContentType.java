package com.maykot.maykottracker.radio;

/**
 * Created by sabatine on 16/10/15.
 */
public enum ContentType {

    IMAGE("image/png"),
    JSON("application/json");

    String type;

    ContentType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
