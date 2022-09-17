package com.facesec.devicegroup.deviceGroupLib;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MemberDevice.class}, version = 2, exportSchema = false)
abstract class MemberDeviceDb extends RoomDatabase {
    public abstract MemberDeviceDao memberDeviceDao();
}
