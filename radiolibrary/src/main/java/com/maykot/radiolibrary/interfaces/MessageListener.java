package com.maykot.radiolibrary.interfaces;

import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.model.ProxyResponse;

import java.io.Serializable;

public interface MessageListener extends Serializable {

    void result(ProxyRequest request, ProxyResponse response);

}
