package com.example.cosmi.shopit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.cosmi.shopit.data.ItemContract.ItemEntry;
import com.example.cosmi.shopit.data.CosContract.CosEntry;
import com.example.cosmi.shopit.data.UserContract.UserEntry;

/**
 * Created by cosmi on 3/28/2018.
 */

public class ItemDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemDBHelper.class.getSimpleName();

    //database file name and version
    private static final String DATABASE_NAME = "shopit.db";
    private static final int DATABASE_VERSION = 1;

    //table names
    private static final String TABLE_ITEMS = ItemEntry.TABLE_NAME;
    private static final String TABLE_COS = CosEntry.TABLE_NAME;
    private static final String TABLE_USER = UserEntry.TABLE_NAME;

    private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ("
            + UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UserEntry.COLUMN_USERNAME + " TEXT NOT NULL, "
            + UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, "
            + UserEntry.COLUMN_ADMIN + " INTEGER NOT NULL DEFAULT 0 )";

    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS + " ("
            + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ItemEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + ItemEntry.COLUMN_CATEGORY + " INTEGER NOT NULL, "
            + ItemEntry.COLUMN_DESCRIPTION + " TEXT, "
            + ItemEntry.COLUMN_IMAGE + " INTEGER, "
            + ItemEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
            + ItemEntry.COLUMN_STOCK + " INTEGER NOT NULL DEFAULT 0 )";

    private static final String CREATE_TABLE_COS = "CREATE TABLE IF NOT EXISTS " + TABLE_COS + " ("
            + CosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CosEntry.COLUMN_USERID + " INTEGER NOT NULL, "
            + CosEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + CosEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
            + CosEntry.COLUMN_QTY + " INTEGER NOT NULL DEFAULT 1, "
            + CosEntry.COLUMN_IMAGE + " INTEGER, "
            + "FOREIGN KEY (" + CosEntry.COLUMN_USERID + ") REFERENCES " + TABLE_USER + "(" + UserEntry._ID + "))";

    public ItemDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create required tables
        sqLiteDatabase.execSQL(CREATE_TABLE_ITEMS);
        sqLiteDatabase.execSQL(CREATE_TABLE_COS);
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //drop older tables on upgrade
        String DELETE_ITEMS_TABLE = "DROP TABLE IF EXISTS " + TABLE_ITEMS;
        String DELETE_COS_TABLE = "DROP TABLE IF EXISTS " + TABLE_COS;
        String DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

        sqLiteDatabase.execSQL(DELETE_ITEMS_TABLE);
        sqLiteDatabase.execSQL(DELETE_COS_TABLE);
        sqLiteDatabase.execSQL(DELETE_USER_TABLE);

        //create new tables
        onCreate(sqLiteDatabase);
    }

    public long addUser(String username, String password, int admin){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_USERNAME, username);
        values.put(UserContract.UserEntry.COLUMN_PASSWORD, password);
        values.put(UserContract.UserEntry.COLUMN_ADMIN, admin);

        long id = sqLiteDatabase.insert(UserEntry.TABLE_NAME, null, values);
        sqLiteDatabase.close();
        return id;
    }

    public long authenticate(String user, String password){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] tableColumns = new String[] { "_id" };
        String whereClause = UserEntry.COLUMN_USERNAME + " = ? AND " + UserEntry.COLUMN_PASSWORD + " = ?";
        String[] whereArgs = new String[] { user, password };
        long id = -1;

        Cursor cursor = sqLiteDatabase.query(UserContract.UserEntry.TABLE_NAME, tableColumns, whereClause, whereArgs, null, null, null);

        //return the correct id of the user or else return -1
        if (cursor != null && cursor.moveToFirst()) {

            int idIndex = cursor.getColumnIndex(UserEntry._ID);
            id = cursor.getLong(idIndex);

        }
        return id;
    }

    public int isAdmin(long id){
        String userId = String.valueOf(id);

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] tableColumns = new String[] { UserEntry.COLUMN_ADMIN };
        String whereClause = UserEntry._ID + " = ?";
        String[] whereArgs = new String[] { userId };

        int isAdmin = 0;

        Cursor cursor = sqLiteDatabase.query(UserContract.UserEntry.TABLE_NAME, tableColumns, whereClause, whereArgs, null, null, null);
        //sqLiteDatabase.close();
        if (cursor != null && cursor.moveToFirst()) {
            int adminIndex = cursor.getColumnIndex(UserEntry.COLUMN_ADMIN);
            isAdmin = cursor.getInt(adminIndex);
        }
        return isAdmin;
    }

    public int updateQty(int increasedQty, Long id) {
        //convert to string the value of the current item id
        String itemID = String.valueOf(id);

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CosEntry.COLUMN_QTY, increasedQty);
        String whereClause = CosEntry._ID + " = ?";
        String[] whereArgs = new String[] { itemID };

        int rowsAffected = sqLiteDatabase.update(CosEntry.TABLE_NAME, values, whereClause, whereArgs);

        return rowsAffected;
    }

    public int deleteItemFromCos(Long id){
        String itemID = String.valueOf(id);

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String whereClause = CosEntry._ID + " = ?";
        String[] whereArgs = new String[] { itemID };

        int rowsAffected = sqLiteDatabase.delete(CosEntry.TABLE_NAME, whereClause, whereArgs);

        return rowsAffected;


    }

}
