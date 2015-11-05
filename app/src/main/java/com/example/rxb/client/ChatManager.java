package com.example.rxb.client;

import android.app.Activity;

import com.example.rxb.msg.AskClientsMsg;
import com.example.rxb.msg.MsgRecvManager;
import com.example.rxb.util.DBManager;
import com.example.rxb.util.LocalDataUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by com.com.rxb on 2015/10/10.
 */
public class ChatManager {

    private static ChatManager chatManager = new ChatManager();
    private static Activity context;

    private int pageEndFlag = 0;//分页加载listview时加载完所有数据的标识（1：加载完所有，0：没加载完）
    private MsgClient nc = null;//连接实体

    private String userId;//用户id
    private String userName;//用户名
    private String userToken;//融云分配给用户的token

    public List<Guest> guestList = new ArrayList<Guest>();//用户列表
    public List<Guest> friendList = new ArrayList<Guest>();//好友列表
    public static MsgRecvManager msgRecvManager = new MsgRecvManager();

    public static final int UDPPort = 3331;//该端口主要是客户端用来接收消息的

    //私有的构造函数，这是一个单例模式
    private ChatManager() {}

    public static ChatManager getChatManager(Activity c){
        context = c;
        return chatManager;
    }

    /*
    *  通过TCP连接登陆到服务器
    *  并告知服务器客户端的UDP端口号和聊天用户名
    * */
    public String connect(String IP, int port,String name) {

        this.userName = name;
        this.nc = new MsgClient(IP,this);

        if (IP == null || IP.equals("")) {
            return "err";
        }
        Socket s = null;
        try {
            //登陆的时候是通过TCP连接的，所有服务器端的接收也写在了TCP的连接中
            s = new Socket(IP, port);

            //登陆时向服务器发送请求
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            String Id = LocalDataUtil.readFromSharePref(context, "LoginInfo","userId");
            if(Id!=null&&Id.length()>0) {
                userId = LocalDataUtil.readFromSharePref(context, "LoginInfo","userId");
                userName = LocalDataUtil.readFromSharePref(context, "LoginInfo","userName");
                userToken = LocalDataUtil.readFromSharePref(context, "LoginInfo","userToken");
                dos.writeUTF(userId);
                dos.writeUTF(userName);
                dos.writeUTF(userToken);
            }
            else
            {
                dos.writeUTF("");
                dos.writeUTF(userName);
                dos.writeUTF("");
            }

            //从app服务器获取登陆请求传回的信息（id，name,token）
            DataInputStream dis = new DataInputStream(s.getInputStream());
            if (dis.readInt() == 1) {
                userId = dis.readUTF();
                userToken = dis.readUTF();
            }
//            } else {
//                System.out.println("id和token都是从本地获取的：id=" + userId + ",token=" + userToken);
//            }

            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            return "err";
        }
    }


    //向在线列表中添加用户
    public void addGuest(String guestId, String guestName,String guestTokent,String guestPic) {
        Guest g = new Guest(guestId, guestName,guestTokent,guestPic);
        if (findClient(guestId)==null) {
            guestList.add(g);
        }
    }

    //向好友列表中添加用户
    public void addFriend(String guestId, String guestName,String guestTokent,String guestPic) {
        Guest g = new Guest(guestId, guestName,guestTokent,guestPic);
        if(g.getGuestId().equals(chatManager.getUserId())==false) {
            friendList.add(g);
        }
        DBManager dbManager = LocalDataUtil.getApp(context).getDbManager();
        //将好友信息写入到本地数据库里
        dbManager.deleteByID("friends",guestId);
        dbManager.add("friends",g);
    }

    /**
     * 查找指定id的客户端
     * */
    public Guest findClient(String clientId) {
        Guest g = null;
        for (int i = 0; i < this.guestList.size(); i++) {
            g = this.guestList.get(i);
            if(g.getGuestId().equals(clientId)==false){
                g = null;
                continue;
            }
            else {
                break;
            }
        }
        return g;
    }

    //属性
    public MsgClient getMsgClient() {
        return nc;
    }
    public void setMsgClient(MsgClient nc) {
        this.nc = nc;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
    public String getUserToken() {
        return userToken;
    }
    public void setPageEndFlag(int endFlag) {
        this.pageEndFlag = endFlag;
    }
    public int getPageEndFlag() {
        return pageEndFlag;
    }
}
