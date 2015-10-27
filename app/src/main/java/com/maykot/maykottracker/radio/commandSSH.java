package com.maykot.maykottracker.radio;

import android.os.AsyncTask;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class commandSSH extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String username = "pi";
        String password = "senhas";
        String hostname = "192.168.42.1";
        int port = 22;
        String command = params[0];

        try {
            JSch jsch = new JSch();

            Session session = jsch.getSession(username, hostname, port);

            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");

            // session.connect();
            session.connect(30000); // making a connection with timeout.

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            channel.setInputStream(null);

            // channel.setErrStream(System.err);

            channel.connect();
            Thread.sleep(3000);

            channel.disconnect();
            session.disconnect();

            Thread.sleep(3000);
            return "Router reiniciou.";
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return "Conexão SSH Falhou!";
        }
    }
}




