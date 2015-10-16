package com.maykot.maykottracker.radio;

import com.maykot.maykottracker.radio.interfaces.MessageListener;

import org.apache.commons.lang3.SerializationUtils;

import java.util.Date;

public class HttpPostSerializer {

    public static Payload dataToPost(String url, String contentType, byte[] body,
                                    MessageListener messageListener)

    {
        ProxyRequest proxyRequest = new ProxyRequest();

        proxyRequest.setUrl(url);
        proxyRequest.setContentType(contentType);
        proxyRequest.setBody(body);
        proxyRequest.setIdMessage(String.valueOf(new Date().getTime()));
        proxyRequest.setVerb(Verb.POST.getVerb());

        CacheMessage.getInstance().addMessage(proxyRequest.getIdMessage(), messageListener, proxyRequest);

        return new Payload(proxyRequest.getIdMessage(),SerializationUtils.serialize(proxyRequest));
    }

    public static Payload dataToGet(String url, String contentType, MessageListener messageListener)

    {
        ProxyRequest proxyRequest = new ProxyRequest();
        proxyRequest.setUrl(url);
        proxyRequest.setContentType(contentType);
        proxyRequest.setBody(null);
        proxyRequest.setIdMessage(String.valueOf(new Date().getTime()));
        proxyRequest.setVerb(Verb.GET.getVerb());

        CacheMessage.getInstance().addMessage(proxyRequest.getIdMessage(),messageListener, proxyRequest);

        return new Payload(proxyRequest.getIdMessage(),SerializationUtils.serialize(proxyRequest));
    }

    public static ProxyResponse deserialize(byte[] dataResult){
        return  (ProxyResponse) SerializationUtils.deserialize(dataResult) ;
    }

}
