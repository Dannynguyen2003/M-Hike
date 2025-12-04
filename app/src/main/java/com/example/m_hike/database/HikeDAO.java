package com.example.m_hike.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.m_hike.models.Hike;

import java.util.ArrayList;
import java.util.List;

public class HikeDAO {
    private DBHelper helper;

    public HikeDAO(Context ctx) {
        helper = new DBHelper(ctx);
    }

    public long insert(Hike h) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.H_NAME, h.getName());
        cv.put(DBHelper.H_LOCATION, h.getLocation());
        cv.put(DBHelper.H_DATE, h.getDate());
        cv.put(DBHelper.H_PARKING, h.isParkingAvailable() ? 1 : 0);
        cv.put(DBHelper.H_LENGTH, h.getLength());
        cv.put(DBHelper.H_DIFFICULTY, h.getDifficulty());
        cv.put(DBHelper.H_DESCRIPTION, h.getDescription());
        cv.put(DBHelper.H_EXTRA1, h.getExtra1());
        cv.put(DBHelper.H_EXTRA2, h.getExtra2());
        long id = db.insert(DBHelper.TABLE_HIKES, null, cv);
        db.close();
        return id;
    }


    public long update(Hike hike) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.H_NAME, hike.getName());
        values.put(DBHelper.H_LOCATION, hike.getLocation());
        values.put(DBHelper.H_DATE, hike.getDate());
        values.put(DBHelper.H_PARKING, hike.isParkingAvailable() ? 1 : 0);
        values.put(DBHelper.H_LENGTH, hike.getLength());
        values.put(DBHelper.H_DIFFICULTY, hike.getDifficulty());
        values.put(DBHelper.H_DESCRIPTION, hike.getDescription());
        values.put(DBHelper.H_IMAGE_PATH, hike.getImagePath());


        return db.update(DBHelper.TABLE_HIKES, values,
                DBHelper.H_ID + " = ?",
                new String[]{String.valueOf(hike.getId())});
    }

    public boolean delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete(DBHelper.TABLE_HIKES, DBHelper.H_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public Hike getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_HIKES, null, DBHelper.H_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Hike h = null;
        if (c != null && c.moveToFirst()) {
            h = cursorToHike(c);
            c.close();
        }
        db.close();
        return h;
    }

    public List<Hike> getAll() {
        List<Hike> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_HIKES, null, null, null, null, null, DBHelper.H_NAME + " ASC");
        if (c != null) {
            while (c.moveToNext()) {
                list.add(cursorToHike(c));
            }
            c.close();
        }
        db.close();
        return list;
    }

    public List<Hike> searchByName(String q) {
        List<Hike> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_HIKES, null, DBHelper.H_NAME + " LIKE ?", new String[]{ "%" + q + "%" }, null, null, DBHelper.H_NAME + " ASC");
        if (c != null) {
            while (c.moveToNext()) {
                list.add(cursorToHike(c));
            }
            c.close();
        }
        db.close();
        return list;
    }

    public List<Hike> advancedSearch(String name, String location, String length, String date) {
        List<String> args = new ArrayList<>();
        StringBuilder where = new StringBuilder();
        if (name != null && !name.isEmpty()) {
            where.append(DBHelper.H_NAME + " LIKE ?");
            args.add("%" + name + "%");
        }
        if (location != null && !location.isEmpty()) {
            if (where.length() > 0) where.append(" AND ");
            where.append(DBHelper.H_LOCATION + " LIKE ?");
            args.add("%" + location + "%");
        }
        if (length != null && !length.isEmpty()) {
            if (where.length() > 0) where.append(" AND ");
            where.append(DBHelper.H_LENGTH + " LIKE ?");
            args.add("%" + length + "%");
        }
        if (date != null && !date.isEmpty()) {
            if (where.length() > 0) where.append(" AND ");
            where.append(DBHelper.H_DATE + " = ?");
            args.add(date);
        }

        List<Hike> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c;
        if (where.length() == 0) {
            c = db.query(DBHelper.TABLE_HIKES, null, null, null, null, null, DBHelper.H_NAME + " ASC");
        } else {
            c = db.query(DBHelper.TABLE_HIKES, null, where.toString(), args.toArray(new String[0]), null, null, DBHelper.H_NAME + " ASC");
        }
        if (c != null) {
            while (c.moveToNext()) {
                list.add(cursorToHike(c));
            }
            c.close();
        }
        db.close();
        return list;
    }

    public void deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DBHelper.TABLE_OBS, null, null); // clear observations
        db.delete(DBHelper.TABLE_HIKES, null, null);
        db.close();
    }

    private Hike cursorToHike(Cursor c) {
        Hike h = new Hike();
        h.setId(c.getLong(c.getColumnIndexOrThrow(DBHelper.H_ID)));
        h.setName(c.getString(c.getColumnIndexOrThrow(DBHelper.H_NAME)));
        h.setLocation(c.getString(c.getColumnIndexOrThrow(DBHelper.H_LOCATION)));
        h.setDate(c.getString(c.getColumnIndexOrThrow(DBHelper.H_DATE)));
        h.setParkingAvailable(c.getInt(c.getColumnIndexOrThrow(DBHelper.H_PARKING)) == 1);
        h.setLength(c.getString(c.getColumnIndexOrThrow(DBHelper.H_LENGTH)));
        h.setDifficulty(c.getString(c.getColumnIndexOrThrow(DBHelper.H_DIFFICULTY)));
        h.setDescription(c.getString(c.getColumnIndexOrThrow(DBHelper.H_DESCRIPTION)));
        h.setExtra1(c.getString(c.getColumnIndexOrThrow(DBHelper.H_EXTRA1)));
        h.setExtra2(c.getString(c.getColumnIndexOrThrow(DBHelper.H_EXTRA2)));
        return h;
    }
}
