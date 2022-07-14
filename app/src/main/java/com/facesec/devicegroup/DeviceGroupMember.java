package com.facesec.devicegroup;

import android.content.Context;

import com.facesec.devicegroup.Util.ConfigUtils;
import com.facesec.devicegroup.Util.NetworkUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DeviceGroupMember {

    private Context context;
    private InetAddress address;

    public DeviceGroupMember(boolean isLeader, Context context){
        this.context = context;
        try {
            address = InetAddress.getByName(ConfigUtils.BROADCAST_IP);

            if (isLeader){
                sendLocalIp();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLocalIp(){
        MulticastSocket multicastSocket;
        try {
            multicastSocket = new MulticastSocket();
            multicastSocket.joinGroup(address);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String ip = NetworkUtils.getLocalIPAddress(context);
                        byte[] data = ip.getBytes();
                        DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                        datagramPacket.setAddress(address);
                        datagramPacket.setPort(ConfigUtils.BROADCAST_PORT);
                        multicastSocket.send(datagramPacket);
                        Thread.sleep(2000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        MulticastSocket multicastSocket;
        try {
            multicastSocket = new MulticastSocket();
            multicastSocket.joinGroup(address);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] data = new byte[512];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        multicastSocket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
