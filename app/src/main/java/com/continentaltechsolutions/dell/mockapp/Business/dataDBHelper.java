package com.continentaltechsolutions.dell.mockapp.Business;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DELL on 02-Aug-17.
 */

public class dataDBHelper extends SQLiteOpenHelper {

    public dataDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tblCreateScript = "CREATE TABLE PEOPLE(ID INTEGER PRIMARY KEY " +
                " AUTOINCREMENT NOT NULL, " +
                "NAME TEXT NOT NULL, "+
                "PHONE TEXT NOT NULL);";
        db.execSQL(tblCreateScript);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
