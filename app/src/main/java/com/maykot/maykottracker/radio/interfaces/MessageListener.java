package com.maykot.maykottracker.radio.interfaces;

import com.maykot.maykottracker.radio.ProxyRequest;
import com.maykot.maykottracker.radio.ProxyResponse;

import java.io.Serializable;

public interface MessageListener extends Serializable {

    void result(ProxyRequest request, ProxyResponse response);

}
