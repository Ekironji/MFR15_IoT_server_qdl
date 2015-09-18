package com.giovanniburresi.mfr15iotserver;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.giovanniburresi.mfr15iotserver.json.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.logging.Level;


public class MFR15MainActivity extends Activity {

    JsonParser mJsonParser = null;
    MFR15MainActivityFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mfr15_main);

        fragment = new MFR15MainActivityFragment();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }

        appendNotification(getLocalWifiIp());

        mJsonParser = new JsonParser();

        new DownloadFilesTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mfr15_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private void appendNotification(String msg){
        // TODO: appendere i messaggi nella textview del fragment.. implementare metodo nel fragment
        //fragment.appendNotification(msg);
        Log.i("MainActivity", msg);
    }

    private String getLocalWifiIp(){
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);

        return ipAddress;
    }


    boolean udpRunning = true;
    private class DownloadFilesTask extends AsyncTask<URL, String, Long> {

        private DatagramSocket datagramSocket;

        private final int BUFFER_SIZE = 1024;
        private byte[] buffer;

        protected Long doInBackground(URL... urls) {

            int count = urls.length;
            long totalSize = 0;
            int port = 10001;

            while(udpRunning){
                try {
                    appendNotification("Lookup listening...");
                    datagramSocket = new DatagramSocket(port);

                    buffer = new byte[BUFFER_SIZE];

                    DatagramPacket in_datagramPacket = new DatagramPacket(buffer, BUFFER_SIZE);
                    datagramSocket.receive(in_datagramPacket);

                    InetAddress clientAddress = in_datagramPacket.getAddress();
                    int clientPort = in_datagramPacket.getPort();

                    // ricevo il pacchetto
                    String in_message = new String(
                            in_datagramPacket.getData(),
                            0,
                            in_datagramPacket.getLength());
                    System.out.println("received: " + in_message);

                    // TODO: qui devo parsare la richiesta, prendere il nome del nick... organizzare parser
                    JSONObject client = null;
                    String clientNick = null;
                    String request = null;

                    String out_messagae = "";
                    try {
                        client = new JSONObject(in_message);
                        clientNick = client.getString("hostname");
                        request = client.getString("request");
                        out_messagae = mJsonParser.getLookupAddressResponse(getLocalWifiIp());if(request.equals("lookup")) {
                            publishProgress("Lookup Request from " + clientNick + " at IP: " + clientAddress + ":" + clientPort);
                            out_messagae = mJsonParser.getLookupAddressResponse(getLocalWifiIp());
                        }
                        else{
                            out_messagae = mJsonParser.getMessageError("WRONG_REQUEST");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        out_messagae = mJsonParser.getMessageError("WRONG_REQUEST");
                    }


                    // risposta al client

                    DatagramPacket out_datagramPacket= new DatagramPacket(
                            out_messagae.getBytes(),
                            out_messagae.length(),
                            clientAddress,
                            clientPort);
                    datagramSocket.send(out_datagramPacket);

                } catch (SocketException ex) {
                    Log.e("", ex.toString());
                } catch (IOException ex) {
                    Log.e("", ex.toString());
                } finally {
                    datagramSocket.close();
                }
            }
            return totalSize;
        }

        protected void onProgressUpdate(String... progress) {
            appendNotification(progress[0]);
        }

        protected void onPostExecute(Long result) {
            //showDialog("Downloaded " + result + " bytes");
        }
    }


}
