package com.example.rxb.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.rxb.client.ChatManager;
import com.example.rxb.msg.AskClientsMsg;
import com.example.rxb.msg.ClientOnMsg;
import com.example.rxb.msg.CreateUserMsg;
import com.example.rxb.msg.GetFriendMsg;
import com.example.rxb.msg.TokenMsg;
import com.example.rxb.util.LocalDataUtil;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity
{
  private static String ServerTCP_IP = "192.168.1.101";
  private static String ServerTCP_Port = "25033";
  private static ChatManager chatManager;
  private Button btnLogin;
  private EditText etName;

  private void autoLogin()
  {
    chatManager = ChatManager.getChatManager(this);
    String str1 = LocalDataUtil.readFromSharePref(this, "LoginInfo", "userName");
    AsyncTask<String,Void,String> local3 = new AsyncTask<String,Void,String>() {

      @Override
      protected String doInBackground(String[] paramAnonymousArrayOfString)
      {
        String str = LoginActivity.chatManager.connect(paramAnonymousArrayOfString[0], Integer.parseInt(paramAnonymousArrayOfString[1]), paramAnonymousArrayOfString[2]);
        if (!str.equals("err"))
        {
          ClientOnMsg localClientOnMsg = new ClientOnMsg(LoginActivity.chatManager.getUserName(), LoginActivity.chatManager.getUserId(), LoginActivity.chatManager.getUserToken());
          LoginActivity.chatManager.getMsgClient().send(localClientOnMsg);

          CreateUserMsg localCreateUserMsg = new CreateUserMsg(LoginActivity.chatManager.getUserName(), LoginActivity.chatManager.getUserId(), LoginActivity.chatManager.getUserToken());
          LoginActivity.chatManager.getMsgClient().send(localCreateUserMsg);

          AskClientsMsg localAskClientsMsg = new AskClientsMsg(LoginActivity.chatManager.getUserId(), 1);
          LoginActivity.chatManager.getMsgClient().send(localAskClientsMsg);

          while (ChatManager.msgRecvManager.AskClients == 0) {
            try {
              Thread.currentThread();
              Thread.sleep(1000L);
            } catch (InterruptedException localInterruptedException2) {
              localInterruptedException2.printStackTrace();
            }
          }

          GetFriendMsg localGetFriendMsg = new GetFriendMsg(LoginActivity.chatManager.getUserId());
          LoginActivity.chatManager.getMsgClient().send(localGetFriendMsg);
          while (ChatManager.msgRecvManager.GetFriends == 0) {
            try {
              Thread.currentThread();
              Thread.sleep(1000L);
            } catch (InterruptedException localInterruptedException1) {
              localInterruptedException1.printStackTrace();
            }
          }

          LocalDataUtil.getApp(LoginActivity.this).setChatManager(LoginActivity.chatManager);
          LocalDataUtil.getApp(LoginActivity.this).setMsgClient(LoginActivity.chatManager.getMsgClient());
        }
        return str;
      }
    };
    String[] arrayOfString = new String[3];
    arrayOfString[0] = ServerTCP_IP;
    arrayOfString[1] = ServerTCP_Port;
    arrayOfString[2] = str1;
    local3.execute(arrayOfString);
    try
    {
      String str2 = local3.get();
      if (str2.equals("success"))
      {
        connectToRongCloud(chatManager.getUserToken());
        return;
      }
    } catch (ExecutionException e) {
      //e.printStackTrace();
      Toast.makeText(getApplicationContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
      setContentView(R.layout.activity_login);
      getViewComponent();
    } catch (InterruptedException e) {
      //e.printStackTrace();
      Toast.makeText(getApplicationContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
      setContentView(R.layout.activity_login);
      getViewComponent();
    }
  }

  private void connectToRongCloud(String paramString)
  {
    RongIM.connect(paramString, new RongIMClient.ConnectCallback() {
      public void onError(RongIMClient.ErrorCode paramAnonymousErrorCode) {
        Toast.makeText(LoginActivity.this, "连接服务器错误", Toast.LENGTH_SHORT).show();
      }

      public void onSuccess(String paramAnonymousString) {
        Toast.makeText(LoginActivity.this.getApplicationContext(), "连接服务器成功", Toast.LENGTH_SHORT).show();
        LoginActivity.this.startActivity(new Intent(LoginActivity.this, ViewPagerActivity.class));
      }

      public void onTokenIncorrect() {
        TokenMsg localTokenMsg = new TokenMsg(LoginActivity.chatManager.getUserId(), LoginActivity.chatManager.getUserName());
        LoginActivity.chatManager.getMsgClient().send(localTokenMsg);
        LoginActivity.this.connectToRongCloud(LoginActivity.chatManager.getUserToken());
      }
    });
  }

  private void getViewComponent()
  {
    this.etName = ((EditText)findViewById(R.id.edtname));
    findViewById(R.id.btnlogin).setOnClickListener(new OnClickListener() {
      public void onClick(View paramAnonymousView) {
        LoginActivity.this.login();
      }
    });
  }

  private void login()
  {
    if (this.etName.getText().toString().length() == 0)
    {
      Toast.makeText(getApplicationContext(), "请输入聊天名称后再登陆", Toast.LENGTH_SHORT).show();
      return;
    }
    chatManager = ChatManager.getChatManager(this);
    AsyncTask<String,Void,String> local2 = new AsyncTask<String,Void,String>()
    {
      protected String doInBackground(String[] paramAnonymousArrayOfString)
      {
        String str = LoginActivity.chatManager.connect(paramAnonymousArrayOfString[0], Integer.parseInt(paramAnonymousArrayOfString[1]), paramAnonymousArrayOfString[2]);
        if (!str.equals("err"))
        {
          ClientOnMsg localClientOnMsg = new ClientOnMsg(LoginActivity.chatManager.getUserName(), LoginActivity.chatManager.getUserId(), LoginActivity.chatManager.getUserToken());
          LoginActivity.chatManager.getMsgClient().send(localClientOnMsg);

          CreateUserMsg localCreateUserMsg = new CreateUserMsg(LoginActivity.chatManager.getUserName(), LoginActivity.chatManager.getUserId(), LoginActivity.chatManager.getUserToken());
          LoginActivity.chatManager.getMsgClient().send(localCreateUserMsg);

          AskClientsMsg localAskClientsMsg = new AskClientsMsg(LoginActivity.chatManager.getUserId(), 1);
          LoginActivity.chatManager.getMsgClient().send(localAskClientsMsg);
          while (ChatManager.msgRecvManager.AskClients == 0) {
            try {
              Thread.currentThread();
              Thread.sleep(1000L);
            } catch (InterruptedException localInterruptedException2) {
              localInterruptedException2.printStackTrace();
            }
          }

          GetFriendMsg localGetFriendMsg = new GetFriendMsg(LoginActivity.chatManager.getUserId());
          LoginActivity.chatManager.getMsgClient().send(localGetFriendMsg);
          while (ChatManager.msgRecvManager.GetFriends == 0) {
            try {
              Thread.currentThread();
              Thread.sleep(1000L);
            } catch (InterruptedException localInterruptedException1) {
              localInterruptedException1.printStackTrace();
            }
          }

          LocalDataUtil.getApp(LoginActivity.this).setChatManager(LoginActivity.chatManager);
          LocalDataUtil.getApp(LoginActivity.this).setMsgClient(LoginActivity.chatManager.getMsgClient());
        }
        return str;
      }
    };
    String[] arrayOfString = new String[3];
    arrayOfString[0] = ServerTCP_IP;
    arrayOfString[1] = ServerTCP_Port;
    arrayOfString[2] = this.etName.getText().toString();
    local2.execute(arrayOfString);
    try
    {
      String str = (String)local2.get();
      if ((str != null) && (str.equals("success")))
      {
        connectToRongCloud(chatManager.getUserToken());
        saveLoginData();
        return;
      }
    } catch (ExecutionException e) {
      //e.printStackTrace();
      Toast.makeText(getApplicationContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
    } catch (InterruptedException e) {
      //e.printStackTrace();
      Toast.makeText(getApplicationContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
    }
  }

  private void saveLoginData()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("userId", chatManager.getUserId());
    localHashMap.put("userName", chatManager.getUserName());
    localHashMap.put("userToken", chatManager.getUserToken());
    LocalDataUtil.writeToSharePref(this, "LoginInfo", localHashMap);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    String str1 = LocalDataUtil.readFromSharePref(this, "LoginInfo", "userId");
    String str2 = PreferenceManager.getDefaultSharedPreferences(this).getString("serverip", null);
    String str3 = PreferenceManager.getDefaultSharedPreferences(this).getString("serverport", null);
    if ((str2 != null) && (str3 != null))
    {
      ServerTCP_IP = str2;
      ServerTCP_Port = str3;
    }
    if ((str1 != null) && (str1.length() > 0))
    {
      autoLogin();
      return;
    }
    setContentView(R.layout.activity_login);
    getViewComponent();
  }

  protected void onRestart()
  {
    super.onRestart();
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.HOME");
    startActivity(localIntent);
    Process.killProcess(Process.myPid());
  }
}
