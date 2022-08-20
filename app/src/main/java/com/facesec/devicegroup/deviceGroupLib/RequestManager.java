package com.facesec.devicegroup.deviceGroupLib;

import android.util.Log;

import com.facesec.devicegroup.deviceGroupLib.util.ConfigUtils;
import com.facesec.devicegroup.deviceGroupLib.util.NetworkUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.List;

class RequestManager {

    private volatile static RequestManager requestManager;
    private HttpClient httpClient;
    private HttpPost httpPost;
    private HttpGet httpGet;
    private HttpResponse httpResponse;
    private byte[] response;
    private static final String TAG = RequestManager.class.getSimpleName();

    public RequestManager(){
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(ConfigUtils.WEB_SERVER_IP);
    }

    public static RequestManager getRequestManager() {
        if (requestManager == null){
            synchronized (RequestManager.class){
                if (requestManager == null)
                    requestManager = new RequestManager();
            }
        }
        return requestManager;
    }

    public RequestManager post(List<NameValuePair> params){
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);
            execute(httpPost);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return requestManager;
    }

    public RequestManager get(Object getUrl){
        httpGet = new HttpGet((String) getUrl);
        execute(httpGet);
        return requestManager;
    }

    private void execute(HttpRequestBase requestBase){
        try {
            httpResponse = httpClient.execute(requestBase);
            response = new byte[1024];
            httpResponse.getEntity().getContent().read(response);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                //task
                Log.i(TAG, "Connection succeed");
            }else{
                Log.e(TAG, "Connection failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpResponse getHttpResponse() {
        HttpResponse result = httpResponse;
        return result;
    }

    public String getStringResponse(){
        getHttpResponse();
        String result = NetworkUtils.byteToString(response);
        return result;
    }
}
