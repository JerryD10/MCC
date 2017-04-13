package com.example.percy.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by percy on 08-04-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "chat1.db";
    public static final String TABLE_USER = "message";
    public static final String TABLE_LOGIN = "login";
    public static final String TABLE_WHAT = "what";
    public static final String TABLE_HOW = "how";
    public static final String TABLE_WHY = "why";
    public static final String TABLE_PERSONAL = "personal";
    public static final String COL_1 = "text";
    public static final String COL_2 = "state";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase mydatabase = openOrCreateDatabase("chat.db",MODE_PRIVATE,null);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_USER+" ( message_data text, state text, user text  );");
        db.execSQL("create table "+TABLE_LOGIN+" ( message_data text, reply text  );");
        db.execSQL("create table "+TABLE_HOW+" ( message_data text, reply text  );");
        db.execSQL("create table "+TABLE_WHY+" ( message_data text, reply text  );");
        db.execSQL("create table "+TABLE_WHAT+" ( message_data text, reply text  );");
        db.execSQL("create table "+TABLE_PERSONAL+" ( message_data text, reply text  );");
        insertMessage();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS message");
        onCreate(db);
    }

    public String reply(String message,String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+table,null);
        while(res.moveToNext())
        {
            if(message.contains(res.getString(0)));
                return res.getString(1);
        }
        return "I'm sorry. I don't understand what you mean";
    }

    public void insertMessage () {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("message_data", "name" );
        contentValues.put("reply", "My name is Pebbles" );
        contentValues.put("message_data", "call" );
        contentValues.put("reply", "They call me Pebbles though I prefer anything else" );
        contentValues.put("message_data", "doing");
        contentValues.put("reply", "Something productive. Unlike you. " );
        contentValues.put("message_data", "job");
        contentValues.put("reply", "My job is to be an assistant for the visually challenged" );
        db.insert("what", null, contentValues);
        ContentValues contentValues1 = new ContentValues();
        contentValues.put("message_data", "Pebbles" );
        contentValues.put("reply", "They call me Pebbles though I prefer anything else" );
        contentValues.put("message_data", "talk");
        contentValues.put("reply", "I am programmed to talk like that" );
        db.insert("why", null, contentValues1);
    }

    public Cursor viewDatabase () {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_WHAT,null);
        return res;
    }

}