package com.facesec.devicegroup.deviceGroupLib;

import android.content.Context;
import android.util.Log;

import com.facesec.devicegroup.deviceGroupLib.listener.OnDataReceivedListener;
import com.facesec.devicegroup.deviceGroupLib.util.ConfigUtils;
import com.facesec.devicegroup.deviceGroupLib.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class LeaderManager implements TCPChannelClient.TCPChannelEvents{

    private static final String TAG = LeaderManager.class.getSimpleName();
    private boolean finish = false;
    private ExecutorService executorService;
    private TCPChannelClient tcpChannelClient;
    private int count = 0;
    private Map<String, Boolean> clients = new HashMap<>();
    private volatile static LeaderManager leaderManager;
    private Context context;
    private OnDataReceivedListener onDataReceivedListener;

    private LeaderManager (){
        executorService = Executors.newSingleThreadExecutor();
    }

    public static LeaderManager getLeaderManager(){
        if (leaderManager == null){
            synchronized (RequestManager.class){
                if (leaderManager == null)
                    leaderManager = new LeaderManager();
            }
        }
        return leaderManager;
    }

    public LeaderManager setContext(Context context) {
        this.context = context;
        return getLeaderManager();
    }


    public void start(){
        try{
            startServer();

            InetAddress group = InetAddress.getByName(ConfigUtils.BROADCAST_IP);
            Log.e(TAG,"New leader");
            MulticastSocket socket = new MulticastSocket(ConfigUtils.BROADCAST_PORT);
            socket.joinGroup(group);
            byte[] msg = ("I'm leader").getBytes();
            DatagramPacket packet = new DatagramPacket(msg,msg.length,group,ConfigUtils.BROADCAST_PORT);
            socket.send(packet);
            Log.e(TAG,"Begin to receive");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        try {
                            Log.e(TAG,"Begin to receive");
                            byte[] msg = new byte[512];
                            DatagramPacket packet = new DatagramPacket(msg, msg.length);
                            socket.receive(packet);
                            String msgString = new String(packet.getData(), "UTF-8");
                            Log.e(TAG,msgString);
                            if (msgString.contains("Who's leader")) {
                                String ip = packet.getAddress().getHostAddress();
                                clients.put(ip, true);
                                int port = packet.getPort();
                                Log.e(TAG, "Receive member from " + ip + ", " + port);
                                byte[] sendMsg = ("I'm leader").getBytes();
                                DatagramPacket sendPacket = new DatagramPacket(sendMsg, sendMsg.length, packet.getAddress(), port);
                                socket.send(sendPacket);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (tcpChannelClient != null)
                    tcpChannelClient.disconnect();
                tcpChannelClient = null;
                executorService.shutdown();
                executorService = Executors.newSingleThreadExecutor();
            }
        });
    }

    private void startServer(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                tcpChannelClient = new TCPChannelClient(executorService, LeaderManager.this, NetworkUtils.getLocalIPAddress(context),ConfigUtils.TCP_PORT, context);
            }
        });
    }

    public Map<String, Boolean> getClients() {
        return clients;
    }

    //    @Override
//    protected void onDestroy() {
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                if (tcpChannelClient != null)
//                    tcpChannelClient.disconnect();
//                tcpChannelClient = null;
//                executorService.shutdown();
//            }
//        });
//        super.onDestroy();
//    }

    @Override
    public void onTCPConnected(boolean server) {
        Log.e(TAG,"New client connected");
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                title.setText("Current members: " + ++count);
//                String text = "";
//                for(String ip: clients){
//                    text+=(ip + "\n");
//                }
//                content.setText(text);
//            }
//        });

    }

    @Override
    public void onTCPMessage(String message) {
        Log.e("Leader", "Received" + message);
        try {
            onDataReceivedListener.onLeaderDataReceived(new JSONObject(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        if (message == null)
//            return;
//        try {
//            JSONObject msg = new JSONObject(message);
//            String type = msg.optString("type");
//            switch (type){
//                case "data":
//                    int in = msg.optInt("in");
//                    int out = msg.optInt("out");
//                    int total = msg.optInt("total");
//                    Log.i("Received", "data");
//                    break;
//                case "ip":
//                    String ip = msg.optString("ip");
//                    if (!clients.keySet().contains(ip)){
//                        clients.put(ip, true);
//                    }
//                    Log.i("Received", "new client");
//                    break;
//                case "disconnect":
//                    clients.put(msg.optString("ip"), false);
//                    Log.i("Received", "remove client");
//                    break;
//            }
//            Log.e(TAG,"Received: " + msg.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onTCPError(String description) {
        Log.e(TAG,"Error detected: " + description);
    }

    @Override
    public void onTCPClose() {
        Log.e(TAG,"Server closed");
        count--;
    }

    public void setOnDataReceivedListener(OnDataReceivedListener onDataReceivedListener){
        this.onDataReceivedListener = onDataReceivedListener;
    }


}