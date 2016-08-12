package com.maykot.radiolibrary;

import android.util.Log;

import com.maykot.radiolibrary.interfaces.ConnectListener;

import java.util.HashMap;

public class CacheRssi {

    int bufferSize = 30;
    int position = -1;
    HashMap<Integer, Long> positionHashMap = new HashMap<>();
    HashMap<Long, ConnectListener> messageListenerHashMap = new HashMap<>();

    private static CacheRssi cacheMessage;

    public static CacheRssi getInstance() {
        if (cacheMessage == null) {
            cacheMessage = new CacheRssi();
        }
        return cacheMessage;
    }

    public void addMessage(Long idMessage, ConnectListener connectListener) {
        setMessagePosition();
        removeOldMessage(position);
        positionHashMap.put(position, idMessage);
        messageListenerHashMap.put(idMessage, connectListener);
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
        removePosition(position);
    }

    public void removeMessageListener(Long idMessage) {
        try {
            messageListenerHashMap.remove(idMessage);
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

    public ConnectListener findMessage(Long idMessage) {
        return messageListenerHashMap.get(idMessage);
    }

    public int size() {
        return messageListenerHashMap.size();
    }

}
