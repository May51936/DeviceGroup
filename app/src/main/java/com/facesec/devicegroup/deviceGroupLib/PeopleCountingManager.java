package com.facesec.devicegroup.deviceGroupLib;

import android.content.Context;

public class PeopleCountingManager {

    private static volatile PeopleCountingManager peopleCountingManager;
    private Context context;

    private PeopleCountingManager(Context context){
        this.context = context;
    }

    public static PeopleCountingManager getInstance(Context context){
        if (peopleCountingManager == null){
            synchronized (PeopleCountingManager.class){
                if (peopleCountingManager == null){
                    peopleCountingManager = new PeopleCountingManager(context);
                }
            }
        }
        return peopleCountingManager;
    }

}
