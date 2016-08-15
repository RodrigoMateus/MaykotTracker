package com.maykot.maykottracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.maykot.maykottracker.chat.ChatMessage;
import com.maykot.maykottracker.chat.TrackerUser;
import com.maykot.maykottracker.rest.SinalRest;
import com.maykot.radiolibrary.ConnectAppChat;
import com.maykot.radiolibrary.Radio;
import com.maykot.radiolibrary.interfaces.ConnectListener;
import com.maykot.radiolibrary.interfaces.PushListener;
import com.maykot.radiolibrary.model.ConnectApp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private ListView list;
    private MediaRecorder mediaRecorder;
    private String voiceStoragePath;
    private MediaPlayer mediaPlayer;

    private boolean audio = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        list = (ListView) findViewById(R.id.list);

        hasSDCard();

        initializeMediaRecord();

        try {
            final ChatAdapter adapter = new ChatAdapter(this, ConnectAppChat.getInstance().listConnectApp());
            list.setAdapter(adapter);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ConnectApp connectApp = adapter.getItem(position);
                    if (audio) {
                        try {
                            stopAudioRecording();
                            playLastStoredAudioMusic();
                            mediaPlayerPlaying();

                            byte[] file = readFile(voiceStoragePath);
                            try {
                                Radio.getInstance(getApplication()).pushSend(connectApp,
                                        file, "audio", new ConnectListener() {
                                            @Override
                                            public void result(String response, int status) {
                                            }
                                        });
                            } catch (Exception e) {}
                        }catch (Exception e){

                        }

                    } else {
                        new ChatMessage(connectApp, ChatActivity.this);
                    }

                    audio = false;
                }
            });

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        createFile();
                        startAudioRecording();
                    }catch (Exception e){}
                    audio = true;
                    return false;
                }
            });

        } catch (Exception e) {}

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        PushListener pushListener = new PushListener() {
            @Override
            public void push(byte[] file, String contentType) {
                if(contentType.contentEquals("audio"))
                    play3gpp(file);
            }
        };
        Radio.getInstance(getApplicationContext()).addPushListeners(pushListener);

        new LoadUserTask().execute();
    }

    private void createFile() {
        voiceStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File audioVoice = new File(voiceStoragePath + File.separator + "voices");
        if (!audioVoice.exists()) {
            audioVoice.mkdir();
        }
        voiceStoragePath = voiceStoragePath + File.separator + "voices/teste_"+ new Date().getTime()+ ".3gpp";
        mediaRecorder.setOutputFile(voiceStoragePath);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Radio.getInstance(getApplication()).connectApp == null){
            new TrackerUser(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Chat Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.maykot.maykottracker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public static class ChatAdapter extends BaseAdapter {

        private Activity activity;
        private static LayoutInflater inflater = null;
        private ArrayList<ConnectApp> connectApps;
        private HashMap<String, ConnectApp> connectAppHashMap;

        public ChatAdapter(Activity activity, ArrayList<ConnectApp> connectAppArrayList) {
            this.activity = activity;
            this.connectApps = connectAppArrayList;
            inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return connectApps.size();
        }

        public ConnectApp getItem(int position) {
            return connectApps.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (convertView == null)
                vi = inflater.inflate(R.layout.item_chat, null);

            TextView user = (TextView) vi.findViewById(R.id.user);
            TextView status = (TextView) vi.findViewById(R.id.status);
            TextView dateCreated = (TextView) vi.findViewById(R.id.dateCreated);

            ConnectApp connectApp = connectApps.get(position);

            try {
                user.setText(connectApp.user);
                status.setText(connectApp.active ? "Ativo" : "Inativo");
                dateCreated.setText(connectApp.dateConnect.toString());
            } catch (Exception e) {
                user.setText("teste");
            }

            return vi;
        }
    }

    private void startAudioRecording() {
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudioRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void playLastStoredAudioMusic() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(voiceStoragePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    private void stopAudioPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void hasSDCard() {
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            System.out.println("There is SDCard");
        } else {
            System.out.println("There is no SDCard");
        }
    }

    private void mediaPlayerPlaying() {
        if (!mediaPlayer.isPlaying()) {
            stopAudioPlay();
        }
    }

    private void initializeMediaRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

    }

    public static byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    public static byte[] readFile(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    private void play3gpp(byte[] mp3SoundByteArray) {
        try {
            voiceStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File audioVoice = new File(voiceStoragePath + File.separator + "voices");
            if (!audioVoice.exists()) {
                audioVoice.mkdir();
            }
            voiceStoragePath = voiceStoragePath + File.separator + "voices/teste" + ".3gpp";

            FileOutputStream fos = new FileOutputStream(new File(voiceStoragePath));
            fos.write(mp3SoundByteArray);
            fos.close();

            FileInputStream fis = new FileInputStream(new File(voiceStoragePath));

            if(mediaPlayer == null)
                initializeMediaRecord();

            createFile();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(fis.getFD());
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
               Log.e("play",e.getMessage());
            }
            mediaPlayer.start();

        } catch (Exception ex) {
            Log.i("play",ex.getMessage());
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    private class LoadUserTask extends AsyncTask<Void, Void, Integer> {

        ProgressDialog progDailog;
        String error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(ChatActivity.this);
            progDailog.setMessage("Buscando Usuários...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                Thread.sleep(1000*60);
                return ConnectAppChat.getInstance().listConnectApp().size();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return 0;
            } catch (Exception e) {
                return 0;
            }
        }

        protected void onPostExecute(Integer result) {
            if (result == 0) {
                Toast.makeText(getApplicationContext(), "Nenhum usuário foi encontrado", Toast.LENGTH_LONG).show();
            }else{
                try {
                final ChatAdapter adapter = new ChatAdapter(ChatActivity.this, ConnectAppChat.getInstance().listConnectApp());
                list.setAdapter(adapter);
                } catch (Exception e) {}
            }
            progDailog.dismiss();
        }
    }

}
