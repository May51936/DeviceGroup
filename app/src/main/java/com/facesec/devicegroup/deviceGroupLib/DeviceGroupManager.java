package com.facesec.devicegroup.deviceGroupLib;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import fi.iki.elonen.NanoHTTPD;

public class DeviceGroupManager implements DeviceGroupWebServer, DataTransfer{

    private String hostIp;
    private static final String TAG = DeviceGroupManager.class.getSimpleName();
    private WebServer webServer;
    private OnErrorListener onErrorListener;
//    private OnDataReadyListener onDataReadyListener;
    private OnDataReceivedListener onDataReceivedListener;
    private Context context;
    private static volatile DeviceGroupManager deviceGroupManager;
//    private OnDetectedListener onDetectedListener;

    private DeviceGroupManager(Context context){
        this.context = context;
        try {
            webServer = new WebServer(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }

    public static DeviceGroupManager getInstance(Context context){
        if (deviceGroupManager == null){
            synchronized (DeviceGroupManager.class){
                if (deviceGroupManager == null){
                    deviceGroupManager = new DeviceGroupManager(context);
                }
            }
        }
        return deviceGroupManager;
    }

    @Override
    public void webServerStart(){
        try {
            webServer.setHostIp(hostIp);
            webServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "WebServer started");
    }

    @Override
    public void webServerStop(){
        webServer.stop();
        Log.i(TAG, "WebServer stoped");
    }

    @Override
    public void setHostAddress(String ip){
        this.hostIp = hostIp;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener){
        this.onErrorListener = onErrorListener;
    }

//    public void setOnDataReadyListener(OnDataReadyListener onDataReadyListener) {
//        this.onDataReadyListener = onDataReadyListener;
//    }

    public void setOnDataReceivedListener(OnDataReceivedListener onDataReceivedListener) {
        LeaderManager.getLeaderManager().setOnDataReceivedListener(onDataReceivedListener);
    }

//    public void setOnDetectedListener(OnDetectedListener onDetectedListener) {
//        this.onDetectedListener = onDetectedListener;
//    }

    @Override
    public void memberSend(JSONObject data) {
        MemberManager.getMemberManager().sendMessage(data);
    }

}
