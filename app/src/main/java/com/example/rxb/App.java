package com.example.rxb;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.example.rxb.client.ChatManager;
import com.example.rxb.client.Guest;
import com.example.rxb.client.MsgClient;
import com.example.rxb.util.DBManager;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by rxb on 2015/10/10.
 */
public class App extends Application {
    private ChatManager chatManager;
    private MsgClient msgClient;
    private DBManager dbManager;

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
        }
        dbManager = new DBManager(this);
    }

    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public void setMsgClient(MsgClient msgClient) {
        this.msgClient = msgClient;
    }

    public MsgClient getMsgClient() {
        return msgClient;
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
