package com.marktony.translator.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lemonhuang on 2017/5/3.
 */

public class NotebookDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    public static final String CREATE_NOTEBOOK = "create table Notebook("
            + "id integer primary key autoincrement,"
            + "input text,"
            + "output text)";

    public NotebookDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NOTEBOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
