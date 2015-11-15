package com.example.rxb.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.example.rxb.App;
import com.example.rxb.client.ChatManager;
import com.example.rxb.client.Guest;
import com.example.rxb.client.MsgClient;
import com.example.rxb.msg.AskClientsMsg;
import com.example.rxb.msg.ClientOffMsg;
import com.example.rxb.msg.MsgRecvManager;
import com.example.rxb.util.DBManager;
import com.example.rxb.util.LocalDataUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import io.rong.imkit.RongIM;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OnlineClientActivity extends AppCompatActivity
{
  private SimpleAdapter adapter;
  private ChatManager chatManager;
  private int currentPage = 1;
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
    for (int i = 0; i < this.onlineList.size(); i++)
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("userimage", R.drawable.rc_default_portrait);
      localHashMap.put("username", ((Guest)this.onlineList.get(i)).getGuestName());
      localHashMap.put("userid", ((Guest)this.onlineList.get(i)).getGuestId());
      localHashMap.put("usertoken", ((Guest)this.onlineList.get(i)).getGuestToken());
      localArrayList.add(localHashMap);
    }
    return localArrayList;
  }

  private void getOnlineClient()
  {
    this.onlineList.clear();
    AskClientsMsg localAskClientsMsg = new AskClientsMsg(this.chatManager.getUserId(), 1);
    this.chatManager.getMsgClient().send(localAskClientsMsg);
    while (ChatManager.msgRecvManager.AskClients == 0)
      try
      {
        Thread.currentThread();
        Thread.sleep(1000L);
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
  }

  private void getOnlineClientPage()
  {
    AskClientsMsg localAskClientsMsg = new AskClientsMsg(this.chatManager.getUserId(), this.currentPage);
    this.chatManager.getMsgClient().send(localAskClientsMsg);
    while (ChatManager.msgRecvManager.AskClients == 0)
      try
      {
        Thread.currentThread();
        Thread.sleep(1000L);
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
  }

  private void getViewComponent()
  {
    this.lv = ((PullToRefreshListView)findViewById(R.id.clientlist));

    this.lv.setOnRefreshListener(new OnRefreshListener<ListView>()
    {
      @Override
      public void onRefresh(PullToRefreshBase<ListView> paramAnonymousPullToRefreshBase)
      {
        AsyncTask<String,Void,Void> local1 = new AsyncTask<String,Void,Void>()
        {
          protected Void doInBackground(String[] paramAnonymous2ArrayOfString)
          {
            OnlineClientActivity.this.getOnlineClient();
            return null;
          }

          protected void onPostExecute(Void paramAnonymous2Void)
          {
            super.onPostExecute(paramAnonymous2Void);
            Iterator localIterator = OnlineClientActivity.this.chatManager.guestList.iterator();
            while (localIterator.hasNext())
            {
              Guest localGuest = (Guest)localIterator.next();
              OnlineClientActivity.this.onlineList.add(localGuest);
            }
            OnlineClientActivity.this.bindListView();
            OnlineClientActivity.this.lv.onRefreshComplete();
            //OnlineClientActivity.access$502(OnlineClientActivity.this, 1);
          }
        };
        String[] arrayOfString = new String[2];
        arrayOfString[0] = OnlineClientActivity.this.chatManager.getUserId();
        arrayOfString[1] = String.valueOf(1);
        local1.execute(arrayOfString);
      }
    });

    this.lv.setOnItemClickListener(new OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        String userId = ((TextView)paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(R.id.userid)).getText().toString();
        String userName = ((TextView)paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(R.id.username)).getText().toString();
        String userToken = ((TextView)paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(R.id.usertoken)).getText().toString();
        if (LocalDataUtil.getApp(OnlineClientActivity.this).getDbManager().queryGuestByID("guests", userId).getGuestId() == null)
        {
          Guest localGuest = new Guest(userId, userName, userToken, "");
          LocalDataUtil.getApp(OnlineClientActivity.this).getDbManager().add("guests", localGuest);
        }
        if (RongIM.getInstance() != null)
          RongIM.getInstance().startPrivateChat(OnlineClientActivity.this, userId, "hello");
      }
    });

    this.lv.setOnLastItemVisibleListener(new OnLastItemVisibleListener()
    {
      public void onLastItemVisible()
      {
        if (OnlineClientActivity.this.chatManager.getPageEndFlag() == 0)
        {
          AsyncTask<String,Void,Void> local1 = new AsyncTask<String,Void,Void>()
          {
            protected Void doInBackground(String[] paramAnonymous2ArrayOfString)
            {
              //OnlineClientActivity.access$508(OnlineClientActivity.this);
              OnlineClientActivity.this.getOnlineClientPage();
              return null;
            }

            protected void onPostExecute(Void paramAnonymous2Void)
            {
              super.onPostExecute(paramAnonymous2Void);
              Iterator localIterator = OnlineClientActivity.this.chatManager.guestList.iterator();
              while (localIterator.hasNext())
              {
                Guest localGuest = (Guest)localIterator.next();
                OnlineClientActivity.this.onlineList.add(localGuest);
              }
              OnlineClientActivity.this.bindListView();
              OnlineClientActivity.this.lv.onRefreshComplete();
            }
          };
          String[] arrayOfString = new String[2];
          arrayOfString[0] = OnlineClientActivity.this.chatManager.getUserId();
          arrayOfString[1] = String.valueOf(OnlineClientActivity.this.currentPage);
          local1.execute(arrayOfString);
          ((ListView)OnlineClientActivity.this.lv.getRefreshableView())
                  .setSelection(10 + ((ListView) OnlineClientActivity.this.lv.getRefreshableView())
                          .getLastVisiblePosition());
        }
      }
    });
  }

  private void initList()
  {
    AsyncTask<String,Void,Void> local5 = new AsyncTask<String,Void,Void>()
    {
      protected Void doInBackground(String[] paramAnonymousArrayOfString)
      {
        OnlineClientActivity.this.getOnlineClient();
        return null;
      }

      protected void onPostExecute(Void paramAnonymousVoid)
      {
        super.onPostExecute(paramAnonymousVoid);
        Iterator localIterator = OnlineClientActivity.this.chatManager.guestList.iterator();
        while (localIterator.hasNext())
        {
          Guest localGuest = (Guest)localIterator.next();
          OnlineClientActivity.this.onlineList.add(localGuest);
        }
        OnlineClientActivity.this.bindListView();
        OnlineClientActivity.this.lv.onRefreshComplete();
      }
    };
    String[] arrayOfString = new String[2];
    arrayOfString[0] = this.chatManager.getUserId();
    arrayOfString[1] = String.valueOf(1);
    local5.execute(arrayOfString);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_online_list);
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
        ChatManager localChatManager = LocalDataUtil.getApp(OnlineClientActivity.this).getChatManager();
        ClientOffMsg localClientOffMsg = new ClientOffMsg(localChatManager.getUserId(), localChatManager.getUserName());
        localChatManager.getMsgClient().send(localClientOffMsg);
      }
    }
    .start();
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
}
