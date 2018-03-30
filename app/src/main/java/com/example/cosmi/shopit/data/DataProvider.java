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



public class DataProvider extends ContentProvider {


    public static final String LOG_TAG = DataProvider.class.getSimpleName();

    //initialize the provider and database helper object
    ItemDBHelper itemDBHelper;

    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final int COS = 102;
    private static final int COS_ID = 103;
    private static final int USER_ID = 104;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer, run first time anything is called from this class
    static {
        //acces multiple rows in items table
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);
        //acces single row in items table
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);

        //acces multiple rows in cos table
        sUriMatcher.addURI(CosContract.CONTENT_AUTHORITY, CosContract.PATH_COS, COS);
        //acces single row in cos table
        sUriMatcher.addURI(CosContract.CONTENT_AUTHORITY, CosContract.PATH_COS + "/#", COS_ID);

        //acces single row in the user table
        sUriMatcher.addURI(UserContract.CONTENT_AUTHORITY, UserContract.PATH_USER + "/#", USER_ID);
    }




    @Override
    public boolean onCreate() {
        //create and initialize a ItemDBHelpler object to gain acces to the database
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
            case COS:
                //query the cos table
                cursor = database.query(CosContract.CosEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case COS_ID:
                //query one row in the cos
                //extract id from uri and perform an sql query on that specific id
                selection = CosContract.CosEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(CosContract.CosEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USER_ID:
                //query one row in the user table
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(UserContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
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
                //insert row in the items table
                return insertItem(uri, contentValues);
            case COS:
                //insert row in the cos table
                return insertItem(uri, contentValues);
            case USER_ID:
                //add a new user into the user table
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //insert a new item into the database with the given content values and return the new content uri for that specific row
    private Uri insertItem(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);

        //create the id to be returned
        long id;

        //initialize the database
        SQLiteDatabase database = itemDBHelper.getWritableDatabase();

        switch (match) {
            case COS:
                //check if name is valid
                String cosName = values.getAsString(CosContract.CosEntry.COLUMN_NAME);
                if (cosName == null){
                    throw new IllegalStateException("Item added to cos must have a name");
                }

                //check if userid is valid
                Integer userid = values.getAsInteger(CosContract.CosEntry.COLUMN_USERID);
                if (userid == null){
                    throw new IllegalStateException("Item added to cos must have a valid user id");
                }

                //check if price is valid
                Integer cosPrice = values.getAsInteger(CosContract.CosEntry.COLUMN_PRICE);
                if (cosPrice == null || cosPrice < 0){
                    throw new IllegalStateException("Item added to cos must have a valid price");
                }

                //insert a new item into the db with the given contentValues
                id = database.insert(CosContract.CosEntry.TABLE_NAME, null, values);

                //notify all listeners of data changes
                getContext().getContentResolver().notifyChange(uri,null);

                //return the new uri with the id appened to it
                return ContentUris.withAppendedId(uri, id);



            case ITEMS:
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
                id = database.insert(ItemContract.ItemEntry.TABLE_NAME, null, values);

                //notify all listeners of data changes
                getContext().getContentResolver().notifyChange(uri,null);

                //return the new uri with the id appended to it
                return ContentUris.withAppendedId(uri, id);

            case USER_ID:
                //check if username is valid
                String userName = values.getAsString(UserContract.UserEntry.COLUMN_USERNAME);
                if (userName == null){
                    throw new IllegalStateException("User must have valid name");
                }

                //check if password is valid
                String password = values.getAsString(UserContract.UserEntry.COLUMN_PASSWORD);
                if (password == null){
                    throw new IllegalStateException("User must have valid password");
                }

                //check for adminity
                int admin = values.getAsInteger(UserContract.UserEntry.COLUMN_ADMIN);
                if (!UserContract.UserEntry.isValidUser(admin)){
                    throw new IllegalStateException("User must be normal or admin: 0 or 1");
                }
                //insert a new user into the db with the given contentValues
                id = database.insert(UserContract.UserEntry.TABLE_NAME, null, values);

                //notify all listeners of data changes
                getContext().getContentResolver().notifyChange(uri,null);

                //return the new uri with the id appended to it
                return ContentUris.withAppendedId(uri, id);

            default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
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
                //delete all rows from items table that match the selection
                rowsDeleted = database.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                //delete a single row in items table
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COS:
                //delete all rows from cos table that match the selection and the selection args
                rowsDeleted = database.delete(CosContract.CosEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COS_ID:
                //delete a single row in the cos table given by the id in the URI
                selection = CosContract.CosEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(CosContract.CosEntry.TABLE_NAME, selection, selectionArgs);
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
                //return number of updated rows in the items table
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                //extract out the id from the uri so we know wich row to update
                selection = ItemContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            case COS:
                //return number of updated rows in the cos table
                return updateItem(uri, contentValues, selection, selectionArgs);
            case COS_ID:
                //extract out the id from the uri so we know wich row to update
                selection = CosContract.CosEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    //update items in the database with the given content values
    //return the number of updated rows
    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        //get a writeable db to perform the update
        SQLiteDatabase database = itemDBHelper.getWritableDatabase();

        //declaring the variable to return the number of updated rows
        int rowsUpdated;

        if (match == COS || match == COS_ID) {
            //update cos table
            //check and validate the data
            //check name
            if (contentValues.containsKey(CosContract.CosEntry.COLUMN_NAME)) {
                String CosName = contentValues.getAsString(CosContract.CosEntry.COLUMN_NAME);
                if (CosName == null) {
                    throw new IllegalArgumentException("Updating item name in cos failed");
                }
            }

            //check userid
            if (contentValues.containsKey(CosContract.CosEntry.COLUMN_USERID)) {
                Integer userid = contentValues.getAsInteger(CosContract.CosEntry.COLUMN_USERID);
                if (userid == null) {
                    throw new IllegalArgumentException("Updating userid in cos failed");
                }
            }

            //check price
            if (contentValues.containsKey(CosContract.CosEntry.COLUMN_PRICE)) {
                Integer cosPrice = contentValues.getAsInteger(CosContract.CosEntry.COLUMN_PRICE);
                if (cosPrice == null) {
                    throw new IllegalArgumentException("Updating price in cos failed");
                }
            }

            //if there are no values to update, dont try to update the db
            if (contentValues.size() == 0) {
                return 0;
            }
             rowsUpdated = database.update(CosContract.CosEntry.TABLE_NAME, contentValues, selection, selectionArgs);

            //notify listeners that data was changed if 1 or more rows were updated
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            //return the number of rows updated
            return rowsUpdated;
        }
        else if (match == ITEMS || match == ITEM_ID){
            //update items table
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
            rowsUpdated = database.update(ItemContract.ItemEntry.TABLE_NAME, contentValues, selection, selectionArgs);

            //notify listeners that data was changed if 1 or more rows were updated
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            //return the number of rows updated
            return rowsUpdated;
        }
        else {
            return 0;
        }

    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USER_ID: return UserContract.UserEntry.CONTENT_ITEM_TYPE;
            case COS: return CosContract.CosEntry.CONTENT_LIST_TYPE;
            case COS_ID: return CosContract.CosEntry.CONTENT_ITEM_TYPE;
            case ITEMS: return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID: return ItemContract.ItemEntry.CONTENT_ITEM_TYPE;
            default: throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
