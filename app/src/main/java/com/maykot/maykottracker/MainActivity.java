package com.maykot.maykottracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.maykot.maykottracker.dao.DataBaseOpenHelper;
import com.maykot.maykottracker.helper.Notifcation;
import com.maykot.maykottracker.models.Sinal;
import com.maykot.maykottracker.rest.SinalRest;
import com.maykot.radiolibrary.Radio;
import com.maykot.radiolibrary.interfaces.MessageListener;
import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.model.ProxyResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public static final String DEFAULT_SHARED_PREFERENCES = "maykot";
    public static final String NOTIFY_LOCATION = "notify_location";
    public static final String URL_BROKER = "url_broker";
    private static final String TAG = "MainActivity";
    private NetworkInfo networkInfo;
    /* SharedPreferences file */
    private SharedPreferences mSharedPreferences;
    /*  VIEW */
    private EditText mMqttUrlEditText;
    private Button mMqttUrlSaveButton;
    private Button mMqttConnectButton;
    private TextView mStatusConexaoTextView;
    private TextView mStatusRadioLocalTextView;
    private TextView mStatusProxyTextView;
    private TextView mRssiTextView;
    private Button mStartTrackingButton;
    private Button mStopTrackingButton;
    private CheckBox mNotifyPositionsCheckBox;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Radio.getInstance(this);

        mSharedPreferences = getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        mMqttConnectButton = (Button) findViewById(R.id.btn_mqtt_connect);
        mMqttConnectButton.setBackgroundColor(getResources().getColor(R.color.redButton));

        mMqttConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Radio.mqttConnected()) {
                    connectMqtt();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                checkRadioConnection();
            }
        });

        mNotifyPositionsCheckBox = (CheckBox) findViewById(R.id.checkbox_notify_positions);
        mNotifyPositionsCheckBox.setChecked(mSharedPreferences.getBoolean(NOTIFY_LOCATION, false));
        mNotifyPositionsCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotifyPositions();
            }
        });

        mStatusConexaoTextView = (TextView) findViewById(R.id.txtView_connection_status);
        checkStatusConnection(mStatusConexaoTextView);

        mStatusRadioLocalTextView = (TextView) findViewById(R.id.txtView_local_radio_status);
        mStatusProxyTextView = (TextView) findViewById(R.id.txtView_proxy_status);
        mRssiTextView = (TextView) findViewById(R.id.txtView_rssi_status);

        mStartTrackingButton = (Button) findViewById(R.id.btn_start_tracking);
        mStartTrackingButton.setBackgroundColor(getResources().getColor(R.color.greenButton));
        mStartTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Radio.getInstance().startMonitor();
                startTracking();
                mStartTrackingButton.setBackgroundColor(Color.GRAY);
                mStartTrackingButton.setEnabled(false);
                mStopTrackingButton.setBackgroundColor(getResources().getColor(R.color.redButton));
                mStopTrackingButton.setEnabled(true);

            }
        });

        mStopTrackingButton = (Button) findViewById(R.id.btn_stop_tracking);
        mStopTrackingButton.setBackgroundColor(Color.GRAY);
        mStopTrackingButton.setEnabled(true);
        mStopTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Radio.getInstance().stopMonitor();
                stopTracking();
                mStartTrackingButton.setBackgroundColor(getResources().getColor(R.color.greenButton));
                mStartTrackingButton.setEnabled(true);
                mStopTrackingButton.setBackgroundColor(Color.GRAY);
                mStopTrackingButton.setEnabled(false);

                new EnviaSinaisTask().execute();
            }
        });

        mMqttUrlEditText = (EditText) findViewById(R.id.edit_text_url_broker);
        mMqttUrlEditText.setText(mSharedPreferences.getString(URL_BROKER, "tcp://192.168.42.1:1883"));

        mMqttUrlSaveButton = (Button) findViewById(R.id.btn_save_url_broker);
        mMqttUrlSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUrlBroker();
            }
        });

