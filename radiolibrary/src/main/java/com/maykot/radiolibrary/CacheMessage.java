package com.maykot.radiolibrary;

import android.util.Log;

import com.maykot.radiolibrary.interfaces.MessageListener;

import java.util.HashMap;

public class CacheMessage {

    int bufferSize = 30;
    int position = -1;
    HashMap<Integer, String> positionHashMap = new HashMap<>();
    HashMap<String, MessageListener> messageListenerHashMap = new HashMap<>();
    HashMap<String, ProxyRequest> requestHashMap = new HashMap<>();

    private static CacheMessage cacheMessage;

    public static CacheMessage getInstance() {
        if (cacheMessage == null) {
            cacheMessage = new CacheMessage();
        }
        return cacheMessage;
    }

    public void addMessage(String idMessage, MessageListener messageListener, ProxyRequest request) {
        setMessagePosition();
        removeOldMessage(position);
        positionHashMap.put(position, idMessage);
        messageListenerHashMap.put(idMessage, messageListener);
        requestHashMap.put(idMessage, request);

        Log.i("CacheMessage.size: ", size() + " ");
    }

    public void setMessagePosition() {
        if (position < bufferSize - 1) {
            position++;
        } else {
            position = 0;
        }
    }

    public void removeOldMessage(int position) {
        removeMessageListener(positionHashMap.get(position));
        removeRequest(positionHashMap.get(position));
        removePosition(position);
    }

    public void removeMessageListener(String idMessage) {
        try {
            messageListenerHashMap.remove(idMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeRequest(String idMessage) {
        try {
            requestHashMap.remove(idMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePosition(int count) {
        try {
            positionHashMap.remove(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MessageListener findMessage(String idMessage) {
        return messageListenerHashMap.get(idMessage);
    }

    public ProxyRequest findRequest(String idMessage) {
        return requestHashMap.get(idMessage);
    }

    public int size() {
        return messageListenerHashMap.size();
    }

}
