package com.maykot.radiolibrary.interfaces;

import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.model.ProxyResponse;

import java.io.Serializable;

public interface ConnectListener extends Serializable {

    void result(String response, int status);

}
