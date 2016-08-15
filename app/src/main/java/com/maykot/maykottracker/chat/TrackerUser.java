package com.maykot.maykottracker.chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.maykot.maykottracker.helper.ChatUser;
import com.maykot.radiolibrary.Radio;
import com.maykot.radiolibrary.interfaces.ConnectListener;

public class TrackerUser {

    String user;

    public TrackerUser(final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Login");
        alertDialog.setMessage("Digite seu nome");

        final EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        
        alertDialog.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        user = input.getText().toString();
                        try {
                            Radio.getInstance(activity).connect(user, user, new ConnectListener() {
                                @Override
                                public void result(String response, int status) {

                                }
                            });
                        } catch (Exception e) {
                            Log.i("connect", e.getMessage());
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