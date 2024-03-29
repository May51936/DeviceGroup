package com.facesec.devicegroup.deviceGroupLib;

import static com.facesec.devicegroup.deviceGroupLib.util.NetworkUtils.jsonPut;

import android.content.Context;
import android.util.Log;

import com.facesec.devicegroup.deviceGroupLib.util.ConfigUtils;
import com.facesec.devicegroup.deviceGroupLib.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MemberManager implements TCPChannelClient.TCPChannelEvents{

    private static final String TAG = MemberManager.class.getSimpleName();
    private String leaderIp;
    private int leaderPort;
    private ExecutorService executorService;
    private TCPChannelClient tcpChannelClient;
    private volatile static MemberManager memberManager;
    private Context context;
    private boolean status = false;
    private MemberDeviceDb database;

    public MemberManager setContext(Context context){
        this.context = context;
        return getMemberManager();
    }

    public void setDatabase(MemberDeviceDb database) {
        this.database = database;
    }

    private MemberManager (){
        executorService = Executors.newSingleThreadExecutor();
    }

    public static MemberManager getMemberManager(){
        if (memberManager == null){
            synchronized (RequestManager.class){
                if (memberManager == null)
                    memberManager = new MemberManager();
            }
        }
        return memberManager;
    }

    private void start(){
        try{
            InetAddress group = InetAddress.getByName(ConfigUtils.BROADCAST_IP);
            Log.e(TAG,"New member");
            MulticastSocket socket = new MulticastSocket(ConfigUtils.BROADCAST_PORT);
            socket.joinGroup(group);
            byte[] msg = ("Who's leader").getBytes();
            DatagramPacket packet = new DatagramPacket(msg,msg.length,group,ConfigUtils.BROADCAST_PORT);
            socket.send(packet);
            Log.i(TAG,"Sent out " + new String(msg));

            while (true){
                byte[] receiveMsg = new byte[512];
                DatagramPacket receivePacket = new DatagramPacket(receiveMsg,receiveMsg.length);
                socket.receive(receivePacket);
                String msgString = new String(receivePacket.getData(), "UTF-8");
                if (msgString.contains("I'm leader")) {
                    if (leaderIp == null) {
                        leaderIp = receivePacket.getAddress().getHostAddress();
                        leaderPort = receivePacket.getPort();
                        Log.e(TAG, "Receive leader from " + leaderIp + ", " + leaderPort);
                        break;
                    }
                }
            }

            connect(leaderIp);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect(String ip){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                tcpChannelClient = new TCPChannelClient(executorService, MemberManager.this,ip, ConfigUtils.TCP_PORT, context);
            }
        });
    }

    public void autoStart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                start();
            }
        }).start();
    }

    public void manualStart(String ip){
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect(ip);
            }
        }).start();
    }

    public void close(){
        status = false;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject msg = new JSONObject();
                jsonPut(msg,"type","disconnect");
                jsonPut(msg,"ip", NetworkUtils.getLocalIPAddress(context));
                sendMessage(msg);
                if (tcpChannelClient != null)
                    tcpChannelClient.disconnect();
                tcpChannelClient = null;
                executorService.shutdown();
                executorService = Executors.newSingleThreadExecutor();
            }
        });
    }

    public boolean getStatus() {
        return status;
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



    @Override
    public void onTCPConnected(boolean server) {
        status = true;
        Log.e(TAG,"Connected to leader");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject msg = new JSONObject();
                jsonPut(msg,"type","newMember");
                jsonPut(msg,"ip",NetworkUtils.getLocalIPAddress(context));
                sendMessage(msg);
            }
        });
//        testSend();
    }

    @Override
    public void onTCPMessage(String message) {
        if (message != null){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(message);
                if (jsonObject.getString("type").equals("check")){
                    JSONObject msg = new JSONObject();
                    jsonPut(msg,"type","checkRsp");
                    jsonPut(msg, "ip", NetworkUtils.getLocalIPAddress(context));
                    sendMessage(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTCPError(String description) {

    }

    @Override
    public void onTCPClose() {
        Log.e(TAG, "TCP closed");
        status =  false;
    }

    private void testSend(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject msg = new JSONObject();
                jsonPut(msg,"type","data");
                jsonPut(msg,"in",1);
                jsonPut(msg,"out",1);
                jsonPut(msg,"total",0);
                Log.e(TAG,"Begin testing sending");
                while (true){
                    try {
                        sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}