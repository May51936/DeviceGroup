package com.facesec.devicegroup.deviceGroupLib;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
interface MemberDeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDevice(MemberDevice... devices);

    @Query("SELECT * FROM MemberDevice")
    List<MemberDevice> queryAll();

    @Query("SELECT * FROM MemberDevice WHERE ip LIKE :ip LIMIT 1")
    MemberDevice findDeviceByIp(String ip);

    @Update
    void update(MemberDevice device);

    @Delete
    void delete(MemberDevice device);

}
