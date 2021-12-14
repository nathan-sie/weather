package com.weather.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
    String sql = "create table if not exists user(" +
            "id integer primary key autoincrement," +
            "username varchar(20) not null," +
            "password varchar(20) not null," +
            "phone varchar(20) default ''," +
            "address varchar(20) default '')";
    db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("当前版本为"+newVersion);
    }
}
