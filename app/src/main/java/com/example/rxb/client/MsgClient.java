package com.example.rxb.client;

import com.example.rxb.msg.AskClientsMsg;
import com.example.rxb.msg.ClientOffMsg;
import com.example.rxb.msg.ClientOnMsg;
import com.example.rxb.msg.GetFriendMsg;
import com.example.rxb.msg.Msg;
import com.example.rxb.msg.TokenMsg;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by com.com.rxb on 2015/10/10.
 */
public class MsgClient {

    DatagramSocket ds = null;//一个UDP的socket
    ChatManager mc = null;

    private String serIP;

    public MsgClient(String serIP, ChatManager mc) {
        this.serIP = serIP;
        this.mc = mc;
        try {

            //用客户端的端口创建了一个客户端的DatagramSocket
            ds = new DatagramSocket(ChatManager.UDPPort);

        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        //一个用于UDP的新线程用来接收服务器发送来的消息
        new Thread(new UDPThread()).start();
    }

    //一个客户端的UDP线程，用来接收服务器端的消息
    private class UDPThread implements Runnable {

        public void run() {
            while (ds != null) {

                byte[] buffer = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

                try {
                    //接收服务器端传来的信息
                    ds.receive(dp);
                    MsgClient.this.parse(dp);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /*
    *   客户端向服务器端发送的请求
    * */
    public void send(Msg msg) {
        msg.send(ds, serIP, 6666);//6666为服务器的UDP端口号
    }

    /*
    *  根据服务器返回的消息的不同类型进行不同的方式的解析
    * */
    public void parse(DatagramPacket dp) {
        if (dp.getLength() != 0) {
            ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());
            DataInputStream dis = new DataInputStream(bais);
            Msg msg = null;
            try {
                int msgType = dis.readInt();
                switch (msgType) {
                    case Msg.ClientOnMsg:
                        msg = new ClientOnMsg(this.mc);
                        msg.parse(dis);
                        break;
                    case Msg.ClientOffMsg:
                        msg = new ClientOffMsg(this.mc);
                        msg.parse(dis);
                        break;
                    case Msg.AskClientsMsg:
                        msg = new AskClientsMsg(this.mc);
                        msg.parse(dis);
                        break;
                    case Msg.GetTokenMsg:
                        msg = new TokenMsg(this.mc);
                        msg.parse(dis);
                        break;
                    case Msg.GetFriendMsg:
                        msg = new GetFriendMsg(this.mc);
                        msg.parse(dis);
                        break;
                    default:
                        break;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
