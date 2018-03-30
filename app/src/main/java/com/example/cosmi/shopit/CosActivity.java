package com.example.cosmi.shopit;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.cosmi.shopit.data.CosContract;

/**
 * Created by cosmi on 3/29/2018.
 */

public class CosActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    CosCursorAdapter cosCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cos);

        //find the list view to display the items
        ListView listView = findViewById(R.id.cos_list_view);

        cosCursorAdapter = new CosCursorAdapter(this, null);
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
        return new CursorLoader(this, CosContract.CosEntry.COS_CONTENT_URI, projection, null, null, null);
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
        }
        return super.onOptionsItemSelected(item);
    }


}
