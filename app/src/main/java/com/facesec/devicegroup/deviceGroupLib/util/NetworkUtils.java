package com.facesec.devicegroup.deviceGroupLib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.json.JSONException;
import org.json.JSONObject;

/***
 * Created by Wang Tianyu
 * Network related methods
 */

public class NetworkUtils {

    /**
     * Get device local IP address
     * @param context
     * @return IP String
     */
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

    /**
     * Convert bytes to string
     * @param bytes The bytes to convert
     * @return The result string
     */
    public static String byteToString(byte[] bytes){
        StringBuilder strBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            if (aByte != 0) {
                strBuilder.append((char) aByte);
            } else {
                break;
            }

        }
        return strBuilder.toString();
    }

    /**
     * Separate parameters from request
     * @param request Request string sent from web server
     * @return The separated parameters
     */

    private static String[] parseParams(String request){
        return request.split("&");
    }

    /**
     * Get represent number for post request
     * @param request Get request string sent from web server
     * @return The represent number of the post request
     */

    public static int getMethodOfPost(String request){
        String[] params = parseParams(request);
        return Integer.parseInt(params[0].split("=")[1]);
    }

    /**
     * Get IP from post request
     * @param request Get request string sent from web server
     * @return The represent number of the post request
     */

    public static String getIpOfPost(String request){
        String[] params = parseParams(request);
        return params[1].split("=")[1];
    }

    public static void jsonPut(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
