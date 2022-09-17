package com.facesec.devicegroup.deviceGroupLib.util;

/***
 * Created by Wang Tianyu
 * Constants setting for device group
 */

public class ConfigUtils {

    public static final int WEB_SERVER_PORT = 7034;

    public static final int BROADCAST_PORT = 18793;

    public static String BROADCAST_IP = "234.1.0.1";

    public static final int TCP_PORT = 20178;

    public static final String WEB_SERVER_IP = "http://localhost:7034";

    public static String localIp = "";

    public static long tcpHeartBeatCheckTime = 5000;

    public static int tcpHeartBeatMaxCheck = 5;

    public static long tcpReconnectTime = 5000;

    public static int tcpMaxReconnect = 5;

    public static final int LEADER_START = 1;

    public static final int MEMBER_AUTO_START = 2;

    public static final int MEMBER_MANUAL_START = 3;

    public static final int LEADER_END = 4;

    public static final int MEMBER_AUTO_END = 5;

    public static final int MEMBER_MANUAL_END = 6;

}
