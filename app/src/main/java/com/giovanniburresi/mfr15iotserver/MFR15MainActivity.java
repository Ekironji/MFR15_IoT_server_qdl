package com.giovanniburresi.mfr15iotserver;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.giovanniburresi.mfr15iotserver.data.DataManager;
import com.giovanniburresi.mfr15iotserver.json.JsonParser;
import com.giovanniburresi.mfr15iotserver.tcpserver.IotTCPserver;
import com.giovanniburresi.mfr15iotserver.tcpserver.WorkerRunnable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;


public class MFR15MainActivity extends Activity {

    JsonParser mJsonParser = null;
    MFR15MainActivityFragment fragment;

    IotTCPserver tcpServer = null;

    DataManager mDataManager = null;

    private boolean isViewCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mfr15_main);

        fragment = new MFR15MainActivityFragment();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }

        mJsonParser = new JsonParser();

        new UdpLookupTask().execute();

        mDataManager = new DataManager(handler);

        tcpServer = new IotTCPserver(10002, handler, this);
        new Thread(tcpServer).start();

        appendNotification(getLocalWifiIp());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mfr15_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tcpServer.stop();
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

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String status = (String) msg.obj;
            int n = msg.arg1;

            if(n==1)
                fragment.setNode1TextView(status);
            else if(n==2)
                fragment.setNode2TextView(status);
            else if(n==3)
                fragment.setNode3TextView(status);
            else
                fragment.appendNotification(status);
        }
    };


    public void viewBuildComplete(){
        this.isViewCreated = true;
    }

    public void processTcpMessage(WorkerRunnable client, String s){
        if(mDataManager != null)
            mDataManager.processTcpMessage( client , s);
    }

    private void appendNotification(String msg){
        // TODO: appendere i messaggi nella textview del fragment.. implementare metodo nel fragment
        if(isViewCreated)
            fragment.appendNotification(msg);
        Log.i("MainActivity", msg);
    }

    private String getLocalWifiIp(){
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);

        return ipAddress;
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    boolean udpRunning = true;
    private class UdpLookupTask extends AsyncTask<URL, String, Long> {

        private DatagramSocket datagramSocket;

        private final int BUFFER_SIZE = 1024;
        private byte[] buffer;

        protected Long doInBackground(URL... urls) {

            int count = urls.length;
            long totalSize = 0;
            int port = 10001;

            WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            WifiManager.MulticastLock multicastLock = wm.createMulticastLock("mydebuginfo");
            multicastLock.acquire();

            while(udpRunning){
                try {
                    //appendNotification("Lookup listening...");
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
                        clientNick = client.getString("HOSTNAME");
                        request = client.getString("REQUEST");
                        out_messagae = mJsonParser.getLookupAddressResponse(getLocalWifiIp());
                        if(request.equals("LOOKUP")) {
                            publishProgress("Lookup Request from " + clientNick + " at IP: " + clientAddress + ":" + clientPort);
                            out_messagae = mJsonParser.getLookupAddressResponse(getLocalWifiIp());
                        }
                        else{
                            out_messagae = mJsonParser.getMessageError("NO_LOOKUP_REQUEST");
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
