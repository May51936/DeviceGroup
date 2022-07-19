package com.facesec.devicegroup.deviceGroupLib;

public interface OnDataReadyListener {

    public void onMemberReadyToSend();

    public void onLeaderReadyToSend();

    public void setTimer(Long msTime);

}
