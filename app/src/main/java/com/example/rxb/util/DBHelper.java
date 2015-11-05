package com.example.rxb.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/10/22.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyDB";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists friends"+
                "(_id integer primary key autoincrement," +
                "userId varchar not null," +
                "userName varchar not null," +
                "userPic varchar," +
                "userToken varchar not null)";

        db.execSQL(sql);

        sql = "create table if not exists guests"+
                "(_id integer primary key autoincrement," +
                "userId varchar not null," +
                "userName varchar not null," +
                "userPic varchar," +
                "userToken varchar not null)";

        db.execSQL(sql);
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
