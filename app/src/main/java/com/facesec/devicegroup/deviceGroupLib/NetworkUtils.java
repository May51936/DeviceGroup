package com.facesec.devicegroup.deviceGroupLib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

class NetworkUtils {

    @SuppressLint("DefaultLocale")
    public static String getLocalIPAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            @SuppressLint("MissingPermission") WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        }
        return "";
    }

    public static String byteToString(byte[] bytes){
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i <bytes.length ; i++) {
            if (bytes[i]!=0){
                strBuilder.append((char)bytes[i]);
            }else {
                break;
            }

        }
        return strBuilder.toString();
    }

    private static String[] parseParams(String request){
        return request.split("&");
    }

    public static int getMethodOfPost(String request){
        String[] params = parseParams(request);
        return Integer.parseInt(params[0].split("=")[1]);
    }

    public static String getIpOfPost(String request){
        String[] params = parseParams(request);
        return params[1].split("=")[1];
    }
}
