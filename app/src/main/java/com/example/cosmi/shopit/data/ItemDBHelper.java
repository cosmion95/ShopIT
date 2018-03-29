package com.example.cosmi.shopit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.cosmi.shopit.data.ItemContract.ItemEntry;
import com.example.cosmi.shopit.cosData.CosContract.CosEntry;

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

    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLE_ITEMS + " ("
            + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ItemEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + ItemEntry.COLUMN_CATEGORY + " INTEGER NOT NULL, "
            + ItemEntry.COLUMN_DESCRIPTION + " TEXT, "
            + ItemEntry.COLUMN_IMAGE + " TEXT, "
            + ItemEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
            + ItemEntry.COLUMN_STOCK + " INTEGER NOT NULL DEFAULT 0 )";

    private static final String CREATE_TABLE_COS = "CREATE TABLE " + TABLE_COS + " ("
            + CosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CosEntry.COLUMN_USERID + " INTEGER NOT NULL, "
            + CosEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + CosEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
            + CosEntry.COLUMN_QTY + " INTEGER NOT NULL, "
            + CosEntry.COLUMN_IMAGE + " TEXT )";

    public ItemDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create required tables
        sqLiteDatabase.execSQL(CREATE_TABLE_ITEMS);
        sqLiteDatabase.execSQL(CREATE_TABLE_COS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //drop older tables on upgrade
        String DELETE_ITEMS_TABLE = "DROP TABLE IF EXISTS " + TABLE_ITEMS;
        String DELETE_COS_TABLE = "DROP TABLE IF EXISTS " + TABLE_COS;

        sqLiteDatabase.execSQL(DELETE_ITEMS_TABLE);
        sqLiteDatabase.execSQL(DELETE_COS_TABLE);

        //create new tables
        onCreate(sqLiteDatabase);
    }
}
