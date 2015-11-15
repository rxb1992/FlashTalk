package com.example.rxb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import com.example.rxb.App;
import com.example.rxb.client.ChatManager;
import com.example.rxb.client.MsgClient;
import com.example.rxb.msg.AddFriendMsg;
import com.example.rxb.util.LocalDataUtil;

public class AddFriendActivity extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_add_friend);
    final ChatManager localChatManager = LocalDataUtil.getApp(this).getChatManager();
    final EditText localEditText = (EditText)findViewById(R.id.etfriendid);
    findViewById(R.id.btnaddfriend).setOnClickListener(new OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        final String str = localEditText.getText().toString();
        if (str.length() == 0)
        {
          Toast.makeText(AddFriendActivity.this, "请输入用户ID添加", Toast.LENGTH_SHORT).show();
          return;
        }
        new Thread()
        {
          public void run()
          {
            super.run();
            AddFriendMsg localAddFriendMsg = new AddFriendMsg(localChatManager.getUserId(), str);
            localChatManager.getMsgClient().send(localAddFriendMsg);
          }
        }
        .start();
        AddFriendActivity.this.finish();
      }
    });
  }
}

/* Location:           C:\Users\rxb\Desktop\dex2jar-0.0.9.15\classes_dex2jar.jar
 * Qualified Name:     com.example.rxb.activity.AddFriendActivity
 * JD-Core Version:    0.6.2
 */