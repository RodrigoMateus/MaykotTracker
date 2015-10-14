package com.maykot.maykottracker.radio;

import com.maykot.maykottracker.models.ProxyRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class HttpPostSerializer {

    public static byte[] dataToPost(String url, String contentType, byte[] body)

    {
        ProxyRequest proxyRequest = new ProxyRequest();

        proxyRequest.setUrl(url);
        proxyRequest.setContentType(contentType);
        proxyRequest.setBody(body);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput;
        try {
            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject(proxyRequest);
            objectOutput.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }
}
