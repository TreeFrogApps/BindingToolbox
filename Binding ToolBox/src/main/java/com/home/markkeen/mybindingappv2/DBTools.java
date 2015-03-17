package com.home.markkeen.mybindingappv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBTools extends SQLiteOpenHelper {

    public DBTools(Context applicationContext) {

        super(applicationContext, "calculations.db", null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase database) {

        String query = "CREATE TABLE spineHistory ( widthId INTEGER PRIMARY KEY, name TEXT, date DATE, pageWidth TEXT, pageHeight TEXT, " +
                "pageCount TEXT, coverWeight TEXT, textWeight TEXT, spineWidth TEXT, bookWeight TEXT)";

        database.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        String query = "DROP TABLE IF EXISTS spineHistory";

        database.execSQL(query);
        onCreate(database);
    }


    public void insertSpine(HashMap<String, String> queryValues) {

        SQLiteDatabase database = getWritableDatabase();


        ContentValues values = new ContentValues();

        values.put("name", queryValues.get("name"));
        values.put("date", queryValues.get("date"));
        values.put("pageWidth", queryValues.get("pageWidth"));
        values.put("pageHeight", queryValues.get("pageHeight"));
        values.put("pageCount", queryValues.get("pageCount"));
        values.put("coverWeight", queryValues.get("coverWeight"));
        values.put("textWeight", queryValues.get("textWeight"));
        values.put("spineWidth", queryValues.get("spineWidth"));
        values.put("bookWeight", queryValues.get("bookWeight"));

        database.insert("spineHistory", null, values);

        database.close();
    }


    public void deleteSpine() {

        SQLiteDatabase database = getWritableDatabase();

        String deleteQuery = "DELETE FROM spineHistory";

        database.execSQL(deleteQuery);

    }

    public void deleteSingleSpine(String id) {

        SQLiteDatabase database = getWritableDatabase();

        String deleteQuery = "DELETE FROM spineHistory WHERE widthId='" + id + "'";

        database.execSQL(deleteQuery);

    }


    public ArrayList<HashMap<String, String>> getAllSpines(int searchIndex) {

        ArrayList<HashMap<String, String>> spineArrayList = new ArrayList<HashMap<String, String>>();

        String[] selectQuery = new String[] { "SELECT * FROM spineHistory ORDER BY date DESC",
                                              "SELECT * FROM spineHistory ORDER BY name*1, name COLLATE NOCASE",
                                              "SELECT * FROM spineHistory ORDER BY spineWidth*1, spineWidth",
                                              "SELECT * FROM spineHistory ORDER BY bookWeight*1, bookWeight"};

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery[searchIndex], null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> spineMap = new HashMap<String, String>();

                spineMap.put("widthId", cursor.getString(0));
                spineMap.put("name", cursor.getString(1));
                spineMap.put("date", cursor.getString(2));
                spineMap.put("pageWidth", cursor.getString(3));
                spineMap.put("pageHeight", cursor.getString(4));
                spineMap.put("pageCount", cursor.getString(5));
                spineMap.put("coverWeight", cursor.getString(6));
                spineMap.put("textWeight", cursor.getString(7));
                spineMap.put("spineWidth", cursor.getString(8));
                spineMap.put("bookWeight", cursor.getString(9));

                spineArrayList.add(spineMap);
            } while (cursor.moveToNext());
        }

        return spineArrayList;

    }

    public HashMap<String, String> getWidthInfo(String id) {

        HashMap<String, String> spineMap = new HashMap<String, String>();

        // Open a database for reading only

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM spineHistory where widthId='" + id + "'";

        // rawQuery executes the query and returns the result as a Cursor

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                spineMap.put("name", cursor.getString(1));
                spineMap.put("pageWidth", cursor.getString(3));
                spineMap.put("pageHeight", cursor.getString(4));
                spineMap.put("pageCount", cursor.getString(5));
                spineMap.put("coverWeight", cursor.getString(6));
                spineMap.put("textWeight", cursor.getString(7));
                spineMap.put("spineWidth", cursor.getString(8));
                spineMap.put("bookWeight", cursor.getString(9));

            } while (cursor.moveToNext());
        }
        return spineMap;
    }


}
