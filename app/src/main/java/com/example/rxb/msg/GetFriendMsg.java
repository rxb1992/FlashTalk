package com.example.rxb.msg;

import android.util.Log;

import com.example.rxb.client.ChatManager;
import com.example.rxb.util.LocalDataUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Created by rxb on 2015/11/1.
 */
public class GetFriendMsg implements Msg {
    int msgType = Msg.GetFriendMsg;
    String id;
    ChatManager mc;
    static  String identify;//大数据分批发送时一批数据的标识


    public GetFriendMsg(String id) {
        this.id = id;
    }

    public GetFriendMsg(ChatManager mc) {
        this.mc = mc;
    }

    public void parse(DataInputStream dis) {
        try {
            String temp = dis.readUTF();//大数据分批发送时一批数据的标识
            if(identify==null
                    ||identify.equals(temp)==false){
                this.mc.friendList.clear();
                identify = temp;
            }
            int friendsCount = dis.readInt();//一批数据的好友总人数

            for(int i=0; i<friendsCount; i++) {
                String clientId = dis.readUTF();
                String clientName = dis.readUTF();
                String clientToken = dis.readUTF();
                String clientPic = dis.readUTF();
                this.mc.addFriend(clientId, clientName, clientToken, clientPic);
            }
            if(dis.readInt()==Msg.MsgEnd){
                mc.msgRecvManager.GetFriends = 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(DatagramSocket ds, String IP, int port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(msgType);//消息类型：Msg.AskClientsMsg;
            dos.writeUTF(id);//用户id
            mc.msgRecvManager.GetFriends = 0;
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        byte[] buffer = baos.toByteArray();

        try {

            DatagramPacket dp = new DatagramPacket(buffer,buffer.length,new InetSocketAddress(IP,port));
            ds.send(dp);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
