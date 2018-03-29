package com.example.cosmi.shopit;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cosmi.shopit.data.ItemContract;


/**
 * Created by cosmi on 3/29/2018.
 */

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView detailsImageView;
    private TextView detailsNameView;
    private TextView detailsStockView;
    private TextView detailsPriceView;
    private TextView detailsDescriptionView;
    private TextView detailsCategoryView;

    private Uri currentItemUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        Intent intent = getIntent();
        currentItemUri = intent.getData();

        //find all relevant views we want to use
        detailsNameView = findViewById(R.id.item_details_name);
        detailsCategoryView = findViewById(R.id.item_details_category);
        detailsDescriptionView = findViewById(R.id.item_details_description);
        detailsPriceView = findViewById(R.id.item_details_price);
        detailsStockView = findViewById(R.id.item_details_stock);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_NAME,
                ItemContract.ItemEntry.COLUMN_STOCK,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_CATEGORY,
                ItemContract.ItemEntry.COLUMN_DESCRIPTION
        };
        return new CursorLoader(this, currentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        data.moveToFirst();

        int namePosition = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME);
        int categoryPosition = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_CATEGORY);
        int pricePosition = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE);
        int stockPosition = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_STOCK);
        int descriptionPosition = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_DESCRIPTION);

        int category = data.getInt(categoryPosition);
        int price = data.getInt(pricePosition);
        int stock = data.getInt(stockPosition);

        String nameToDisplay = data.getString(namePosition);
        String categoryToDisplay = String.valueOf(category);
        String priceToDisplay = String.valueOf(price);
        String stockToDisplay = String.valueOf(stock);
        String descriptionToDisplay = data.getString(descriptionPosition);

        detailsNameView.setText(nameToDisplay);
        detailsStockView.setText(stockToDisplay);
        detailsPriceView.setText(priceToDisplay);
        detailsCategoryView.setText(categoryToDisplay);
        detailsDescriptionView.setText(descriptionToDisplay);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //do nothing for now
    }
}
