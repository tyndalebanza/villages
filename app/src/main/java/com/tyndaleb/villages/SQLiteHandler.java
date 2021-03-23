package com.tyndaleb.villages;

/**
 * Created by tyndale on 9/17/2016.
 */
/**
 * Created by tyndale on 11/1/2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = com.tyndaleb.villages.SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_LOCATION = "location" ;

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_LOCATION = "location" ;
    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT," + KEY_USERNAME + " TEXT," + KEY_FULLNAME + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LOCATION + " INTEGER"  + ")";

        db.execSQL(LOCATION_TABLE);

        //Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String email, String uid, String username, String fullname , String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // User ID
        values.put(KEY_USERNAME, username); // UserName
        values.put(KEY_FULLNAME, fullname); // UserName
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        //Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void addLocation( Integer location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        //values.put(KEY_UID, uid); // User ID
        values.put(KEY_LOCATION, location); // Location

        // Inserting Row
        long id = db.insert(TABLE_LOCATION, null, values);
        db.close(); // Closing database connection

        //Log.d(TAG, "Location inserted into sqlite: " + id);
    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {

            user.put("email", cursor.getString(1));
            user.put("uid", cursor.getString(2));
            user.put("username", cursor.getString(3));
            user.put("fullname", cursor.getString(4));
            user.put("created_at", cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        //Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public HashMap<String, String> getUserCount() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  COUNT(*) AS USER_COUNT FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            //user.put("name", cursor.getString(1));
            user.put("user_count", cursor.getString(0));

        }
        cursor.close();
        db.close();
        // return user
        //Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public HashMap<String, String> getLocationDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("location", cursor.getString(2));
            user.put("uid", cursor.getString(3));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching location from Sqlite: " + user.toString());

        return user;
    }
    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TABLE_USER + "'", null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {

                db.delete(TABLE_USER, null, null);
                cursor.close();
            }
            cursor.close();
        }

        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }


    public void deleteLocation() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + TABLE_LOCATION + "'", null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {

                db.delete(TABLE_LOCATION, null, null);
                cursor.close();
            }
            cursor.close();
        }
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}