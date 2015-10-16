package com.maykot.maykottracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.maykot.maykottracker.models.Point;
import com.maykot.maykottracker.radio.ContentType;
import com.maykot.maykottracker.radio.HttpPostSerializer;
import com.maykot.maykottracker.radio.ProxyRequest;
import com.maykot.maykottracker.radio.ProxyResponse;
import com.maykot.maykottracker.radio.Radio;
import com.maykot.maykottracker.radio.interfaces.MessageListener;
import com.maykot.maykottracker.service.Helper;
import com.maykot.maykottracker.service.TrackingService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private NetworkInfo networkInfo;

    /* SharedPreferences file */
    private SharedPreferences mSharedPreferences;
    public static final String DEFAULT_SHARED_PREFERENCES = "maykot";
    public static final String NOTIFY_LOCATION = "notify_location";
    public static final String URL_BROKER = "url_broker";
    public static final String URL_APP_SERVER = "url_app_server";

    /*  VIEW */
    private Button mMqttConnectButton;
    private CheckBox mCheckBoxNotifyPositions;
    private EditText mUrlBrokerEditText;
    private EditText mUrlApplicationServerEditText;
    private Button mSaveUrlBrokerButton;
    private Button mSaveUrlApplicationServerButton;
    private Button mTakePictureButton;
    private Button mStartTrackingButton;
    private Button mStopTrackingButton;
    private EditText mUserMessageEditText;
    private Button mSendMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Radio.getInstance();

        mSharedPreferences = getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        mMqttConnectButton = (Button) findViewById(R.id.btn_mqtt_connect);
        mMqttConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Radio.getInstance().mqttConnect(mSharedPreferences.getString(URL_BROKER, "tcp://192.168.42.1:1883"));
                }catch (Exception e){
                    e.getMessage();
                }
             }
        });

        mCheckBoxNotifyPositions = (CheckBox) findViewById(R.id.checkbox_notify_positions);
        mCheckBoxNotifyPositions.setChecked(mSharedPreferences.getBoolean(NOTIFY_LOCATION, false));
        mCheckBoxNotifyPositions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotifyPositions();
            }
        });

        mUrlBrokerEditText = (EditText) findViewById(R.id.edit_text_url_broker);
        mUrlBrokerEditText.setText(mSharedPreferences.getString(URL_BROKER, "tcp://192.168.42.1:1883"));

        mSaveUrlBrokerButton = (Button) findViewById(R.id.btn_save_url_broker);
        mSaveUrlBrokerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUrlBroker();
            }
        });

        mUrlApplicationServerEditText = (EditText) findViewById(R.id.edit_text_url_application_server);
        mUrlApplicationServerEditText.setText(mSharedPreferences.getString(URL_APP_SERVER, "http://localhost:8000"));

        mSaveUrlApplicationServerButton = (Button) findViewById(R.id.btn_save_url_application_server);
        mSaveUrlApplicationServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUrlAppServer();
            }
        });

        mStartTrackingButton = (Button) findViewById(R.id.btn_start_tracking);
        mStartTrackingButton.setBackgroundColor(getResources().getColor(R.color.greenButton));
        mStartTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Radio.getInstance().startMonitor();
                //startTracking();
                //mStartTrackingButton.setBackgroundColor(Color.GRAY);
                //mStartTrackingButton.setEnabled(false);
                //mStopTrackingButton.setBackgroundColor(getResources().getColor(R.color.redButton));
                //mStopTrackingButton.setEnabled(true);
            }
        });

        mStopTrackingButton = (Button) findViewById(R.id.btn_stop_tracking);
        mStopTrackingButton.setBackgroundColor(Color.GRAY);
        mStopTrackingButton.setEnabled(true );
        mStopTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Radio.getInstance().stopMonitor();
                //stopTracking();
                //mStartTrackingButton.setBackgroundColor(getResources().getColor(R.color.greenButton));
                //mStartTrackingButton.setEnabled(true);
                //mStopTrackingButton.setBackgroundColor(Color.GRAY);
                //mStopTrackingButton.setEnabled(false);
            }
        });

        mTakePictureButton = (Button) findViewById(R.id.btn_take_picture);
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mUserMessageEditText = (EditText) findViewById(R.id.edit_text_user_message);

        mSendMessageButton = (Button) findViewById(R.id.btn_send_message);
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        String msg = "";
                        if (mUserMessageEditText != null) {
                            msg = mUserMessageEditText.getText().toString();
                        }
                        if (msg.isEmpty()) {
                            msg = "MensagemTeste";
                        }

                        Point point = new Point();
                        point.setAccuracy(new Random().nextInt(20));
                        point.setCreatedAt(new Date());
                        point.setUploaded(true);
                        point.setSpeed(new Random().nextInt(20));
                        point.setLatitude(-22.2222);
                        point.setLatitude(-48.2222);
                        point.setMsg(msg);

                        Gson gson = new Gson();
                        final String pointJson = gson.toJson(point);
                        sendMessage(pointJson);
            }
        });

        TextView mStatusConexaoTextView;
        mStatusConexaoTextView = (TextView) findViewById(R.id.txtView_connection_status);
        int connected = checkNetworkConnection(this);
        if (connected == 0) {
            mStatusConexaoTextView.setText("Sem Conexão");
        }
        if (connected == 1) {
            mStatusConexaoTextView.setText("Conexão WiFi: " + networkInfo.getExtraInfo());
        }
        if (connected == 2) {
            mStatusConexaoTextView.setText("Conexão 3G: " + networkInfo.getExtraInfo());
        }

    }

    private void sendMessage(final String pointJson) {
            try {
                Radio.getInstance().sendGet("http://persys.eprodutiva.com.br/api/organizacao/ping", ContentType.JSON, new MessageListener() {
                    @Override
                    public void result(ProxyRequest request, final ProxyResponse response) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.i("teste", "teste");
                                Toast.makeText(getApplicationContext(), "Mensagem MQTT: " + new String(response.getBody()), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void fail() {
                        Log.i("teste", "teste");
                    }
                });
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

            }
    }


    private void startTracking() {
        Log.i(TAG, "Starting tracking");
        Intent intent = new Intent(MainActivity.this, TrackingService.class);
        startService(intent);
    }

    private void stopTracking() {
        Log.i(TAG, "Stopping tracking");
        Intent intent = new Intent(this, TrackingService.class);
        stopService(intent);
    }

    private void saveNotifyPositions() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(NOTIFY_LOCATION, mCheckBoxNotifyPositions.isChecked());
        editor.apply();
    }

    private void saveUrlBroker() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(URL_BROKER, mUrlBrokerEditText.getText().toString());
        editor.apply();
    }

    private void saveUrlAppServer() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(URL_APP_SERVER, mUrlApplicationServerEditText.getText().toString());
        editor.apply();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap resizedImage = imagemResize(imageBitmap, 400);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            final byte[] imagemToSend = byteArrayOutputStream.toByteArray();
            //radio.sendPost("http://localhost:8000", ContentType.IMAGE, imagemToSend, this);
            try {
                Radio.getInstance().sendPost("http://localhost:8000", ContentType.IMAGE, imagemToSend, new MessageListener() {
                    @Override
                    public void result(ProxyRequest request, final ProxyResponse response) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("teste", "teste");
                                Toast.makeText(getApplicationContext(), "Mensagem MQTT: " + response.getBody().toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void fail() {
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public int checkNetworkConnection(Context context) {
        int connected;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        try {
            //Check WIFI
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                connected = 1;
            } else {
                //Check 3G
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    connected = 2;
                } else {
                    connected = 0;
                }
            }
        } catch (Exception e) {
            connected = 0;
        }
        return connected;
    }

    public static Bitmap imagemResize(Bitmap myBitmap, int maxSize) {
        int outWidth;
        int outHeight;
        int inWidth = myBitmap.getWidth();
        int inHeight = myBitmap.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        return Bitmap.createScaledBitmap(myBitmap, outWidth, outHeight, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Perdeu Conexao", Toast.LENGTH_LONG).show();
        }
    };
}
