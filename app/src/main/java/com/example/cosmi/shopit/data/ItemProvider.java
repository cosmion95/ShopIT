package com.example.cosmi.shopit.data;

/**
 * Created by cosmi on 3/28/2018.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class ItemProvider extends ContentProvider {


    private static final String AUTHORITY = "com.example.cosmi.shopit.data.ItemProvider";


    public static final String LOG_TAG = ItemProvider.class.getSimpleName();

    //initialize the provider and database helper object

    ItemDBHelper itemDBHelper;

    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer, run first time anything is called from this class
    static {
        //acces multiple rows in items table
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);

        //acces single row in items table
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);
    }




    @Override
    public boolean onCreate() {
        //create and initialize a ItemDBHelpler object to gain acces to items database
        itemDBHelper = new ItemDBHelper(getContext());
        return true;
    }






    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //get a readable database
        SQLiteDatabase database = itemDBHelper.getReadableDatabase();

        //cursor to hold the result of the sql query
        Cursor cursor;

        //check to see if uri matcher can match the uri to a specific code
        int match = sUriMatcher.match(uri);

        switch (match){
            case ITEMS:
                //perform database query on the items table
                cursor = database.query(ItemContract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                //extract id from uri and perform an sql query on that specific id
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ItemContract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
                default:
                    throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //set a listener on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }






    //insert new data into the provider with the given content values
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //insert a new item into the database with the given content values and return the new content uri for that specific row
    private Uri insertItem(Uri uri, ContentValues values) {
        //check and validate the data we receive

        //check if name is valid
        String name = values.getAsString(ItemContract.ItemEntry.COLUMN_NAME);
        if (name == null){
            throw new IllegalStateException("Item must have a name");
        }

        //check if category is valid
        Integer category = values.getAsInteger(ItemContract.ItemEntry.COLUMN_CATEGORY);
        if (category == null || !ItemContract.ItemEntry.isValidCategory(category)){
            throw new IllegalStateException("Item must have a category");
        }

        //check if price is valid
        Integer price = values.getAsInteger(ItemContract.ItemEntry.COLUMN_PRICE);
        if (price == null || price < 0){
            throw new IllegalStateException("Item must have a valid price");
        }

        //insert a new item into the db with the given contentValues
        SQLiteDatabase database = itemDBHelper.getWritableDatabase();
        long id = database.insert(ItemContract.ItemEntry.TABLE_NAME, null, values);

        //notify all listeners of data changes
        getContext().getContentResolver().notifyChange(uri,null);

        //return the new uri with the id appened to it
        return ContentUris.withAppendedId(uri, id);
    }






    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //get a writeable database
        SQLiteDatabase database = itemDBHelper.getWritableDatabase();

        //variable to hold the number of deleted rows after the sql query
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                //delete all rows that match the selection and the selection args
                rowsDeleted = database.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                //delete a single row given by the id in the URI
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //notify all the listeners if changes were made to the db
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //return the number of deleted rows
        return rowsDeleted;
    }








    //update items in the database with the given content values and return the number of rows that were updated successfully
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                //extract out the id from the uri so we know wich row to update
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    //update items in the database with the given content values
    //return the number of updated rows
    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        //check and validate the data

        //check name
        if (contentValues.containsKey(ItemContract.ItemEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(ItemContract.ItemEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item must have a name");
            }
        }

        //check category
        if (contentValues.containsKey(ItemContract.ItemEntry.COLUMN_CATEGORY)) {
            Integer category = contentValues.getAsInteger(ItemContract.ItemEntry.COLUMN_CATEGORY);
            if (category == null) {
                throw new IllegalArgumentException("Item must have a category");
            }
        }

        //check price
        if (contentValues.containsKey(ItemContract.ItemEntry.COLUMN_PRICE)) {
            Integer price = contentValues.getAsInteger(ItemContract.ItemEntry.COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Item must have a category");
            }
        }

        //if there are no values to update, dont try to update the db
        if (contentValues.size() == 0) {
            return 0;
        }

        //get a writeable db and perform the update
        SQLiteDatabase database = itemDBHelper.getWritableDatabase();
        int rowsUpdated = database.update(ItemContract.ItemEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        //notify listeners that data was changed if 1 or more rows were updated
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //return the number of rows updated
        return rowsUpdated;
    }






    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS: return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID: return ItemContract.ItemEntry.CONTENT_ITEM_TYPE;
            default: throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
