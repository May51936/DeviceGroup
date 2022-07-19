package com.facesec.devicegroup.deviceGroupLib;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facesec.devicegroup.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test extends Activity{

    private ExecutorService executor;
    private TCPChannelClient tcpChannelClient;
    private WebServer webServer;
    private Button in;
    private Button out;
    private int counter = 0;
    private static final String TAG = Test.class.getSimpleName();
    private RequestManager requestManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConfigUtils.localIp = NetworkUtils.getLocalIPAddress(this);
        requestManager = RequestManager.getRequestManager();
        executor = Executors.newSingleThreadExecutor();
        setContentView(R.layout.activity_test);
        in = findViewById(R.id.in);
        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        out = findViewById(R.id.out);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        try {
            webServer = new WebServer(this);
//            webServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            Log.e("onCreate", "WebServer started");
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
            Log.e("onCreate", "WebServer start failed" + e.getMessage());
        }
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    private void test(){
        new PeopleDataBuilder().
                addItem("123", "123").
                addItem("123", "123").
                getResult();
        requestManager.post(new PostItemBuilder()
                .addItem("test1", "test1")
                .addItem("test2", "test2")
                .getResult());

    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        if (webServer != null) {
//            webServer.closeAllConnections();
//            webServer = null;
//            Log.e("onPause", "app pause, so web server close");
//        }
//    }

    private void demo(){
        DeviceGroupManager deviceGroupManager = DeviceGroupManager.getInstance(this);
        deviceGroupManager.setHostAddress("http://192.168.0.140:1234/dashboard");
        deviceGroupManager.webServerStart();
        deviceGroupManager.setOnDataReceivedListener(new OnDataReceivedListener() {

            @Override
            public void onLeaderDataReceived(JSONObject jsonObject) {
                //计算总数
                try {
                    jsonObject.get("in");
                    //...
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                deviceGroupManager.memberSend(
                        new PeopleDataBuilder().
                                addItem("in", 0).
                                addItem("out",1).
                                addItem("serialNumber",123).
                                addItem("Timestamp", 123).
                                getResult());
            }
        }).start();

    }
}
