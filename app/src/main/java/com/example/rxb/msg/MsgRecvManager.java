package com.example.rxb.msg;

/**
 * Created by rxb on 2015/11/1.
 * 这个类是为了保证在客户端想服务器发送了请求后在接收到相应的数据后再执行往下的操作
 */
public class MsgRecvManager {
    public int AskClients = 0;
    public int GetToken = 0;
    public int GetFriends = 0;
    public int AddFriend = 0;
}