//        mGetRadioPowerRatingButton = (Button) findViewById(R.id.btn_get_radio_power_rating);
//        mGetRadioPowerRatingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                powerRating();
//            }
//        });
//
//        mCheckRadioConnectionButton = (Button) findViewById(R.id.btn_check_radio_connection);
//        mCheckRadioConnectionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkRadioConnection();
//            }
//        });

        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new EnviaSinaisTask().execute();
    }

    private void connectMqtt() {
        MqttTask mqttTask = new MqttTask();
        mqttTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("On Resume", "On Resume");
        checkStatusConnection(mStatusConexaoTextView);
        if (Radio.mqttConnected()) {
            mMqttConnectButton.setBackgroundColor(getResources().getColor(R.color.greenButton));
            mMqttConnectButton.setText("RADIO\nOK!");
        } else {
            mMqttConnectButton.setBackgroundColor(getResources().getColor(R.color.redButton));
            mMqttConnectButton.setText("Conectar\nRADIO");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
//        updateLocationPoint(location);
        checkRadioConnection();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }


    private void startTracking() {
        mGoogleApiClient.connect();
    }

    private void stopTracking() {
        mGoogleApiClient.disconnect();
    }

    private void saveNotifyPositions() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(NOTIFY_LOCATION, mNotifyPositionsCheckBox.isChecked());
        editor.apply();
    }

    private void saveUrlBroker() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(URL_BROKER, mMqttUrlEditText.getText().toString());
        editor.apply();
    }

    public int checkTypeConnection(Context context) {
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

    public void checkStatusConnection(TextView mStatusConexaoTextView) {
        int connected = checkTypeConnection(this);
        if (connected == 0) {
            mStatusConexaoTextView.setText("Sem Conexão");
            mStatusConexaoTextView.setTypeface(null, Typeface.BOLD_ITALIC);
            mStatusConexaoTextView.setTextColor(Color.RED);
            mStatusConexaoTextView.setBackgroundColor(Color.YELLOW);
        }
        if (connected == 1) {
            mStatusConexaoTextView.setText(networkInfo.getExtraInfo());
            mStatusConexaoTextView.setTypeface(null, Typeface.NORMAL);
            mStatusConexaoTextView.setTextColor(Color.BLACK);
            mStatusConexaoTextView.setBackgroundColor(Color.TRANSPARENT);
        }
        if (connected == 2) {
            mStatusConexaoTextView.setText(networkInfo.getExtraInfo() + " (3G)");
            mStatusConexaoTextView.setTypeface(null, Typeface.NORMAL);
            mStatusConexaoTextView.setTextColor(Color.BLACK);
            mStatusConexaoTextView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void checkRadioConnection() {
        try {
            Radio.getInstance(getApplicationContext()).sendCheckRadio(new MessageListener() {
                @Override
                public void result(ProxyRequest request, final ProxyResponse response) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String rssi = null;
                            String localRadio = null;
                            String proxy = null;
                            JSONObject jsonObject = null;

                            try {
                                jsonObject = new JSONObject(new String(response.getBody()));
                                rssi = jsonObject.getString("rssi");
                                localRadio = jsonObject.getString("radio_local");
                                proxy = jsonObject.getString("proxy");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mRssiTextView.setText(rssi);
                            mRssiTextView.setTypeface(null, Typeface.NORMAL);
                            mRssiTextView.setTextColor(Color.BLACK);
                            mRssiTextView.setBackgroundColor(Color.TRANSPARENT);

                            mStatusRadioLocalTextView.setText(localRadio);
                            mStatusRadioLocalTextView.setTypeface(null, Typeface.NORMAL);
                            mStatusRadioLocalTextView.setTextColor(Color.BLACK);
                            mStatusRadioLocalTextView.setBackgroundColor(Color.TRANSPARENT);

                            mStatusProxyTextView.setText(proxy);
                            mStatusProxyTextView.setTypeface(null, Typeface.NORMAL);
                            mStatusProxyTextView.setTextColor(Color.BLACK);
                            mStatusProxyTextView.setBackgroundColor(Color.TRANSPARENT);

                            Sinal sinal = new Sinal();

                            try {
                                com.maykot.maykottracker.models.Location location = new com.maykot.maykottracker.models.Location(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                sinal.setDate(new Date());
                                sinal.setLocation(location);
                                sinal.setRssi(Integer.parseInt(rssi));

                                sinal.salva(DataBaseOpenHelper.getInstance(getApplicationContext()).getDatabase());

                                Notifcation.notifyPoint(MainActivity.this, sinal, mSharedPreferences);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }

                public void fail() {
                }
            });
        } catch (Exception e) {
            //falha no mqtt
            Toast.makeText(getApplicationContext(), "Mensagem Fail: ", Toast.LENGTH_LONG).show();
        }
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

    /**
     * Converte a velocidade de metros/segundo para kilometros/hora
     */
    private int msToKmh(float speed) {
        return Math.round(3.6f * speed);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    public class MqttTask extends AsyncTask<Void, Void, Boolean> {

        ProgressDialog progDailog;
        String error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (Radio.getInstance(MainActivity.this).mqttConnect(mSharedPreferences.getString(URL_BROKER, "tcp://192.168.42.1:1883"))) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
       }

        protected void onPostExecute(Boolean result) {
            if (result) {
                mMqttConnectButton.setBackgroundColor(getResources().getColor(R.color.greenButton));
                mMqttConnectButton.setText("RADIO\nOK!");
            } else {
                mMqttConnectButton.setBackgroundColor(getResources().getColor(R.color.redButton));
                mMqttConnectButton.setText("RADIO\nFail!");

            }
            progDailog.dismiss();
        }

    }

    private class EnviaSinaisTask extends AsyncTask<Void, Void, Integer> {

        ProgressDialog progDailog;
        String error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Iniciando sincronização com servidor");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return SinalRest.enviaSinais(getApplicationContext());
        }

        protected void onPostExecute(Integer result) {
            if(result > 0) {
                Toast.makeText(getApplicationContext(), result + " itens sendo sincronizados com o servidor", Toast.LENGTH_LONG).show();
            }
            progDailog.dismiss();
        }
    }

}
