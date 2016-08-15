package com.maykot.maykottracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.maykot.maykottracker.chat.MessageAdapter;
import com.maykot.maykottracker.models.Message;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    MessageAdapter messageAdapter;
    EditText messageInput;
    Button sendButton;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = this.getIntent().getExtras().getString("username");
        Toast.makeText(this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();

        messageInput = (EditText) findViewById(R.id.message_input);
        messageInput.setOnKeyListener(this);

        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
        final ListView messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
            postMessage();
        }
        return true;
    }

    private void postMessage()  {
        String text = messageInput.getText().toString();

        if (text.equals("")) {
            return;
        }

//        RequestParams params = new RequestParams();
//
//        params.put("text", text);
//        params.put("name", username);
//        params.put("time", new Date().getTime());
//
//        AsyncHttpClient client = new AsyncHttpClient();
//
//        client.post(MESSAGES_ENDPOINT + "/messages", params, new JsonHttpResponseHandler(){
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        messageInput.setText("");
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        postMessage();
    }

}
