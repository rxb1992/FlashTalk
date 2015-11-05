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
 * Created by Administrator on 2015/10/20.
 */
public class TokenMsg implements Msg {
    int msgType = Msg.GetTokenMsg;
    String id;
    String name;
    ChatManager mc;

    public TokenMsg(String id,String name) {
        this.id = id;
        this.name = name;
    }

    public TokenMsg(ChatManager mc) {
        this.mc = mc;
    }

    public void parse(DataInputStream dis) {
        try {

            String userId = dis.readUTF();//用户id
            String userToken = dis.readUTF();//用户的新token
            Guest g = mc.findClient(userId);

            //刷新用户的token
            if(g!=null){
                g.setGuestToken(userToken);
                mc.setUserToken(userToken);
            }
            //如果是用户自己的客户端还要刷新chatmanager的token
            if(mc.getUserId().equals(userId)){
                mc.setUserToken(userToken);
            }
            mc.msgRecvManager.GetToken = 1;
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
            dos.writeUTF(name);
            mc.msgRecvManager.GetToken = 0;
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
