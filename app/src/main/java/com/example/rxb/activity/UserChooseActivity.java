package com.example.rxb.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rxb.App;
import com.example.rxb.client.ChatManager;
import com.example.rxb.client.Guest;
import com.example.rxb.client.MsgClient;
import com.example.rxb.msg.GetFriendMsg;
import com.example.rxb.msg.MsgRecvManager;
import com.example.rxb.util.LocalDataUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import io.rong.imkit.RongIM;
import java.util.ArrayList;
import java.util.List;

public class UserChooseActivity extends AppCompatActivity
{
  private UserChooseAdapter adapter;
  private ChatManager chatManager;
  private List<String> choosedId = new ArrayList();
  private int currentPage = 1;
  private LinearLayout linear;
  private PullToRefreshListView lv;
  private MsgClient msgClient;

  private void getComponent()
  {
    this.linear = ((LinearLayout)findViewById(R.id.userlist));
    this.lv = ((PullToRefreshListView)findViewById(R.id.clientlist4dis));
    this.lv.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
        //Toast.makeText(UserChooseActivity.this.getApplicationContext(), "123", Toast.LENGTH_SHORT).show();
        TextView tvID = (TextView) paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(R.id.userlistid);
//        TextView localTextView2 = (TextView) paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(2131624112);
//        TextView localTextView3 = (TextView) paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(2131624114);
        CheckBox localCheckBox = (CheckBox) paramAnonymousAdapterView.getChildAt(paramAnonymousInt).findViewById(R.id.userlistcheck);
        String str = tvID.getText().toString();
//        localTextView2.getText().toString();
//        localTextView3.getText().toString();
        if (localCheckBox.isChecked() == true) {
          localCheckBox.setChecked(false);
          UserChooseActivity.this.removeFromList(str);
        }
        else {
          localCheckBox.setChecked(true);
          UserChooseActivity.this.choosedId.add(str);
        }
//        do {
//          return;
//
//        }
//        while (str.equals(UserChooseActivity.this.chatManager.getUserId()));

      }
    });

    this.lv.setOnRefreshListener(new OnRefreshListener2<ListView>() {
      public void onPullDownToRefresh(PullToRefreshBase<ListView> paramAnonymousPullToRefreshBase) {
        new AsyncTask<Void,Void,Void>() {
          protected Void doInBackground(Void[] paramAnonymous2ArrayOfVoid) {
            UserChooseActivity.this.getFriends();
            return null;
          }

          protected void onPostExecute(Void paramAnonymous2Void) {
            super.onPostExecute(paramAnonymous2Void);
            UserChooseActivity.this.adapter.notifyDataSetChanged();
            UserChooseActivity.this.lv.onRefreshComplete();
          }
        }.execute();
      }

      public void onPullUpToRefresh(PullToRefreshBase<ListView> paramAnonymousPullToRefreshBase) {
      }
    });
  }

  private void getFriends()
  {
    this.chatManager.friendList.clear();
    GetFriendMsg localGetFriendMsg = new GetFriendMsg(this.chatManager.getUserId());
    this.chatManager.getMsgClient().send(localGetFriendMsg);
    while (ChatManager.msgRecvManager.GetFriends == 0)
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

  private void initList()
  {
    this.adapter = new UserChooseAdapter();
    this.lv.setAdapter(this.adapter);
    this.lv.onRefreshComplete();
  }

  private void removeFromList(String paramString)
  {
    if (this.choosedId.size() > 0);
    for (int i = 0; ; i++)
      if (i < this.choosedId.size())
      {
        if (((String)this.choosedId.get(i)).equals(paramString))
          this.choosedId.remove(i);
      }
      else
        return;
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_user_choose);
    getComponent();
    this.chatManager = LocalDataUtil.getApp(this).getChatManager();
    this.chatManager.setPageEndFlag(0);
    this.msgClient = LocalDataUtil.getApp(this).getMsgClient();
    initList();
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(R.menu.menu_user_choose, paramMenu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem menuItem)
  {

    switch (menuItem.getItemId()){
      case R.id.confirm:
        RongIM.getInstance().createDiscussionChat(this, this.choosedId, "我的讨论组");
        break;
    }
    return super.onOptionsItemSelected(menuItem);
  }

  class UserChooseAdapter extends BaseAdapter
  {
    UserChooseAdapter()
    {
    }

    public int getCount()
    {
      return UserChooseActivity.this.chatManager.friendList.size();
    }

    public Object getItem(int paramInt)
    {
      return UserChooseActivity.this.chatManager.friendList.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return 0L;
    }

    //通过paramView和ViewHolder来提升listview在已加载的数据时滑动的效率
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
      ViewHolder viewHolder;
      if(paramView==null){
        paramView = LayoutInflater.from(UserChooseActivity.this.getApplicationContext()).inflate(R.layout.cell_userchoose_list, null);
        viewHolder = new ViewHolder();
        viewHolder.imageView = (ImageView)paramView.findViewById(R.id.userlistimage);
        viewHolder.tvID = (TextView)paramView.findViewById(R.id.userlistid);
        viewHolder.tvName =  (TextView)paramView.findViewById(R.id.userlistname);
        viewHolder.tvToken =  (TextView)paramView.findViewById(R.id.userlisttoken);

        paramView.setTag(viewHolder);
      }else{
        viewHolder = (ViewHolder) paramView.getTag();
      }
//      View localView = LayoutInflater.from(UserChooseActivity.this.getApplicationContext()).inflate(R.layout.cell_userchoose_list, null);
//      ImageView localImageView = (ImageView)localView.findViewById(R.id.userlistimage);
//      TextView tvName = (TextView)localView.findViewById(R.id.userlistname);
//      TextView tvID = (TextView)localView.findViewById(R.id.userlistid);
//      TextView tvToken = (TextView)localView.findViewById(R.id.userlisttoken);
      Guest localGuest = (Guest)getItem(paramInt);
      viewHolder.imageView.setImageResource(R.drawable.rc_default_portrait);
      viewHolder.tvName.setText(localGuest.getGuestName());
      viewHolder.tvID.setText(localGuest.getGuestId());
      viewHolder.tvToken.setText(localGuest.getGuestToken());
      return paramView;
    }
  }

  //通过paramView和ViewHolder来提升listview在已加载的数据时滑动的效率
  public static class ViewHolder{
    ImageView imageView;
    TextView tvName;
    TextView tvID;
    TextView tvToken;
  }
}
