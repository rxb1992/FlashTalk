package com.example.rxb.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import com.example.rxb.client.ChatManager;
import com.example.rxb.client.Guest;
import com.example.rxb.msg.ClientOffMsg;
import com.example.rxb.util.DBManager;
import com.example.rxb.util.LocalDataUtil;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.UserInfoProvider;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class ConversationListActivity extends AppCompatActivity implements UserInfoProvider {

  protected void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_conversation_list);
    RongIM.setUserInfoProvider(this, true);
    ((ConversationListFragment)getSupportFragmentManager().findFragmentById(R.id.conversationlist))
            .setUri(Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")
                    .build());
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
    new Thread()
    {
      public void run()
      {
        super.run();
        ChatManager localChatManager = LocalDataUtil.getApp(ConversationListActivity.this).getChatManager();
        ClientOffMsg localClientOffMsg = new ClientOffMsg(localChatManager.getUserId(), localChatManager.getUserName());
        localChatManager.getMsgClient().send(localClientOffMsg);
      }
    }
    .start();
    RongIM.getInstance().disconnect();
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  public UserInfo getUserInfo(String userId) {
    LocalDataUtil.getApp(this).getChatManager();
    DBManager localDBManager = LocalDataUtil.getApp(this).getDbManager();
    Guest localGuest1 = localDBManager.queryGuestByID("friends", userId);
    Guest localGuest2 = localDBManager.queryGuestByID("guests", userId);
    String name = userId;
    Uri pic = null;
    if ((localGuest1.getGuestId() == null) || (localGuest1.getGuestId() == ""))
    {
      if(localGuest2.getGuestId() != null||localGuest2.getGuestId().length()>0){
        name = localGuest2.getGuestName();
        pic = localGuest2.getGuestPic()==null||localGuest2.getGuestPic()==""?null:Uri.parse(localGuest2.getGuestPic());
      }
    }
    else{
      name = localGuest1.getGuestName();
      pic = localGuest1.getGuestPic()==null||localGuest1.getGuestPic()==""?null:Uri.parse(localGuest1.getGuestPic());
    }
    return new UserInfo(userId,name,pic);
  }

}
