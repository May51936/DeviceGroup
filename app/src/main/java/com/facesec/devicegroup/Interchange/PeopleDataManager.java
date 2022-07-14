package com.facesec.devicegroup.Interchange;

import java.util.HashMap;
import java.util.Map;

public class PeopleDataManager {

    private PeopleData totalPeopleData;
    private static volatile PeopleDataManager peopleDataManager;
    private Map<String, PeopleData> memberPeopleData;

    private PeopleDataManager(){
        memberPeopleData = new HashMap<String, PeopleData>();
        totalPeopleData = new PeopleData(0,0,0);
    }

    public static PeopleDataManager getPeopleDataManager(){
        if (peopleDataManager == null){
            synchronized (PeopleDataManager.class){
                if (peopleDataManager == null){
                    peopleDataManager = new PeopleDataManager();
                }
            }
        }
        return peopleDataManager;
    }

    public PeopleData getTotalPeopleData(){
        return totalPeopleData;
    }

    public void updateTotalPeopleData(PeopleData peopleData){
        this.totalPeopleData = peopleData;
    }

    public PeopleData convertFromArrayToPeopleData(int[] peopleData){
        return new PeopleData(peopleData[0], peopleData[1], peopleData[2]);
    }

    public void updatePeopleData(String ip, PeopleData peopleData){
        memberPeopleData.put(ip, peopleData);
    }


}
