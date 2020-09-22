package com.example.keepnotes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME="database_name";
    public static final String TABLE_NAME="table_name";

    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY,title TEXT,emessage TEXT,iv TEXT,salt TEXT)";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldi, int newi) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean addUser(String title,String msg,String iv,String salt) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("title",title);
        contentValues.put("emessage",msg);
        contentValues.put("iv",iv);
        contentValues.put("salt",salt);
        db.insert(TABLE_NAME,null,contentValues);
        return true;
    }

    public boolean deleteMessage(String title,String msg,String iv,String salt) {
        SQLiteDatabase db= this.getWritableDatabase();
        String query_1= "DELETE FROM " + TABLE_NAME  + " WHERE title= '" + title + "' AND emessage= '" + msg + "' AND iv= '" + iv + "' AND salt= '" + salt + "'";
        Log.d(TAG, "deleteName: query: " + query_1);
        Log.d(TAG, "deleteName: Deleting " + title  + " from database.");
        db.execSQL(query_1);
        return true;
    }

    public ArrayList<String> arr_title() {
        SQLiteDatabase sqLiteDatabase= this.getReadableDatabase();
        ArrayList<String> arrayList= new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor= sqLiteDatabase.rawQuery("select * from "+ TABLE_NAME,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            arrayList.add(cursor.getString(cursor.getColumnIndex("title")));
            cursor.moveToNext();
        }
        return arrayList;
    }

    public String arr_emsg(int position) {
        SQLiteDatabase sqLiteDatabase= this.getReadableDatabase();
        ArrayList<String> arrayList= new ArrayList<>();
        String element;
        @SuppressLint("Recycle") Cursor cursor= sqLiteDatabase.rawQuery("select * from "+ TABLE_NAME,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            arrayList.add(cursor.getString(cursor.getColumnIndex("emessage")));
            cursor.moveToNext();
        }

        element= arrayList.get(position);
        return element;
    }

    public String arr_iv(int position) {
        SQLiteDatabase sqLiteDatabase= this.getReadableDatabase();
        String element;
        ArrayList<String> arrayList= new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor= sqLiteDatabase.rawQuery("select * from "+ TABLE_NAME,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            arrayList.add(cursor.getString(cursor.getColumnIndex("iv")));
            cursor.moveToNext();
        }

        element= arrayList.get(position);
        return element;
    }

    public String arr_salt(int position) {
        SQLiteDatabase sqLiteDatabase= this.getReadableDatabase();
        String element;
        ArrayList<String> arrayList= new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor= sqLiteDatabase.rawQuery("select * from "+ TABLE_NAME,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            arrayList.add(cursor.getString(cursor.getColumnIndex("salt")));
            cursor.moveToNext();
        }
        element= arrayList.get(position);
        return element;
    }
}
