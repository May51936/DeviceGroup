package com.facesec.devicegroup.Interchange;

import android.content.Context;

public class DeviceGroupManager {

    private static volatile DeviceGroupManager deviceGroupManager;
    private Context context;
    private OnErrorListener onErrorListener;

    private DeviceGroupManager(Context context){
        this.context = context;
    }

    private void setOnErrorListener(OnErrorListener onErrorListener){
        this.onErrorListener = onErrorListener;
    }

    public static DeviceGroupManager getDeviceGroupManager(Context context){
        if (deviceGroupManager == null){
            synchronized (DeviceGroupManager.class){
                if (deviceGroupManager == null){
                    deviceGroupManager = new DeviceGroupManager(context);
                }
            }
        }
        return deviceGroupManager;
    }

    public void webServerStart(){

    }
}
