package com.example.rxb.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.rxb.client.Guest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rxb on 2015/10/22.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public void add(String baseName,Guest g) {
        db.beginTransaction();  //开始事务
        try {

            db.execSQL("INSERT INTO "+baseName+" VALUES(null,?,?,?,?)",
                    new Object[]{g.getGuestId(),g.getGuestName(),g.getGuestPic(),g.getGuestToken()});

            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void add(String baseName,List<Guest> gList) {
        db.beginTransaction();  //开始事务
        try {
            for(Guest g:gList) {
                db.execSQL("INSERT INTO " + baseName + " VALUES(null,?,?,?,?);",
                        new Object[]{g.getGuestId(), g.getGuestName(), g.getGuestPic(), g.getGuestToken()});
            }

            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void deleteByID(String baseName,String id) {
        db.beginTransaction();
        try {
            db.delete(baseName, "userId = ?", new String[]{id});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }

    }

    public Guest queryGuestByID(String baseName,String userId){
        Cursor c = db.rawQuery("SELECT * FROM "+baseName+" where userId=?",new String[]{userId} );
        Guest g = new Guest();
        while (c.moveToNext()){
            g.setGuestId(c.getString(c.getColumnIndex("userId")));
            g.setGuestName(c.getString(c.getColumnIndex("userName")));
            g.setGuestPic(c.getString(c.getColumnIndex("userPic")));
            g.setGuestToken(c.getString(c.getColumnIndex("userToken")));
        }
        return  g;
    }

    public List<Guest> queryGuest(String baseName) {
        ArrayList<Guest> guests = new ArrayList<Guest>();
        Cursor c = queryTheCursor(baseName);
        while (c.moveToNext()) {
            Guest g = new Guest();
            g.setGuestId(c.getString(c.getColumnIndex("userId")));
            g.setGuestName(c.getString(c.getColumnIndex("userName")));
            g.setGuestPic(c.getString(c.getColumnIndex("userPic")));
            g.setGuestToken(c.getString(c.getColumnIndex("userToken")));
            guests.add(g);
        }
        c.close();
        return guests;
    }

    public List<Guest> queryGuestExceptSelf(String baseName,String selfID) {
        ArrayList<Guest> guests = new ArrayList<Guest>();
        Cursor c = db.rawQuery("SELECT * FROM "+baseName+" where userId!=?",new String[]{selfID} );
        Guest g = new Guest();
        while (c.moveToNext()){
            g.setGuestId(c.getString(c.getColumnIndex("userId")));
            g.setGuestName(c.getString(c.getColumnIndex("userName")));
            g.setGuestPic(c.getString(c.getColumnIndex("userPic")));
            g.setGuestToken(c.getString(c.getColumnIndex("userToken")));
            guests.add(g);
        }
        return guests;
    }

    public Cursor queryTheCursor(String baseName) {
        Cursor c = db.rawQuery("SELECT * FROM "+baseName+"", null);
        return c;
    }

    public void closeDB() {
        db.close();
    }
}
