package com.example.cosmi.shopit;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cosmi.shopit.data.CosContract;
import com.example.cosmi.shopit.data.UserContract;

/**
 * Created by cosmi on 3/29/2018.
 */

public class CosActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    CosCursorAdapter cosCursorAdapter;

    Uri currentUserUri;
    private long userId = -1;

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cos);


        //retrieve the value of the userid
        Intent intent = getIntent();
        currentUserUri = intent.getData();
        if (currentUserUri != null) {
            userId = Long.valueOf(currentUserUri.getLastPathSegment());
        }
        Log.e("tag","current user id is: " + userId);




        //find the list view to display the items
        listView = findViewById(R.id.cos_list_view);
        cosCursorAdapter = new CosCursorAdapter(this, null);
        listView.setAdapter(cosCursorAdapter);

        cosCursorAdapter.notifyDataSetChanged();
        listView.setAdapter(cosCursorAdapter);






        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                CosContract.CosEntry._ID,
                CosContract.CosEntry.COLUMN_NAME,
                CosContract.CosEntry.COLUMN_QTY,
                CosContract.CosEntry.COLUMN_PRICE,
                CosContract.CosEntry.COLUMN_IMAGE
        };
        String userID = String.valueOf(userId);
        String selection = CosContract.CosEntry.COLUMN_USERID + " = ?";
        String[] selectionArgs = new String[] { userID };
        return new CursorLoader(this, CosContract.CosEntry.COS_CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cosCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cosCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_cos, menu);
        return true;
    }

    private ContentValues getContentValues() {

        ContentValues values = new ContentValues();
        values.put(CosContract.CosEntry.COLUMN_NAME, "HP ELITEBOOK 14 G3");
        values.put(CosContract.CosEntry.COLUMN_PRICE, 2999);
        values.put(CosContract.CosEntry.COLUMN_QTY, 5);
        values.put(CosContract.CosEntry.COLUMN_USERID, 2);

        return values;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_insert_cos_dummy_data:
                getContentResolver().insert(CosContract.CosEntry.COS_CONTENT_URI, getContentValues());
                break;
            case R.id.action_empty_cos:
                getContentResolver().delete(CosContract.CosEntry.COS_CONTENT_URI,null,null);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //override the back button presses to send the user id back to the catalog activity
    @Override
    public void onBackPressed() {
        Log.e("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(CosActivity.this, CatalogActivity.class);
        Uri currentUserUri = ContentUris.withAppendedId(UserContract.UserEntry.USER_CONTENT_URI, userId);
        setIntent.setData(currentUserUri);
        startActivity(setIntent);
    }

}
