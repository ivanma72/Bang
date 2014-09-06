package com.dev.mhacks.bang.ivanma.bang;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.Override;

public class MySQLiteHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "puLineDB";
    private static final String LINE_TABLE = "lines";
    private static final String KEY_ID = "key";
    private static final String LINE = "line";
    private static final String[] COLUMNS = {KEY_ID,LINE};

    public MySQLiteHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_LINE_TABLE = "CREATE TABLE " + LINE_TABLE + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                LINE + " TEXT);";
        db.execSQL(CREATE_LINE_TABLE);
        //do a bunch of adds when we create the table

        //puLine line;
        //line.setKey()
        //addLine()
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Do i need this
        db.execSQL("DROP TABLE IF EXISTS puLine");
        this.onCreate(db);
    }

    public void addLine(puLine line){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, line.getKey());
        values.put(LINE, line.getLine());
        db.insert(LINE_TABLE,null,values);
        db.close();
    }
    public String getLine(int key){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(LINE_TABLE,
                COLUMNS,
                " key = ?",
                new String[] {String.valueOf(key)},
                null,null,null,null);
        if (cursor != null)
            cursor.moveToFirst();
        String line = cursor.getString(1);
        return line;
    }
    public void delete(puLine lines){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LINE_TABLE,KEY_ID + " = ?",new String[] {String.valueOf(lines.getKey())});
        db.close();
    }
}