package com.marktony.translator.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lemonhuang on 2017/5/3.
 */

public class MeetingDatebaseHelper extends SQLiteOpenHelper {
    private Context context;

    public static final String CREATE_MEETING = "create table meeting("
            + "id integer primary key autoincrement,"
            + "time text,"
            + "title text,"
            + "input text,"
            + "output text)";

    public MeetingDatebaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEETING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
