package com.example.rxb.msg;

import com.example.rxb.client.ChatManager;
import com.example.rxb.client.Guest;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by rxb on 2015/11/1.
 */
public class AddFriendMsg implements Msg {
    ChatManager mc = null;
    int msgType = Msg.AddFriendMsg;
    String id;
    String friendid;

    public AddFriendMsg(String id,String friendid) {
        this.id = id;
        this.friendid = friendid;
    }

    public AddFriendMsg(ChatManager mc) {
        this.mc = mc;
    }


    public void parse(DataInputStream dis) {
        try{
            String userId = dis.readUTF();//用户id
            String userName=dis.readUTF();
            String userToken = dis.readUTF();//用户的新token
            String userPic = dis.readUTF();
            mc.addFriend(userId,userName,userToken,userPic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(DatagramSocket ds,String IP,int port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.writeInt(msgType);//消息类型
            dos.writeUTF(id);//用户id
            dos.writeUTF(friendid);//好友id
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
