package com.example.rxb.activity;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.rxb.App;
import com.example.rxb.adapter.MyPagerAdapter;
import com.example.rxb.client.ChatManager;
import com.example.rxb.client.MsgClient;
import com.example.rxb.msg.ClientOffMsg;
import com.example.rxb.util.DBManager;
import com.example.rxb.util.LocalDataUtil;
import io.rong.imkit.RongIM;
import java.util.ArrayList;

public class ViewPagerActivity extends AppCompatActivity
{
  public static DBManager dbManager;
  private LocalActivityManager activityManager;
  private Context context;
  private ImageButton imb1;
  private ImageButton imb2;
  private ImageButton imb3;
  private LinearLayout linearConversation;
  private LinearLayout linearFriends;
  private LinearLayout linearOnline;
  private ArrayList<View> list = new ArrayList();
  private TextView tvConversation;
  private TextView tvFriends;
  private TextView tvOnline;
  private ViewPager viewPager;

  private void getComponent()
  {
    this.context = this;
    this.viewPager = ((ViewPager)findViewById(R.id.viewpager));
    this.linearConversation = ((LinearLayout)findViewById(R.id.linearconversation));
    this.linearOnline = ((LinearLayout)findViewById(R.id.linearonline));
    this.linearFriends = ((LinearLayout)findViewById(R.id.linearfriend));
    this.linearConversation.setOnClickListener(new MyOnClickListener(0));
    this.linearOnline.setOnClickListener(new MyOnClickListener(1));
    this.linearFriends.setOnClickListener(new MyOnClickListener(2));
    this.imb1 = ((ImageButton)findViewById(R.id.imb1));
    this.imb2 = ((ImageButton)findViewById(R.id.imb2));
    this.imb3 = ((ImageButton)findViewById(R.id.imb3));
    this.tvConversation = ((TextView)findViewById(R.id.tvconversation));
    this.tvOnline = ((TextView)findViewById(R.id.tvonline));
    this.tvFriends = ((TextView)findViewById(R.id.tvfriend));
  }

  private View getView(String paramString, Intent paramIntent)
  {
    return this.activityManager.startActivity(paramString, paramIntent).getDecorView();
  }

  private void initPagerViewer()
  {
    this.list = new ArrayList();
    Intent localIntent1 = new Intent(this.context, ConversationListActivity.class);
    this.list.add(getView("ConversationList", localIntent1));

    Intent localIntent2 = new Intent(this.context, OnlineClientActivity.class);
    this.list.add(getView("OnlineClient", localIntent2));

    Intent localIntent3 = new Intent(this.context, MyFriendsActivity.class);
    this.list.add(getView("Friends", localIntent3));

    this.viewPager.setAdapter(new MyPagerAdapter(this.list));
    this.viewPager.setCurrentItem(0);
    this.tvConversation.setTextColor(Color.rgb(0, 255, 0));

    this.viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
  }

  private void resetColor()
  {
    this.tvConversation.setTextColor(Color.rgb(0, 0, 0));
    this.tvOnline.setTextColor(Color.rgb(0, 0, 0));
    this.tvFriends.setTextColor(Color.rgb(0, 0, 0));
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.activity_view_pager);
    this.activityManager = new LocalActivityManager(this, true);
    this.activityManager.dispatchCreate(paramBundle);
    getComponent();
    initPagerViewer();
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(R.menu.menu_view_pager, paramMenu);
    return true;
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    RongIM.getInstance().disconnect();
    new Thread()
    {
      public void run()
      {
        super.run();
        ChatManager localChatManager = LocalDataUtil.getApp(ViewPagerActivity.this).getChatManager();
        ClientOffMsg localClientOffMsg = new ClientOffMsg(localChatManager.getUserId(), localChatManager.getUserName());
        localChatManager.getMsgClient().send(localClientOffMsg);
      }
    }
    .start();
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.addCategory("android.intent.category.HOME");
    startActivity(localIntent);
    Process.killProcess(Process.myPid());
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  public boolean onOptionsItemSelected(MenuItem menuItem)
  {
    switch (menuItem.getItemId()){
      case R.id.addfriend:
          startActivity(new Intent(this, AddFriendActivity.class));
          break;
      case R.id.discussion:
        startActivity(new Intent(this, UserChooseActivity.class));
        break;
      case R.id.scan:

        break;
      case R.id.selfinfo:
        startActivity(new Intent(this, SelfInfoActivity.class));
        break;

    }
    return super.onOptionsItemSelected(menuItem);
  }

  private class MyOnClickListener implements OnClickListener
  {
    private int index = 0;

    public MyOnClickListener(int i)
    {
      this.index = i;
    }

    public void onClick(View paramView)
    {
      ViewPagerActivity.this.viewPager.setCurrentItem(this.index);
    }
  }

  private class MyOnPageChangeListener implements OnPageChangeListener
  {
    private MyOnPageChangeListener()
    {
    }

    public void onPageScrollStateChanged(int paramInt)
    {
    }

    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
    {
    }

    public void onPageSelected(int paramInt)
    {
      switch (ViewPagerActivity.this.viewPager.getCurrentItem())
      {
      default:
        return;
      case 0:
        ViewPagerActivity.this.resetColor();
        ViewPagerActivity.this.tvConversation.setTextColor(Color.rgb(0, 255, 0));
        return;
      case 1:
        ViewPagerActivity.this.resetColor();
        ViewPagerActivity.this.tvOnline.setTextColor(Color.rgb(0, 255, 0));
        return;
      case 2:
      }
      ViewPagerActivity.this.resetColor();
      ViewPagerActivity.this.tvFriends.setTextColor(Color.rgb(0, 255, 0));
    }
  }
}
