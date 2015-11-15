package com.example.rxb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.example.rxb.App;
import com.example.rxb.client.ChatManager;
import com.example.rxb.client.MsgClient;
import com.example.rxb.msg.ClientOffMsg;
import com.example.rxb.util.LocalDataUtil;
import io.rong.imkit.RongIM;

public class SelfInfoActivity extends Activity
{
  private boolean isExit = false;

  private void deleteLoginData()
  {
    LocalDataUtil.clearSharePref(this, "LoginInfo");
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_self_info);
    EditText localEditText1 = (EditText)findViewById(R.id.etselfid);
    EditText localEditText2 = (EditText)findViewById(R.id.etselfname);
    ChatManager localChatManager = LocalDataUtil.getApp(this).getChatManager();
    localEditText1.setText(localChatManager.getUserId());
    localEditText2.setText(localChatManager.getUserName());
    findViewById(R.id.btnexit).setOnClickListener(new OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        new Thread()
        {
          public void run()
          {
            super.run();
            ChatManager localChatManager = LocalDataUtil.getApp(SelfInfoActivity.this).getChatManager();
            ClientOffMsg localClientOffMsg = new ClientOffMsg(localChatManager.getUserId(), localChatManager.getUserName());
            localChatManager.getMsgClient().send(localClientOffMsg);
          }
        }
        .start();
        SelfInfoActivity.this.deleteLoginData();
        SelfInfoActivity.this.finish();
        //SelfInfoActivity.access$102(SelfInfoActivity.this, true);
      }
    });
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (this.isExit)
    {
      RongIM.getInstance().disconnect();
      Process.killProcess(Process.myPid());
    }
  }
}
