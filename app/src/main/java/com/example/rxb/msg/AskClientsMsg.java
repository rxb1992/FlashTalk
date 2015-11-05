package com.example.rxb.msg;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;

import com.example.rxb.client.*;

public class AskClientsMsg implements Msg {
	int msgType = Msg.AskClientsMsg;
	int currentPage;//当前页数
	String id;
	ChatManager mc;
	static  String identify;//大数据分批发送时一批数据的标识

	
	public AskClientsMsg(String id,int page) {
		this.id = id;
		this.currentPage = page;
	}

	public AskClientsMsg(ChatManager mc) {
		this.mc = mc;
	}

	public void parse(DataInputStream dis) {
		try {
			String temp = dis.readUTF();//大数据分批发送时一批数据的标识
			if(identify==null
				||identify.equals(temp)==false){
				this.mc.guestList.clear();
				identify = temp;

			}

			int clientsCount = dis.readInt();//在线总人数
			mc.setPageEndFlag(dis.readInt());

			for(int i=0; i<clientsCount; i++) {
				String clientId = dis.readUTF();
				String clientName = dis.readUTF();
				String clientToken = dis.readUTF();
				String clientPic = dis.readUTF();
				this.mc.addGuest(clientId,clientName,clientToken,clientPic);
			}
			if(dis.readInt()==Msg.MsgEnd){
				mc.msgRecvManager.AskClients = 1;
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
			dos.writeInt(currentPage);//请求的当前页数
			mc.msgRecvManager.AskClients = 0;
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
