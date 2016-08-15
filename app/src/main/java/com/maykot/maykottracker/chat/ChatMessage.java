package com.maykot.maykottracker.chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.maykot.radiolibrary.Radio;
import com.maykot.radiolibrary.interfaces.ConnectListener;
import com.maykot.radiolibrary.model.ConnectApp;

public class ChatMessage {

    String message;

    public ChatMessage(final ConnectApp connectApp, final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(connectApp.user);
        alertDialog.setMessage("Digite a mensagem");

        final EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Enviar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        message = input.getText().toString();
                        try {
                            Radio.getInstance(activity).pushSend(connectApp,
                                    message.getBytes(), "message", new ConnectListener() {
                                        @Override
                                        public void result(String response, int status) {

                                        }
                                    });
                        } catch (Exception e) {
                            Log.i("chat", "send message fail");
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}