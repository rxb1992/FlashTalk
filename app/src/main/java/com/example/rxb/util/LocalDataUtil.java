package com.example.rxb.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.rxb.App;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by rxb on 2015/10/20.
 */
public class LocalDataUtil {
    private static SharedPreferences preferences = null;
    private static SharedPreferences.Editor editor = null;

    //向SharePreferences写数据
    public static void writeToSharePref(Activity context,String fileName,HashMap<String,String> hashMap){
        preferences = context.getSharedPreferences(fileName,Activity.MODE_PRIVATE);
        editor = preferences.edit();
        Iterator iter = hashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            editor.putString(entry.getKey().toString(),entry.getValue().toString());
            editor.commit();
        }
    }
    //向SharePreferences写数据
    public static void writeToSharePref(Activity context,String fileName,String key,String value){
        preferences = context.getSharedPreferences(fileName,Activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(key,value);
    }
    //从SharePreferences读数据
    public static String readFromSharePref(Activity context,String fileName,String key){
        preferences = context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        return preferences.getString(key,"");//第二个参数是获取的默认值
    }
    //清空SharePreferences的数据
    public static void clearSharePref(Activity context,String fileName){
        preferences = context.getSharedPreferences(fileName,Activity.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.commit();
    }



    //获取全局变量所在实体App
    public static App getApp(Activity context){
        return  (App)context.getApplicationContext();
    }
}
