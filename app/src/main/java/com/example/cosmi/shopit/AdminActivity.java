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
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cosmi.shopit.data.ItemContract;
import com.example.cosmi.shopit.data.UserContract;

public class AdminActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //edit texts to enter item info
    private EditText nameEditText;
    private Spinner categorySpinner;
    private EditText descriptionEditText;
    private Spinner imageSpinner;
    private EditText priceEditText;
    private EditText stocEditText;

    //button to add the item
    Button addItemButton;

    //set the default value for the category
    private int selectedCategory = ItemContract.ItemEntry.CATEOGRY_LAPTOPS;
    private int selectedImage = R.drawable.laptop1;

    Uri currentUserUri;
    private long userId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //retrieve the value of the userid
        Intent intent = getIntent();
        currentUserUri = intent.getData();
        if (currentUserUri != null) {
            userId = Long.valueOf(currentUserUri.getLastPathSegment());
        }
        Log.e("tag","current user id is: " + userId);

        //find all the necesary views
        nameEditText = findViewById(R.id.admin_item_name);
        descriptionEditText = findViewById(R.id.admin_item_description);
        priceEditText = findViewById(R.id.admin_item_price);
        stocEditText = findViewById(R.id.admin_item_stoc);
        categorySpinner = findViewById(R.id.admin_item_category_spinner);
        imageSpinner = findViewById(R.id.admin_item_image_spinner);

        //find the add item button
        addItemButton = findViewById(R.id.admin_add_item);

        //setup the spinners
        setupCategorySpinner();
        setupImageSpinner();

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertItem();
                finish();
            }
        });

    }

    private void setupCategorySpinner(){
        //create an adapter for the spinner
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_category_options, android.R.layout.simple_spinner_item);

        //specify the dropdown layout style
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //set the adapter
        categorySpinner.setAdapter(categorySpinnerAdapter);

        //set the selection to the constant values defined in the item contract
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals("Laptops")) {
                        selectedCategory = ItemContract.ItemEntry.CATEOGRY_LAPTOPS;
                    } else if (selection.equals("Desktops")){
                        selectedCategory = ItemContract.ItemEntry.CATEGORY_DESKTOPS;
                    } else if (selection.equals("Mice")) {
                        selectedCategory = ItemContract.ItemEntry.CATEOGRY_MICE;
                    } else if (selection.equals("Keyboards")) {
                        selectedCategory = ItemContract.ItemEntry.CATEOGRY_KEYBOARDS;
                    } else if (selection.equals("Headphones")) {
                        selectedCategory = ItemContract.ItemEntry.CATEOGRY_HEADPHONES;
                    } else if (selection.equals("Monitors")) {
                        selectedCategory = ItemContract.ItemEntry.CATEOGRY_MONITORS;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = ItemContract.ItemEntry.CATEOGRY_LAPTOPS;
            }
        });
    }

    private void setupImageSpinner(){
        //create an adapter for the spinner
        ArrayAdapter imageSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_image_options, android.R.layout.simple_spinner_item);

        //specify the dropdown layout style
        imageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //set the adapter
        imageSpinner.setAdapter(imageSpinnerAdapter);

        //set the selection to the constant values defined in the item contract
        imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)){
                    if (selection.equals("Laptop1")) {
                        selectedImage = R.drawable.laptop1;
                    } else if (selection.equals("Laptop2")){
                        selectedImage = R.drawable.laptop2;
                    } else if (selection.equals("Desktop1")) {
                        selectedImage = R.drawable.desktop1;
                    } else if (selection.equals("Desktop2")) {
                        selectedImage = R.drawable.desktop2;
                    } else if (selection.equals("Mice1")) {
                        selectedImage = R.drawable.mice1;
                    } else if (selection.equals("Mice2")) {
                        selectedImage = R.drawable.mice2;
                    }else if (selection.equals("Keyboard1")){
                        selectedImage = R.drawable.keyboard1;
                    } else if (selection.equals("Keyboard2")) {
                        selectedImage = R.drawable.keyboard2;
                    } else if (selection.equals("Headphones1")) {
                        selectedImage = R.drawable.headphone1;
                    } else if (selection.equals("Headphones2")) {
                        selectedImage = R.drawable.headphone2;
                    } else if (selection.equals("Monitor1")) {
                        selectedImage = R.drawable.monitor1;
                    } else if (selection.equals("Monitor2")) {
                        selectedImage = R.drawable.monitor2;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedImage = R.drawable.laptop1;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_NAME,
                ItemContract.ItemEntry.COLUMN_CATEGORY,
                ItemContract.ItemEntry.COLUMN_DESCRIPTION,
                ItemContract.ItemEntry.COLUMN_IMAGE,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_STOCK
        };
        return new CursorLoader(this, null, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //do nothing
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        categorySpinner.setSelection(0);
        descriptionEditText.setText("");
        imageSpinner.setSelection(0);
        priceEditText.setText("");
        stocEditText.setText("");
    }

    private void insertItem(){
        String itemName = nameEditText.getText().toString().trim();
        int itemCategory = selectedCategory;
        String itemDescription = descriptionEditText.getText().toString().trim();
        String itemPrice = priceEditText.getText().toString().trim();
        String itemStoc = stocEditText.getText().toString().trim();

        //check for empty fields
        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(itemDescription) || TextUtils.isEmpty(itemPrice) || TextUtils.isEmpty(itemStoc)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_NAME, itemName);
        values.put(ItemContract.ItemEntry.COLUMN_CATEGORY, itemCategory);
        values.put(ItemContract.ItemEntry.COLUMN_PRICE, itemPrice);
        values.put(ItemContract.ItemEntry.COLUMN_STOCK, itemStoc);
        values.put(ItemContract.ItemEntry.COLUMN_IMAGE, selectedImage);
        values.put(ItemContract.ItemEntry.COLUMN_DESCRIPTION, itemDescription);

        //insert the item with the content values
        Uri resultUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

        //display toast to see if item was added
        if (resultUri == null){
            Toast.makeText(this,"Insert failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,"Item added to db", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.e("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(AdminActivity.this, CatalogActivity.class);
        setIntent.setData(currentUserUri);
        startActivity(setIntent);
    }
}
