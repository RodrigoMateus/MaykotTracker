package com.maykot.radiolibrary;

public enum Verb {
    GET("get"),
    POST("post"),
    CHECK("check");

    String verb;

    Verb(String verb) {
        this.verb = verb;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }
}
