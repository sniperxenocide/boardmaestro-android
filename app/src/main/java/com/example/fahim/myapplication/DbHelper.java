package com.example.fahim.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDB.db";
    public static final String TABLE_NAME = "data_table";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_VALUE = "value";
    public Context context;

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("+COLUMN_NAME+" text UNIQUE PRIMARY KEY,"+COLUMN_VALUE+" text)" );
        System.out.println("*************************** Initiating Database ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public boolean insertData (String name, String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME+" WHERE name = \""+name+"\"");

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("value", value);
        db.insert(TABLE_NAME, null, contentValues);

        System.out.println("***********Inserting data: "+name+"  "+value);
        return true;
    }

    public String getData(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT value FROM "+TABLE_NAME+" WHERE name = \""+name+"\"", null );
        if(res.getCount()==0) return  null;
        res.moveToFirst();
        System.out.println("***********Getting Data: "+res.getString(res.getColumnIndex("value")));
        return res.getString(res.getColumnIndex("value"));
    }

    public void deleteData(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME+" WHERE name = \""+name+"\"");
        System.out.println("*********** Deleting data: "+name);
    }

    public boolean clearTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        return true;
    }

    public void printTable()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM "+TABLE_NAME+"", null );
        System.out.println("********************** Printing Database Table **********************");
        for(int i=0;i<res.getCount();i++)
        {
            res.moveToFirst();
            res.move(i);
            System.out.println(".");
            System.out.println(res.getString(res.getColumnIndex("name"))+"      "+res.getString(res.getColumnIndex("value")));
        }
        System.out.println("********************** Printing Database Table End **********************");
    }

    public long getSize()
    {
        File f = context.getDatabasePath(DATABASE_NAME);
        long dbSize = f.length();
        return dbSize;
    }
}
