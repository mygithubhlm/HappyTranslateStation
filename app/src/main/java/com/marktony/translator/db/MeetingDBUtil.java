package com.marktony.translator.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * Created by lemonhuang on 2017/5/3.
 */

public class MeetingDBUtil {
    public static Boolean queryIfItemExist( MeetingDatebaseHelper dbhelper, String queryString){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = db.query("meeting",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                String s = cursor.getString(cursor.getColumnIndex("title"));
                if (queryString.equals(s)){
                    return true;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return false;
    }

    public static void insertValue(MeetingDatebaseHelper dbhelper, ContentValues values){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        db.insert("meeting",null,values);
        System.out.print("Insert successfully!");
    }

    public static void deleteValue(MeetingDatebaseHelper dbhelper,String deleteString){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        db.delete("meeting","title = ?",new String[]{deleteString});
    }
}
