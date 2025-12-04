package com.example.m_hike.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "mhike.db";


    public static final int DB_VERSION = 2;

    public static final String TABLE_HIKES = "hikes";
    public static final String H_ID = "_id";
    public static final String H_NAME = "name";
    public static final String H_LOCATION = "location";
    public static final String H_DATE = "date";
    public static final String H_PARKING = "parking";
    public static final String H_LENGTH = "length";
    public static final String H_DIFFICULTY = "difficulty";
    public static final String H_DESCRIPTION = "description";


    public static final String H_IMAGE_PATH = "image_path";

    public static final String H_EXTRA1 = "extra1";
    public static final String H_EXTRA2 = "extra2";

    public static final String TABLE_OBS = "observations";
    public static final String O_ID = "_id";
    public static final String O_HIKE_ID = "hike_id";
    public static final String O_TEXT = "obs_text";
    public static final String O_TIMESTAMP = "timestamp";
    public static final String O_COMMENTS = "comments";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createHikes = "CREATE TABLE " + TABLE_HIKES + " (" +
                H_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                H_NAME + " TEXT NOT NULL, " +
                H_LOCATION + " TEXT NOT NULL, " +
                H_DATE + " TEXT NOT NULL, " +
                H_PARKING + " INTEGER NOT NULL, " +
                H_LENGTH + " TEXT NOT NULL, " +
                H_DIFFICULTY + " TEXT NOT NULL, " +
                H_DESCRIPTION + " TEXT, " +
                H_IMAGE_PATH + " TEXT, " +
                H_EXTRA1 + " TEXT, " +
                H_EXTRA2 + " TEXT" +
                ");";

        String createObs = "CREATE TABLE " + TABLE_OBS + " (" +
                O_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                O_HIKE_ID + " INTEGER NOT NULL, " +
                O_TEXT + " TEXT NOT NULL, " +
                O_TIMESTAMP + " TEXT NOT NULL, " +
                O_COMMENTS + " TEXT, " +
                "FOREIGN KEY(" + O_HIKE_ID + ") REFERENCES " + TABLE_HIKES + "(" + H_ID + ") ON DELETE CASCADE" +
                ");";

        db.execSQL(createHikes);
        db.execSQL(createObs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        onCreate(db);
    }
}