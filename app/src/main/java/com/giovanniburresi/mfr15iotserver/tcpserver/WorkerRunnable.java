package com.giovanniburresi.mfr15iotserver.tcpserver;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Mario on 21/09/2015.
 */
public class WorkerRunnable implements Runnable{


    private String hostname;
    private String ip;
    private String port;
    private String[] services;

    protected Socket clientSocket     = null;
    protected String serverText       = null;
    protected IotTCPserver mIotServer = null;

    private boolean isRunning = true;

    // Getting the streams
    InputStream input   = null;
    OutputStream output = null;

    public WorkerRunnable(Socket clientSocket, String serverText, IotTCPserver mIotServer) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.mIotServer   = mIotServer;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public  String[] getServices() {
        return services;
    }

    public void setServices( String[] services) {
        this.services = services;
    }

    public void stop(){
        isRunning = false;
    }

    public void run() {
        try {
            input  = clientSocket.getInputStream();
            output = clientSocket.getOutputStream();
            // get system time
            long time = System.currentTimeMillis();

            // get info from socket
            ip = clientSocket.getInetAddress().getHostAddress();
            port = "" + clientSocket.getPort();

            Log.i("WorkerRunnable", "Access request from client ip: " + ip + "  port: " + port);

            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String recievedMsg = "";
            char rd;

            while(isRunning) {
                recievedMsg = "";
                while((rd = (char)input.read()) != '}') {
                    recievedMsg += rd;
                }
                recievedMsg += '}';

                processTcpMessage(recievedMsg);

                Log.d("WorkerRunnable", "Message recieved from ip: " + ip + "  port: " + port + ">>> " + recievedMsg);
            }

            output.close();
            input.close();

            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }


    private void processTcpMessage(String s){
        mIotServer.processTcpMessage(this, s);
    }

    public void sendMessage(String s){
        try {
            output.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}