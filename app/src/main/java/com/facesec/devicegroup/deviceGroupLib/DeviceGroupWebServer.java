package com.facesec.devicegroup.deviceGroupLib;

public interface DeviceGroupWebServer {

    public void webServerStart();

    public void webServerStop();

    public void setHostAddress(String ip);

}
