package com.facesec.devicegroup.deviceGroupLib;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MemberDevice {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @ColumnInfo(name = "ip")
    public String ip;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "seriesNumber")
    public String seriesNumber;

    public MemberDevice(String ip) {
        this.id = id;
        this.ip = ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeriesNumber(String seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    @Override
    public String toString() {
        return "MemberDevice{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", seriesNumber='" + seriesNumber + '\'' +
                '}';
    }
}
