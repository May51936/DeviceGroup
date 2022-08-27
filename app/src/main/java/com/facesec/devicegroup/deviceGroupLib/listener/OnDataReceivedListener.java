package com.facesec.devicegroup.deviceGroupLib.listener;

import org.json.JSONObject;

/***
 * Created by Wang Tianyu
 * After setting in device group manager, when leader received message from member,
 * will call this interface's method
 */

public interface OnDataReceivedListener {
//
//    public void onMemberDataReceived(JSONObject jsonObject);

    public void onLeaderDataReceived(JSONObject jsonObject);
}
