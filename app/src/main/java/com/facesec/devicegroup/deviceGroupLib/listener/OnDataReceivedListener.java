package com.facesec.devicegroup.deviceGroupLib.listener;

import org.json.JSONObject;

public interface OnDataReceivedListener {
//
//    public void onMemberDataReceived(JSONObject jsonObject);

    public void onLeaderDataReceived(JSONObject jsonObject);
}
