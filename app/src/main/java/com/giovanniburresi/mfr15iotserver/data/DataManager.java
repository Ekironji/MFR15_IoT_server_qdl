package com.giovanniburresi.mfr15iotserver.data;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.giovanniburresi.mfr15iotserver.json.JsonParser;
import com.giovanniburresi.mfr15iotserver.tcpserver.WorkerRunnable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Created by Mario on 21/09/2015.
 */
public class DataManager {

    ArrayList<IoTClient> mClients = null;
    Handler mHandler = null;

    public DataManager(Handler mHandler) {
        this.mHandler = mHandler;
    }


    public String processTcpMessage(WorkerRunnable client, String s){

        JSONObject mReq = null;
        String response = "";
        try {
            mReq = new JSONObject(s);
        } catch (JSONException e) {
            return JsonParser.getMessageError("JSON_FORMAT_ERROR");
        }

        Log.v("IotTCPserver", "json request: " + mReq);

        try {
            if(mReq.getString("REQUEST").equals("JOIN") ){
               // client.setHostname(mReq.getString("HOSTNAME"));
              //  client.setServices(mReq.getString("SERVICES").split("|")); //TODO: services divided by PIPE

                Log.w("IotTCPserver", "request to join recieve from: " + mReq.getString("HOSTNAME") + "services: " + mReq.getString("SERVICES"));

                response = JsonParser.getMessageSuccess("Everything ok. you can join at " + System.currentTimeMillis());
            }
            else if(mReq.getString("REQUEST").equals("SENSORS_DATA") ){
                String labels   = mReq.getString("LABELS");
                String values   = mReq.getString("VALUES");
                String hostname = mReq.getString("HOSTNAME");

                response = JsonParser.getMessageSuccess("Everything ok, sensor data recieved.");
                Log.w("IotTCPserver", "sensor message recieve from: " + mReq.getString("HOSTNAME"));

                Message m = mHandler.obtainMessage();
                m.obj = values;

                if(hostname.equals("NODE_1")){
                   m.arg1 = 1;
                }
                else if(hostname.equals("NODE_2")){
                    m.arg1 = 2;
                }
                else if(hostname.equals("NODE_3")){
                    m.arg1 = 3;
                }
                mHandler.sendMessage(m);
            }
            else
                response = JsonParser.getMessageError("JSON_REQUEST_UNKNOWN");

        } catch (JSONException e) {
            e.printStackTrace();
            response = JsonParser.getMessageError("JSON_CONTENT_ERROR");
        }

        client.sendMessage(response);
        return response;
    }

    private void addClient(){}

    public void removeClient(){}
}
