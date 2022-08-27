package com.facesec.devicegroup.deviceGroupLib.listener;

/***
 * Created by Wang Tianyu
 * Currently obsolete
 */

public interface OnDataReadyListener {

    public void onMemberReadyToSend();

    public void onLeaderReadyToSend();

    public void setTimer(Long msTime);

}
