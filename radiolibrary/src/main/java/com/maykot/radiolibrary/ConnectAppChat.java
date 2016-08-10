package com.maykot.radiolibrary;

import com.maykot.radiolibrary.model.ConnectApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rodrigo on 10/08/16.
 */
public class ConnectAppChat {

    HashMap<String, ConnectApp> connectAppConcurrentHashMap;

    static ConnectAppChat connectAppChat = null;

    public static ConnectAppChat getInstance(){
        if(connectAppChat==null){
            connectAppChat = new ConnectAppChat();
        }

        return connectAppChat;
    }

    public ConnectAppChat(){
        connectAppConcurrentHashMap = new HashMap<>();
    }

    public void updateList(ArrayList<ConnectApp> connectApps){
        for (ConnectApp connectApp : connectApps){
            connectAppConcurrentHashMap.put(connectApp.token, connectApp);
        }
    }

    public ConnectApp getConnectApp(String token) throws Exception{
        if(connectAppConcurrentHashMap.containsKey(token)){
            return connectAppConcurrentHashMap.get(token);
        }else{
            throw new Exception("Key not found");
        }
    }

    public ArrayList<ConnectApp> listConnectApp() throws Exception{
        return new ArrayList<>(connectAppConcurrentHashMap.values());
    }

}
