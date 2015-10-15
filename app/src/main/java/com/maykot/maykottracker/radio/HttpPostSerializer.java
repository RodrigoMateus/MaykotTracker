package com.maykot.maykottracker.radio;

import com.maykot.maykottracker.models.ProxyRequest;

import org.apache.commons.lang3.SerializationUtils;

public class HttpPostSerializer {

    public static byte[] dataToPost(String url, String contentType, byte[] body)

    {
        ProxyRequest proxyRequest = new ProxyRequest();

        proxyRequest.setUrl(url);
        proxyRequest.setContentType(contentType);
        proxyRequest.setBody(body);

//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ObjectOutput objectOutput;
//        try {
//            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
//            objectOutput.writeObject(proxyRequest);
//            objectOutput.close();
//            byteArrayOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return byteArrayOutputStream.toByteArray();

        return SerializationUtils.serialize(proxyRequest);
    }
}
