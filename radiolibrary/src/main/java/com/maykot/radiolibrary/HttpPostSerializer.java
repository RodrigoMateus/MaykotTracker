package com.maykot.radiolibrary;

import com.maykot.radiolibrary.interfaces.MessageListener;

import org.apache.commons.lang3.SerializationUtils;

import java.util.Date;
import java.util.HashMap;

public class HttpPostSerializer {

    public static Payload dataToPost(String url, HashMap<String, String> header, byte[] body,
                                     MessageListener messageListener) {
        ProxyRequest proxyRequest = new ProxyRequest();

        proxyRequest.setUrl(url);
        proxyRequest.setHeader(header);
        proxyRequest.setBody(body);
        proxyRequest.setIdMessage(String.valueOf(new Date().getTime()));
        proxyRequest.setVerb(Verb.POST.getVerb());


        CacheMessage.getInstance().addMessage(proxyRequest.getIdMessage(), messageListener, proxyRequest);

        return new Payload(proxyRequest.getIdMessage(), SerializationUtils.serialize(proxyRequest));
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
