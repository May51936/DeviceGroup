package com.facesec.devicegroup.deviceGroupLib.listener;

/***
 * Created by Wang Tianyu
 * After setting in device group manager, it will report error once detected
 */

public interface OnErrorListener {

    public void onMemberDisconnect(String ip);

}
