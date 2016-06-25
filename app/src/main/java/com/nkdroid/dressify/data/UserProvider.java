package com.nkdroid.dressify.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class UserProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.nkdroid.dressify.data";


    static final String URL_SHIRTS = "content://" + PROVIDER_NAME + "/shirts";
    public static final Uri CONTENT_URI_SHIRTS = Uri.parse(URL_SHIRTS);

    static final String URL_PANTS = "content://" + PROVIDER_NAME + "/pants";
    public static final Uri CONTENT_URI_PANTS = Uri.parse(URL_PANTS);

    static final String URL_FAVORITES = "content://" + PROVIDER_NAME + "/favorite";
    public static final Uri CONTENT_URI_FAVORITE = Uri.parse(URL_FAVORITES);


    public static final String _ID = "_id";
    public static final String _SHIRT_ID = "shirtid";
    public static final String _SHIRT_DATA = "url";

    public static final String _ID_PANTS = "_id";
    public static final String _PANTS_ID = "pantid";
    public static final String _PANTS_DATA = "url";

    public static final String _ID_FAVORITE = "_id";
    public static final String _SHIRT_ID_FAV = "shirtid";
    public static final String _SHIRT_DATA_FAV = "urlshirt";
    public static final String _PANTS_ID_FAV = "pantid";
    public static final String _PANTS_DATA_FAV = "urlpant";

    private static HashMap<String, String> USER_SHIRTS_PROJECTION_MAP;
    private static HashMap<String, String> USER_PANTS_PROJECTION_MAP;
    private static HashMap<String, String> USER_FAVORITE_PROJECTION_MAP;

    static final int USER_SHIRT  = 1;
    static final int USER_SHIRT_ID = 2;

    static final int USER_PANTS  = 3;
    static final int USER_PANTS_ID = 4;

    static final int USER_FAV  = 5;
    static final int USER_FAV_ID = 6;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PROVIDER_NAME, "shirts", USER_SHIRT);
        uriMatcher.addURI(PROVIDER_NAME, "shirts/#", USER_SHIRT_ID);

        uriMatcher.addURI(PROVIDER_NAME, "pants", USER_PANTS);
        uriMatcher.addURI(PROVIDER_NAME, "pants/#", USER_PANTS_ID);

        uriMatcher.addURI(PROVIDER_NAME, "favorite", USER_FAV);
        uriMatcher.addURI(PROVIDER_NAME, "favorite/#", USER_FAV_ID);

    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;

    public static final String DATABASE_NAME = "SHIRTPANTSDATABASE";
    public static final String USER_SHIRTS_TABLE_NAME = "shirts";
    public static final String USER_PANTS_TABLE_NAME = "pants";
    public static final String USER_FAVORITE_TABLE_NAME = "favorite";

    static final int DATABASE_VERSION = 1;

    static final String CREATE_DB_TABLE_SHIRTS =
            " CREATE TABLE " + USER_SHIRTS_TABLE_NAME +
                    " ( shirtid INTEGER PRIMARY KEY NOT NULL, " +
                    " url TEXT NOT NULL);";


    static final String CREATE_DB_TABLE_FAV =
            " CREATE TABLE " + USER_FAVORITE_TABLE_NAME +
                    " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " shirtid INTEGER NOT NULL, " +
                    " urlshirt TEXT NOT NULL, " +
                    " pantid INTEGER NOT NULL, " +
                    " urlpant TEXT NOT NULL);";

    static final String CREATE_DB_TABLE_PANTS =
            " CREATE TABLE " + USER_PANTS_TABLE_NAME +
                    " ( pantid INTEGER PRIMARY KEY NOT NULL, " +
                    " url TEXT NOT NULL);";


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE_SHIRTS);
            db.execSQL(CREATE_DB_TABLE_PANTS);
            db.execSQL(CREATE_DB_TABLE_FAV);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  USER_SHIRTS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " +  USER_PANTS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " +  USER_FAVORITE_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new student record
         */
        Uri _uri = null;
        switch (uriMatcher.match(uri)) {

            case USER_SHIRT: {
                long rowID = db.insert(USER_SHIRTS_TABLE_NAME, "", values);

                /**
                 * If record is added successfully
                 */

                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_SHIRTS, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

                throw new SQLException("Failed to add a record into " + uri);
                //break;
            }

            case USER_PANTS: {
                long rowID = db.insert(USER_PANTS_TABLE_NAME, "", values);

                /**
                 * If record is added successfully
                 */

                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_PANTS, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

                throw new SQLException("Failed to add a record into " + uri);
                //break;
            }

            case USER_FAV: {
                long rowID = db.insert(USER_FAVORITE_TABLE_NAME, "", values);

                /**
                 * If record is added successfully
                 */

                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_FAVORITE, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

                throw new SQLException("Failed to add a record into " + uri);
                //break;
            }

            default:{
               // return _uri;
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

       // return _uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        //qb.setTables(USER_TABLE_NAME);

        switch (uriMatcher.match(uri)) {

            case USER_SHIRT:
                qb.setTables(USER_SHIRTS_TABLE_NAME);
                qb.setProjectionMap(USER_SHIRTS_PROJECTION_MAP);
                break;

            case USER_SHIRT_ID:
                qb.setTables(USER_SHIRTS_TABLE_NAME);
                qb.appendWhere( _SHIRT_ID + "=" + uri.getPathSegments().get(1));
                break;

            case USER_PANTS:
                qb.setTables(USER_PANTS_TABLE_NAME);
                qb.setProjectionMap(USER_PANTS_PROJECTION_MAP);
                break;

            case USER_PANTS_ID:
                qb.setTables(USER_PANTS_TABLE_NAME);
                qb.appendWhere( _PANTS_ID + "=" + uri.getPathSegments().get(1));
                break;

            case USER_FAV:
                qb.setTables(USER_FAVORITE_TABLE_NAME);
                qb.setProjectionMap(USER_FAVORITE_PROJECTION_MAP);
                break;

            case USER_FAV_ID:
                qb.setTables(USER_FAVORITE_TABLE_NAME);
                qb.appendWhere( _ID_FAVORITE + "=" + uri.getPathSegments().get(1));
                break;


            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }


        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){

            case USER_SHIRT:
                count = db.delete(USER_SHIRTS_TABLE_NAME, selection, selectionArgs);
                break;

            case USER_SHIRT_ID:
                String mid = uri.getPathSegments().get(1);
                count = db.delete( USER_SHIRTS_TABLE_NAME, _SHIRT_ID +  " = " + mid +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case USER_PANTS:
                count = db.delete(USER_PANTS_TABLE_NAME, selection, selectionArgs);
                break;

            case USER_PANTS_ID:
                String pantid = uri.getPathSegments().get(1);
                count = db.delete( USER_PANTS_TABLE_NAME, _PANTS_ID +  " = " + pantid +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case USER_FAV:
                count = db.delete(USER_PANTS_TABLE_NAME, selection, selectionArgs);
                break;

            case USER_FAV_ID:
                String favid = uri.getPathSegments().get(1);
                count = db.delete( USER_FAVORITE_TABLE_NAME, _ID_FAVORITE +  " = " + favid +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;


            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){

            case USER_SHIRT: {
                count = db.update(USER_SHIRTS_TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case USER_SHIRT_ID:
                count = db.update(USER_SHIRTS_TABLE_NAME, values, _SHIRT_ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            case USER_PANTS: {
                count = db.update(USER_PANTS_TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case USER_PANTS_ID:
                count = db.update(USER_PANTS_TABLE_NAME, values, _PANTS_ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            case USER_FAV: {
                count = db.update(USER_FAVORITE_TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case USER_FAV_ID:
                count = db.update(USER_FAVORITE_TABLE_NAME, values, _ID_FAVORITE + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;


            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){

            case USER_SHIRT:
                // return "vnd.android.cursor.dir/vnd.example.students";
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PROVIDER_NAME + "/" + "shirts";
            case USER_SHIRT_ID:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PROVIDER_NAME + "/" + "shirts";

            case USER_PANTS:
                // return "vnd.android.cursor.dir/vnd.example.students";
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PROVIDER_NAME + "/" + "pants";
            case USER_PANTS_ID:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PROVIDER_NAME + "/" + "pants";

            case USER_FAV:
                // return "vnd.android.cursor.dir/vnd.example.students";
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PROVIDER_NAME + "/" + "favorite";
            case USER_FAV_ID:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PROVIDER_NAME + "/" + "favorite";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}