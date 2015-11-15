package com.example.rxb.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.example.rxb.App;
import com.example.rxb.client.ChatManager;
import com.example.rxb.client.Guest;
import com.example.rxb.client.MsgClient;
import com.example.rxb.msg.ClientOffMsg;
import com.example.rxb.msg.DeleteFriendMsg;
import com.example.rxb.msg.GetFriendMsg;
import com.example.rxb.msg.MsgRecvManager;
import com.example.rxb.util.DBManager;
import com.example.rxb.util.LocalDataUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import io.rong.imkit.RongIM;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFriendsActivity extends AppCompatActivity
{
  private SimpleAdapter adapter;
  private ChatManager chatManager;
  private DBManager dbManager;
  private PullToRefreshListView lv;
  private MsgClient msgClient;
  private List<Guest> onlineList = new ArrayList();

  private void bindListView()
  {
    this.adapter = new SimpleAdapter(this,
            buildListForSimpleAdapter(),
            R.layout.cell_online_list,
            new String[] { "userimage", "username", "userid", "usertoken" },
            new int[] { R.id.userimage, R.id.username, R.id.userid, R.id.usertoken });
    this.lv.setAdapter(this.adapter);
  }

  private List<Map<String, Object>> buildListForSimpleAdapter()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < this.chatManager.friendList.size(); i++)
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("userimage", R.drawable.rc_default_portrait);
      localHashMap.put("username", ((Guest)this.chatManager.friendList.get(i)).getGuestName());
      localHashMap.put("userid", ((Guest)this.chatManager.friendList.get(i)).getGuestId());
      localHashMap.put("usertoken", ((Guest)this.chatManager.friendList.get(i)).getGuestToken());
      localArrayList.add(localHashMap);
    }
    return localArrayList;
  }

  private void getFriends()
  {
    this.chatManager.friendList.clear();
    GetFriendMsg localGetFriendMsg = new GetFriendMsg(this.chatManager.getUserId());
    this.chatManager.getMsgClient().send(localGetFriendMsg);
    while (ChatManager.msgRecvManager.GetFriends == 0) {
      try {
        Thread.currentThread();
        Thread.sleep(1000L);
      } catch (InterruptedException localInterruptedException) {
        localInterruptedException.printStackTrace();
      }
    }
  }

  private void getViewComponent()
  {
    this.lv = ((PullToRefreshListView)findViewById(R.id.friendlist));
    this.lv.setOnRefreshListener(new OnRefreshListener()
    {
      @Override
      public void onRefresh(PullToRefreshBase refreshView) {
        new AsyncTask<Void,Void,Void>()
        {
          protected Void doInBackground(Void[] paramAnonymous2ArrayOfVoid)
          {
            MyFriendsActivity.this.getFriends();
            return null;
          }

          protected void onPostExecute(Void paramAnonymous2Void)
          {
            super.onPostExecute(paramAnonymous2Void);
            MyFriendsActivity.this.bindListView();
            MyFriendsActivity.this.lv.onRefreshComplete();
          }
        }.execute(new Void[0]);
      }
    });

    this.lv.setOnItemClickListener(new OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        String userId = ((TextView)paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(R.id.userid)).getText().toString();
        String userName = ((TextView)paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(R.id.username)).getText().toString();
        String userToken = ((TextView)paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(R.id.usertoken)).getText().toString();
        if (LocalDataUtil.getApp(MyFriendsActivity.this).getDbManager().queryGuestByID("friends", userId).getGuestId() == null)
        {
          Guest localGuest = new Guest(userId, userName, userToken, "");
          LocalDataUtil.getApp(MyFriendsActivity.this).getDbManager().add("friends", localGuest);
        }
        if (RongIM.getInstance() != null)
          RongIM.getInstance().startPrivateChat(MyFriendsActivity.this, userId, "hello");
      }
    });
    this.lv.setOnItemLongClickListener(new OnItemLongClickListener()
    {
      public boolean onItemLongClick(final AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, final int paramAnonymousInt, long paramAnonymousLong)
      {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(MyFriendsActivity.this);
        localBuilder.setMessage("确定删除好友？");
        localBuilder.setCancelable(true);
        localBuilder.setPositiveButton("确定", new OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            final String str = ((TextView)paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(R.id.userid)).getText().toString();
            for (int i = 0; ; i++)
              if (i < MyFriendsActivity.this.chatManager.friendList.size())
              {
                if (((Guest)MyFriendsActivity.this.chatManager.friendList.get(i)).getGuestId().equals(str))
                  MyFriendsActivity.this.chatManager.friendList.remove(i);
              }
              else
              {
                MyFriendsActivity.this.adapter.notifyDataSetChanged();
                new Thread()
                {
                  public void run()
                  {
                    super.run();
                    DeleteFriendMsg localDeleteFriendMsg = new DeleteFriendMsg(MyFriendsActivity.this.chatManager.getUserId(), str);
                    MyFriendsActivity.this.chatManager.getMsgClient().send(localDeleteFriendMsg);
                  }
                }
                .start();
                return;
              }
          }
        });
        localBuilder.setNegativeButton("取消", new OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            paramAnonymous2DialogInterface.cancel();
          }
        });
        localBuilder.show();
        return true;
      }
    });
  }

  private void initList()
  {
    bindListView();
    this.lv.onRefreshComplete();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_my_friends);
    getViewComponent();
    this.chatManager = LocalDataUtil.getApp(this).getChatManager();
    this.chatManager.setPageEndFlag(0);
    this.msgClient = LocalDataUtil.getApp(this).getMsgClient();
    initList();
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    RongIM.getInstance().disconnect();
    new Thread()
    {
      public void run()
      {
        super.run();
        ChatManager localChatManager = LocalDataUtil.getApp(MyFriendsActivity.this).getChatManager();
        ClientOffMsg localClientOffMsg = new ClientOffMsg(localChatManager.getUserId(), localChatManager.getUserName());
        localChatManager.getMsgClient().send(localClientOffMsg);
      }
    }
    .start();
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
}
