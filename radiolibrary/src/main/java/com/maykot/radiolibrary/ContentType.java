package com.maykot.radiolibrary;

public enum ContentType {

    IMAGE("image/jpg"),
    JSON("application/json");

    String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
