package com.example.cosmi.shopit;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cosmi.shopit.data.CosContract;
import com.example.cosmi.shopit.data.UserContract;

/**
 * Created by cosmi on 3/29/2018.
 */

public class CosActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    CosCursorAdapter cosCursorAdapter;

    Uri currentUserUri;
    private long userId = -1;
    private int itemsPrice = 0;

    ListView listView;

    //setting up the order button necesary views
    final Context context = this;
    private Button orderButton;
    private String emailAdress;

    int totalPrice;


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

        //get the prices views
        TextView itemPriceTextView = findViewById(R.id.cos_item_cost);
        TextView deliveryPriceTextView = findViewById(R.id.cos_delivery_cost);
        TextView totalPriceTextView = findViewById(R.id.cos_total_price);


        //get the total price
        itemsPrice = getItemsPrice();
        Log.e("tag", "total price is: " + itemsPrice);
        String itemPriceString = "Pret produse: " + itemsPrice + " RON";
        totalPrice = itemsPrice + 25;
        String totalPriceString = "Pret total: " + totalPrice + " RON";


        itemPriceTextView.setText(itemPriceString);
        deliveryPriceTextView.setText("Cost livrare: 25 RON");
        totalPriceTextView.setText(totalPriceString);




        //setting up the order button
        orderButton = findViewById(R.id.cos_send_order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderButton();
            }
        });




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

    //get the sum of all items in cos  (price * qty)
    private int getItemsPrice(){
        String sumForProjection = "SUM(" + CosContract.CosEntry.COLUMN_PRICE + " * " + CosContract.CosEntry.COLUMN_QTY + ")";
        String[] projection = {
                sumForProjection
        };
        String stringUserID = String.valueOf(userId);
        String whereClause = CosContract.CosEntry.COLUMN_USERID + " = ?";
        String[] whereArgs = new String[] { stringUserID };
        Cursor totalPriceCursor = getContentResolver().query(CosContract.CosEntry.COS_CONTENT_URI, projection, whereClause, whereArgs, null);
        totalPriceCursor.moveToFirst();
        int totalPrice = totalPriceCursor.getInt(0);
        return totalPrice;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private String getItems(){
        String items="";

        String[] projection = {
                CosContract.CosEntry.COLUMN_NAME
        };
        String stringUserID = String.valueOf(userId);
        String whereClause = CosContract.CosEntry.COLUMN_USERID + " = ?";
        String[] whereArgs = new String[] { stringUserID };
        Cursor itemsCursor = getContentResolver().query(CosContract.CosEntry.COS_CONTENT_URI, projection, whereClause, whereArgs, null);



        if(itemsCursor!=null && itemsCursor.getCount() > 0)
        {
            if (itemsCursor.moveToFirst())
            {
                do {
                    items += itemsCursor.getString(0) + "\n";
                } while (itemsCursor.moveToNext());
            }
        }

       Log.e("tag","returned items are: " + items);
      return items;
    }

    private void orderButton(){
        //get the dialog prompt view
        LayoutInflater li = LayoutInflater.from(context);
        View promptDialog = li.inflate(R.layout.activity_prompt_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptDialog);

        final EditText emailEditText = promptDialog.findViewById(R.id.prompt_dialog_email_adress);



        //set the dialog message
        alertDialogBuilder.setCancelable(false);

        //set the ok button and check for valid email adress

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //get the user input
                String checkEmail = emailEditText.getText().toString().trim();
                boolean validEmail = isValidEmail(checkEmail);
                if (!validEmail){
                    Toast.makeText(context, "Please enter a valid email adress", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    String items = getItems();
                    String orderMessage = "Comanda ta la ShopIT a fost preluata:\n" + items + " Total: " + totalPrice + " RON.";
                    emailAdress = checkEmail;
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAdress});
                    email.putExtra(Intent.EXTRA_SUBJECT, "ShopIT Order");
                    email.putExtra(Intent.EXTRA_TEXT, orderMessage);
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                }
                Log.e("tag", "email adress from user input is: " + emailAdress);
            }
        });

        //set the cancel button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //create the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
