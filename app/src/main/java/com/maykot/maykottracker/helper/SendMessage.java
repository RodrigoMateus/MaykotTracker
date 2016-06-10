package com.maykot.maykottracker.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.maykot.radiolibrary.ContentType;
import com.maykot.radiolibrary.Radio;
import com.maykot.radiolibrary.interfaces.MessageListener;
import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.model.ProxyResponse;

import java.util.HashMap;

/**
 * Created by Rodrigo on 10/05/16.
 */
public class SendMessage {

    /**
     * Envia uma mensagem por get pelo mqtt para o radio
     *
     * @param urlToGet
     */
    public static void sendGetMessage(Context context, String urlToGet) {
        try {
            HashMap<String, String> header = new HashMap<>();
            header.put("content-type", ContentType.JSON.getType());

            Radio.getInstance(context).sendGet(urlToGet, header,
                    new MessageListener() {

                        @Override
                        public void result(ProxyRequest request, final ProxyResponse response) {

                        }
                    });
          }catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.i("Main.sendGetMessage", "Fail.sendGet");
        }
    }

    public static void sendPostMessage(final Activity activity, String json, String urlToGet) {
        try {
            HashMap<String, String> header = new HashMap<>();
            header.put("content-type", ContentType.JSON.getType());

            if (json.isEmpty())
                json = "vazio";

            Radio.getInstance(activity).sendPost(urlToGet, header, json.getBytes(),
                    new MessageListener() {

                        @Override
                        public void result(final ProxyRequest request, final ProxyResponse response) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("Post Button", "Sucess.sendPOst");
                                    Toast.makeText(activity, "Post Button Response: " +
                                            response.getIdMessage() + "\n" +
                                            new String(response.getBody()) + "\n" +
                                            "StatusCode:  " +
                                            response.getStatusCode(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.i("Main.sendGetMessage", "Fail.sendGet");
        }
    }
}
