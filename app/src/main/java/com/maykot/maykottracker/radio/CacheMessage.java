package com.maykot.maykottracker.radio;

import com.maykot.maykottracker.radio.interfaces.MessageListener;

import java.util.HashMap;

/**
 * Created by sabatine on 16/10/15.
 */
public class CacheMessage {
    HashMap<String, MessageListener> hashMap = new HashMap<>();
    HashMap<String, ProxyRequest> hashMapRequest = new HashMap<>();

    private static CacheMessage cacheMessage;

    public static CacheMessage getInstance(){
           if(cacheMessage == null){
               cacheMessage = new CacheMessage();
           }
        return cacheMessage;
    }

    public void addMessage(String idMessage, MessageListener messageListener, ProxyRequest request)
    {
        hashMap.put(idMessage,messageListener);
        hashMapRequest.put(idMessage, request);
    }

    public void removeMessage(String idMessage){
        hashMap.remove(idMessage);
    }

    public int size(){
       return hashMap.size();
    }

    public MessageListener findMessage(String idMessage){
        return hashMap.get(idMessage);
    }

    public void removeRequest(String idMessage){
        hashMapRequest.remove(idMessage);
    }

    public ProxyRequest findRequest(String idMessage){
        return hashMapRequest.get(idMessage);
    }
}
