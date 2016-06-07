package com.maykot.radiolibrary;

import android.util.Log;

import com.maykot.radiolibrary.interfaces.MessageListener;
import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.model.ProxyResponse;

import org.apache.commons.lang3.SerializationUtils;

import java.util.Date;
import java.util.HashMap;

public class HttpPostSerializer {

    public static Payload dataToPost(String url, HashMap<String, String> header, String mqttClientId, byte[] body,
                                     MessageListener messageListener) {
        ProxyRequest proxyRequest = new ProxyRequest();

        proxyRequest.setUrl(url);
        proxyRequest.setHeader(header);
        proxyRequest.setMqttClientId(mqttClientId);
        proxyRequest.setBody(body);
        proxyRequest.setIdMessage(String.valueOf(new Date().getTime()));
        proxyRequest.setVerb(Verb.POST.getVerb());


        CacheMessage.getInstance().addMessage(proxyRequest.getIdMessage(), messageListener, proxyRequest);

        Log.i("HttpPost", proxyRequest.getIdMessage());

        return new Payload(proxyRequest.getIdMessage(), SerializationUtils.serialize(proxyRequest));
    }

    public static Payload dataToCheck(String contentType, byte[] body, MessageListener messageListener)

    {
        ProxyRequest proxyRequest = new ProxyRequest();
        proxyRequest.setBody(body);
        proxyRequest.setIdMessage(String.valueOf(new Date().getTime()));
        proxyRequest.setVerb(Verb.CHECK.getVerb());

        CacheMessage.getInstance().addMessage(proxyRequest.getIdMessage(), messageListener, proxyRequest);

        return new Payload(proxyRequest.getIdMessage(),SerializationUtils.serialize(proxyRequest));
    }

    public static Payload dataToGet(String url, HashMap<String, String> header, MessageListener messageListener) {
        ProxyRequest proxyRequest = new ProxyRequest();
        proxyRequest.setUrl(url);
        proxyRequest.setHeader(header);
        proxyRequest.setIdMessage(String.valueOf(new Date().getTime()));
        proxyRequest.setVerb(Verb.GET.getVerb());
        proxyRequest.setBody(null);

        CacheMessage.getInstance().addMessage(proxyRequest.getIdMessage(), messageListener, proxyRequest);

        return new Payload(proxyRequest.getIdMessage(), SerializationUtils.serialize(proxyRequest));
    }

    public static ProxyResponse deserialize(byte[] dataResult) {
        return (ProxyResponse) SerializationUtils.deserialize(dataResult);
    }

}
