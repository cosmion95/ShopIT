package com.example.cosmi.shopit;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.cosmi.shopit.data.ItemContract;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ItemCursorAdapter itemCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        //find the list view to display the items
        ListView listView = findViewById(R.id.item_list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        //instantiate the cursorAdapter
        itemCursorAdapter = new ItemCursorAdapter(this,null);

        //set the adapter on the listview
        listView.setAdapter(itemCursorAdapter);

        //create an on click listener on the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //send an intent to DetalisActivity with the current item uri
                Intent intent = new Intent(CatalogActivity.this, DetailsActivity.class);

                Uri currentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);
                intent.setData(currentItemUri);

                startActivity(intent);
            }
        });


        //start the loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_NAME,
                ItemContract.ItemEntry.COLUMN_CATEGORY,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_STOCK,
                ItemContract.ItemEntry.COLUMN_IMAGE
        };
        return new CursorLoader(this, ItemContract.ItemEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        itemCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private ContentValues getContentValues() {

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_NAME, "Lenovo Thinkpad 13");
        values.put(ItemContract.ItemEntry.COLUMN_CATEGORY, ItemContract.ItemEntry.CATEOGRY_LAPTOPS);
        values.put(ItemContract.ItemEntry.COLUMN_PRICE, 2999);
        values.put(ItemContract.ItemEntry.COLUMN_STOCK, 20);
        values.put(ItemContract.ItemEntry.COLUMN_DESCRIPTION, "Starting at a mere 3.17 lbs and just .79” thin, this laptop is ultraportable – it’s perfect for productivity on the go. And with up to nine hours of battery life, you can work a full day without recharging. Intel® 6th Gen Core™ i processors with built-in security deliver great mobile performance. The optional 16GB high-performance DDR4 memory yields a higher data transfer rate than previous generations, less power consumption, and a cooler running machine.");

        return values;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, getContentValues());
                return true;
            case R.id.action_view_cos:
                Intent intent = new Intent(CatalogActivity.this, CosActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}
