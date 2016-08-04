package com.maykot.radiolibrary.interfaces;

import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.model.ProxyResponse;
import com.maykot.radiolibrary.model.TypeDataPush;

import java.io.Serializable;

public interface PushListener extends Serializable {

    void push(byte[] file, String contentType);

}
