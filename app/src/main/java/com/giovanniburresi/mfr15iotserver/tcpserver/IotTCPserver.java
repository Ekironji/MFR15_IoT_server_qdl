package com.giovanniburresi.mfr15iotserver.tcpserver;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.giovanniburresi.mfr15iotserver.MFR15MainActivity;
import com.giovanniburresi.mfr15iotserver.json.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Mario on 21/09/2015.
 */
public class IotTCPserver implements Runnable{

    protected int          serverPort    = 10002;
    protected ServerSocket serverSocket  = null;
    protected boolean      isStopped     = false;
    protected Thread       runningThread = null;

    protected Handler mmHandler = null;

    protected MFR15MainActivity mainActivity = null;

    public IotTCPserver(int port){
        this.serverPort = port;
    }

    public IotTCPserver(int port, Handler mHandler, MFR15MainActivity mainActivity){
        this.serverPort = port;
        this.mmHandler = mHandler;
        this.mainActivity = mainActivity;
    }

    public void run(){
        Log.i("IotTCPserver", "Server socked launched.");
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            java.net.Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            WorkerRunnable temp = new WorkerRunnable(clientSocket, "Multithreaded Server", this);

            new Thread( temp ).start();
        }
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            Log.d("IotTCPserver", "Server socked opened on port " + this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }
    }

    public void processTcpMessage(WorkerRunnable client, String s)  {
        this.mainActivity.processTcpMessage(client, s);
        Message m = mmHandler.obtainMessage();
        m.obj = s;
        mmHandler.sendMessage(m);
    }


    private void addIoTClient(){}

    private void removeIoTClient(){}

}