package com.facesec.devicegroup.deviceGroupLib;

import static com.facesec.devicegroup.deviceGroupLib.util.NetworkUtils.jsonPut;

import android.content.Context;
import android.os.Handler;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * Created by Wang Tianyu
 * Singleton group leader manager, will be called in device group manager
 */

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
    private Thread tcpConnectThread;
    private volatile boolean tcpStart;
    private Runnable runnable;
    private Handler handler;

    private LeaderManager (){
        executorService = Executors.newSingleThreadExecutor();
    }

    public static LeaderManager getLeaderManager(){
        if (leaderManager == null){
            synchronized (LeaderManager.class){
                if (leaderManager == null)
                    leaderManager = new LeaderManager();
            }
        }
        return leaderManager;
    }

    /**
     * Refresh current context to leader manager
     * @param context Current context
     * @return Return itself
     */
    public LeaderManager setContext(Context context) {
        this.context = context;
        return getLeaderManager();
    }

    /**
     * Start to receive the connection request by members, and connect them through TCP
     */

    public void start(){
        try{
            startServer();

            //Join the multicast group
            InetAddress group = InetAddress.getByName(ConfigUtils.BROADCAST_IP);
            Log.i(TAG,"New leader");
            MulticastSocket socket = new MulticastSocket(ConfigUtils.BROADCAST_PORT);
            socket.joinGroup(group);
            byte[] msg = ("I'm leader").getBytes();
            DatagramPacket packet = new DatagramPacket(msg,msg.length,group,ConfigUtils.BROADCAST_PORT);
            socket.send(packet);
            tcpStart = true;
            Log.i("TCP", "TCP started");
            tcpConnectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (tcpStart) {

                        try {
                            Log.i(TAG,"Begin to receive");
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
                    Log.i("TCP", "TCP stopped");
                }
            });
            tcpConnectThread.start();
            checkTCPStatus();

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

    public void stopConnectNewMembers(){
        tcpStart = false;
        Log.i("TCP", "Stop connecting new members");
        tcpConnectThread.stop();
    }

    /**
     * Start the TCP server
     */

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
        JSONObject jsonMsg = null;
        if (message == null){
            new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();
        }
        try {
            jsonMsg = new JSONObject(message);
            if (jsonMsg.getString("type").equals("ip")){
                clients.put(jsonMsg.getString("ip"), true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onDataReceivedListener.onLeaderDataReceived(jsonMsg);
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

    public void sendMessage(JSONObject message) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                tcpChannelClient.send(message.toString());
                Log.e(TAG,"Send out "+ message);
            }
        });

    }

    private void checkTCPStatus(){
        handler = new Handler();
        Map<String, Boolean> status = new HashMap<>();
        runnable = new Runnable() {
            @Override
            public void run() {
                for (String ip: clients.keySet()){
                    if (Boolean.TRUE.equals(clients.get(ip))){
                        status.put(ip, false);
                    }
                }
                int size = status.size();
                JSONObject msg = new JSONObject();
                jsonPut(msg,"type","check");
                sendMessage(msg);
                handler.postDelayed(this, ConfigUtils.tcpCheckTime);
            }
        };
        handler.postDelayed(runnable, 0);
    }


}