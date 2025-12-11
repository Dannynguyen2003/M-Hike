package com.example.m_hike.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.m_hike.models.Observation;
import java.util.ArrayList;
import java.util.List;

public class ObservationDAO {
    private DBHelper helper;

    public ObservationDAO(Context ctx) {
        helper = new DBHelper(ctx);
    }

    public long insert(Observation o) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.O_HIKE_ID, o.getHikeId());
        cv.put(DBHelper.O_TEXT, o.getObsText());
        cv.put(DBHelper.O_TIMESTAMP, o.getTimestamp());
        cv.put(DBHelper.O_COMMENTS, o.getComments());
        long id = db.insert(DBHelper.TABLE_OBS, null, cv);
        db.close();
        return id;
    }

    public boolean update(Observation o) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.O_TEXT, o.getObsText());
        cv.put(DBHelper.O_TIMESTAMP, o.getTimestamp());
        cv.put(DBHelper.O_COMMENTS, o.getComments());
        int rows = db.update(DBHelper.TABLE_OBS, cv, DBHelper.O_ID + "=?", new String[]{String.valueOf(o.getId())});
        db.close();
        return rows > 0;
    }

    public void delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DBHelper.TABLE_OBS, DBHelper.O_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Observation> getByHike(long hikeId) {
        List<Observation> list = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_OBS, null, DBHelper.O_HIKE_ID + "=?", new String[]{String.valueOf(hikeId)}, null, null, DBHelper.O_ID + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                Observation o = new Observation();
                o.setId(c.getLong(c.getColumnIndexOrThrow(DBHelper.O_ID)));
                o.setHikeId(c.getLong(c.getColumnIndexOrThrow(DBHelper.O_HIKE_ID)));
                o.setObsText(c.getString(c.getColumnIndexOrThrow(DBHelper.O_TEXT)));
                o.setTimestamp(c.getString(c.getColumnIndexOrThrow(DBHelper.O_TIMESTAMP)));
                o.setComments(c.getString(c.getColumnIndexOrThrow(DBHelper.O_COMMENTS)));
                list.add(o);
            }
            c.close();
        }
        db.close();
        return list;
    }

    // --- THÊM HÀM NÀY ĐỂ EDIT ---
    public Observation getById(long obsId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_OBS, null, DBHelper.O_ID + "=?", new String[]{String.valueOf(obsId)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            Observation o = new Observation();
            o.setId(c.getLong(c.getColumnIndexOrThrow(DBHelper.O_ID)));
            o.setHikeId(c.getLong(c.getColumnIndexOrThrow(DBHelper.O_HIKE_ID)));
            o.setObsText(c.getString(c.getColumnIndexOrThrow(DBHelper.O_TEXT)));
            o.setTimestamp(c.getString(c.getColumnIndexOrThrow(DBHelper.O_TIMESTAMP)));
            o.setComments(c.getString(c.getColumnIndexOrThrow(DBHelper.O_COMMENTS)));
            c.close();
            return o;
        }
        return null;
    }
}