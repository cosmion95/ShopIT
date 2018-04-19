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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cosmi.shopit.data.CosContract;
import com.example.cosmi.shopit.data.ItemContract;
import com.example.cosmi.shopit.data.ItemDBHelper;
import com.example.cosmi.shopit.data.UserContract;


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

    private String name;
    private int category;
    private int price;
    private int stoc;

    private int imageResourceId;

    private long userId = -1;
    private int isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        Intent intent = getIntent();
        currentItemUri = intent.getData();

        //get the current user id
        userId = intent.getLongExtra("userId", -1);
        Log.e("tag", "id received via intent is: " + userId);

        isAdmin = new ItemDBHelper(this).isAdmin(userId);

        //set up the floating action button
        if (userId != -1) {
            FloatingActionButton fab = findViewById(R.id.item_details_fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertItem();
                }
            });
        }
        //find all relevant views we want to use
        detailsNameView = findViewById(R.id.item_details_name);
        detailsCategoryView = findViewById(R.id.item_details_category);
        detailsDescriptionView = findViewById(R.id.item_details_description);
        detailsPriceView = findViewById(R.id.item_details_price);
        detailsStockView = findViewById(R.id.item_details_stock);
        detailsImageView = findViewById(R.id.item_details_image);

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
                ItemContract.ItemEntry.COLUMN_DESCRIPTION,
                ItemContract.ItemEntry.COLUMN_IMAGE
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
        int imagePosition = data.getColumnIndex(ItemContract.ItemEntry.COLUMN_IMAGE);

        category = data.getInt(categoryPosition);
        price = data.getInt(pricePosition);
        stoc = data.getInt(stockPosition);
        imageResourceId = data.getInt(imagePosition);


        //get the name of the item
        name = data.getString(namePosition);

        //get the category of the item
        String categoryToDisplay = "Categorie: ";
        switch (category){
            case 0:
                categoryToDisplay += "Laptops";
                break;
            case 1:
                categoryToDisplay += "Desktops";
                break;
            case 2:
                categoryToDisplay += "Mice";
                break;
            case 3:
                categoryToDisplay += "Keyboards";
                break;
            case 4:
                categoryToDisplay += "Headphones";
                break;
            case 5:
                categoryToDisplay += "Monitors";
                break;
        }

        //get the price of the item
        String priceToDisplay = "Pret: " + price + " RON";

        //get the stoc info
        String stockToDisplay = "";
        if (stoc > 10) {
            stockToDisplay = "In stoc magazin";
        }
        else if (stoc == 0) {
            stockToDisplay = "Nu este in stoc";
        }
        else {
            stockToDisplay = "In stoc furnizor";
        }


        String descriptionToDisplay = "Descriere: \n" + data.getString(descriptionPosition);

        detailsNameView.setText(name);
        detailsStockView.setText(stockToDisplay);
        detailsPriceView.setText(priceToDisplay);
        detailsCategoryView.setText(categoryToDisplay);
        detailsDescriptionView.setText(descriptionToDisplay);
        detailsImageView.setImageResource(imageResourceId);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //do nothing for now
    }

    private void insertItem() {
        String itemName = detailsNameView.getText().toString().trim();
        String itemPrice = String.valueOf(price);
        String qty = String.valueOf(1);
        String userID = String.valueOf(userId);


        ContentValues values = new ContentValues();
        values.put(CosContract.CosEntry.COLUMN_NAME, name);
        values.put(CosContract.CosEntry.COLUMN_PRICE, price);
        values.put(CosContract.CosEntry.COLUMN_QTY, qty);
        values.put(CosContract.CosEntry.COLUMN_USERID, userID);
        values.put(CosContract.CosEntry.COLUMN_IMAGE, imageResourceId);

        Uri resultUri = getContentResolver().insert(CosContract.CosEntry.COS_CONTENT_URI, values);

        if (resultUri == null) {
            Toast.makeText(this,"Error adding item to cos", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,"Item added to cos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        MenuItem item = menu.getItem(0);
        MenuItem adminItem = menu.getItem(1);

        if (isAdmin == 1) {
            adminItem.setVisible(true);
        }

        if (userId == -1){
            item.setTitle("Login");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_view_cos_details_activity:
                if (userId != -1) {
                    Intent intent = new Intent(DetailsActivity.this, CosActivity.class);
                    Uri currentUserUri = ContentUris.withAppendedId(UserContract.UserEntry.USER_CONTENT_URI, userId);
                    intent.setData(currentUserUri);
                    startActivity(intent);
                }
                else {
                    Log.e("tag", "entered action_login");
                    Intent loginIntent = new Intent(DetailsActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
                break;
            case R.id.action_remove_item:
                removeItem();
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
        Intent setIntent = new Intent(DetailsActivity.this, CatalogActivity.class);
        Uri currentUserUri = ContentUris.withAppendedId(UserContract.UserEntry.USER_CONTENT_URI, userId);
        setIntent.setData(currentUserUri);
        startActivity(setIntent);
    }

    private void removeItem(){
        String whereClause = ItemContract.ItemEntry.COLUMN_NAME + " = ?";
        String itemName = detailsNameView.getText().toString().trim();
        String[] whereArgs = new String[] { itemName };
        int rowsAffected = getContentResolver().delete(ItemContract.ItemEntry.CONTENT_URI, whereClause, whereArgs);
        if (rowsAffected == 0){
            Toast.makeText(this, "Item was NOT removed", Toast.LENGTH_SHORT);
        }
        else {
            Intent intent = new Intent(DetailsActivity.this, CatalogActivity.class);
            Uri currentUserUri = ContentUris.withAppendedId(UserContract.UserEntry.USER_CONTENT_URI, userId);
            intent.setData(currentUserUri);
            startActivity(intent);
        }
    }
}
