package com.facesec.devicegroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facesec.devicegroup.Util.ConfigUtils;
import com.facesec.devicegroup.Util.NetworkUtils;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fi.iki.elonen.NanoHTTPD;
import okhttp3.ResponseBody;
import rx.Observer;

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
}
