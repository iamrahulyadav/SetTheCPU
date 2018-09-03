package com.ansoft.speedup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper {
    public static final String DATABASE_NAME = "profiles.sqlite";
    public static final int DATABASE_VERSION = 5;
    public static final String TABLE_NAME = "profiles";
    private Context context;
    private SQLiteDatabase db;

    private static class DBOpenHelper extends SQLiteOpenHelper {
        private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS profiles (_id INTEGER, priority INTEGER, enabled INTEGER, profile_id INTEGER, max INTEGER, min INTEGER, governor TEXT, param1 INTEGER, param2 INTEGER, param3 INTEGER, param4 INTEGER, param5 INTEGER, param6 INTEGER, param7 INTEGER, param8 INTEGER, PRIMARY KEY (_id));";

        DBOpenHelper(Context context) {
            super(context, DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
        }

        DBOpenHelper(Context context, String dbName) {
            super(context, dbName, null, DBHelper.DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STATEMENT);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("SpeedUp", "Found old version of profiles database. Upgrading.");
            safeExecSql(db, "ALTER TABLE profiles ADD param4 INT AFTER param3;");
            safeExecSql(db, "ALTER TABLE profiles ADD param5 INT AFTER param4;");
            safeExecSql(db, "ALTER TABLE profiles ADD param6 INT AFTER param5;");
            safeExecSql(db, "ALTER TABLE profiles ADD param7 INT AFTER param6;");
            safeExecSql(db, "ALTER TABLE profiles ADD param8 INT AFTER param7;");
        }

        private void safeExecSql(SQLiteDatabase db, String exec) {
            try {
                db.execSQL(exec);
            } catch (SQLiteException e) {
                Log.d("SpeedUp", "Skipping table...");
            }
        }
    }

    public DBHelper(Context context) {
        this.context = context;
        this.db = new DBOpenHelper(this.context).getWritableDatabase();
    }

    public DBHelper(Context context, String databaseName) {
        this.context = context;
        this.db = new DBOpenHelper(this.context, databaseName).getWritableDatabase();
    }

    public long insert(int priority, int enabled, int profile_id, int max, int min, String governor, int param1, int param2, int param3, int param4, int param5) {
        ContentValues values = new ContentValues();
        values.put("priority", Integer.valueOf(priority));
        values.put("enabled", Integer.valueOf(enabled));
        values.put("profile_id", Integer.valueOf(profile_id));
        values.put("max", Integer.valueOf(max));
        values.put("min", Integer.valueOf(min));
        values.put("governor", governor);
        values.put("param1", Integer.valueOf(param1));
        values.put("param2", Integer.valueOf(param2));
        values.put("param3", Integer.valueOf(param3));
        values.put("param4", Integer.valueOf(param4));
        values.put("param5", Integer.valueOf(param5));
        return this.db.insert(TABLE_NAME, null, values);
    }

    public Cursor getAllProfiles() {
        return this.db.query(TABLE_NAME, new String[]{"_id", "priority", "enabled", "profile_id", "max", "min", "governor", "param1", "param2", "param3", "param4", "param5", "param6", "param7"}, null, null, null, null, "priority DESC, max DESC, min DESC, enabled DESC");
    }

    public Cursor getAllEnabledProfiles() {
        return this.db.query(TABLE_NAME, new String[]{"_id", "priority", "enabled", "profile_id", "max", "min", "governor", "param1", "param2", "param3", "param4", "param5", "param6", "param7"}, "enabled=?", new String[]{"1"}, null, null, "priority DESC, max DESC, min DESC");
    }

    public Cursor getAllProfilesByPriority() {
        return this.db.query(TABLE_NAME, new String[]{"_id", "priority", "enabled", "profile_id", "max", "min", "governor", "param1", "param2", "param3", "param4", "param5", "param6", "param7"}, null, null, null, null, "priority DESC, max DESC, min DESC");
    }

    public Cursor getAllEnabledTimeProfiles() {
        return this.db.query(TABLE_NAME, new String[]{"param1", "param2"}, "enabled=? AND profile_id=?", new String[]{"1", "7"}, null, null, "priority DESC, max DESC, min DESC");
    }

    public Cursor getAllEnabledAppProfiles() {
        return this.db.query(TABLE_NAME, new String[]{"param1", "param2"}, "enabled=? AND profile_id=?", new String[]{"1", "8"}, null, null, "priority DESC, max DESC, min DESC");
    }

    public Cursor getProfileDetails(int id) {
        return this.db.query(TABLE_NAME, null, "_id=?", new String[]{Integer.toString(id)}, null, null, null);
    }

    public void deleteById(int id) {
        this.db.delete(TABLE_NAME, "_id=?", new String[]{Integer.toString(id)});
    }

    public void changePriority(int id, int priority) {
        ContentValues values = new ContentValues();
        values.put("priority", Integer.valueOf(priority));
        this.db.update(TABLE_NAME, values, "_id=?", new String[]{Integer.toString(id)});
    }

    public void changeValue(int id, int value, String parameter) {
        ContentValues values = new ContentValues();
        values.put(parameter, Integer.valueOf(value));
        this.db.update(TABLE_NAME, values, "_id=?", new String[]{Integer.toString(id)});
    }

    public void close() {
        if (this.db.isOpen()) {
            this.db.close();
        }
    }

    public void reset() {
        String RESET_STATEMENT = "DROP TABLE IF EXISTS profiles";
        this.db.execSQL("DROP TABLE IF EXISTS profiles");
    }

    public void walCheckpoint() {
        try {
            this.db.execSQL("PRAGMA wal_checkpoint");
        } catch (Exception e) {
            Log.d("SpeedUp", "SQL exception " + e + ". Cannot WAL checkpoint or version of SQLite is older than 3.7.0");
        }
    }
}
