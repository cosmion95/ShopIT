package com.example.cosmi.shopit;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cosmi.shopit.data.CosContract;
import com.example.cosmi.shopit.data.ItemDBHelper;

import static android.content.ContentValues.TAG;

/**
 * Created by cosmi on 3/28/2018.
 */

public class CosCursorAdapter extends CursorAdapter {

    //construct a new ItemCursorAdapter
    public CosCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }


    //make a new blank list item view without any data
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return the list item view
        return LayoutInflater.from(context).inflate(R.layout.cos_item_list, parent, false);

    }


    //bind the item data in the current row pointed to by cursor to the given list item layout
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        //find all the list views
        ImageView imageView = view.findViewById(R.id.cos_item_image);
        TextView nameTextView = view.findViewById(R.id.cos_item_name);
        final TextView qtyTextView = view.findViewById(R.id.cos_item_qty);
        TextView priceTextView =  view.findViewById(R.id.cos_item_price);

        //find all the item information from the db using the cursor
        int idPosition = cursor.getColumnIndex(CosContract.CosEntry._ID);
        int namePosition = cursor.getColumnIndex(CosContract.CosEntry.COLUMN_NAME);
        int pricePosition = cursor.getColumnIndex(CosContract.CosEntry.COLUMN_PRICE);
        int qtyPosition = cursor.getColumnIndex(CosContract.CosEntry.COLUMN_QTY);
        int imagePosition = cursor.getColumnIndex(CosContract.CosEntry.COLUMN_IMAGE);

        final String name = cursor.getString(namePosition);
        final int qty = cursor.getInt(qtyPosition);
        Integer price = cursor.getInt(pricePosition);
        final Integer imageResourceId = cursor.getInt(imagePosition);
        final Long id = cursor.getLong(idPosition);
        Log.e("tag","actual qty is: " + qty);


        Button increaseQty = view.findViewById(R.id.cos_item_increase_qty);
        increaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // get an instance of a db helper
                 ItemDBHelper itemDBHelper = new ItemDBHelper(context);
                 int qtyToIncrease = qty;
               // increase quantity by 1
                Log.e("tag","qty before increase: " + qtyToIncrease);
                 qtyToIncrease++;
                Log.e("tag","qty after increase: " + qtyToIncrease);
               // increase the qty in the database
                 int rowsAffected = itemDBHelper.updateQty(qtyToIncrease, id);
                 //qtyToIncrease = 0;
                //Log.e("tag","button pressed, current item id is: " + id);
                if (rowsAffected != 0){
                   Toast.makeText(context, "Qty in DB increased succesfully",Toast.LENGTH_SHORT).show();

                }
                else {
                   Toast.makeText(context, "Could not increase qty in DB",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button decreaseQty = view.findViewById(R.id.cos_item_decrease_qty);
        decreaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an instance of a db helper
                if (qty > 1) {
                    ItemDBHelper itemDBHelper = new ItemDBHelper(context);
                    int qtyToIncrease = qty;
                    // increase quantity by 1
                    Log.e("tag", "qty before decrease: " + qtyToIncrease);
                    qtyToIncrease--;
                    Log.e("tag", "qty after decrease: " + qtyToIncrease);
                    // increase the qty in the database
                    int rowsAffected = itemDBHelper.updateQty(qtyToIncrease, id);
                    //qtyToIncrease = 0;
                    //Log.e("tag","button pressed, current item id is: " + id);
                    if (rowsAffected != 0) {
                        Toast.makeText(context, "Qty in DB decreased succesfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "Could not decrease qty in DB", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "Qty must be at least 1", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button deleteItem = view.findViewById(R.id.cos_item_remove_from_cos);
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemDBHelper itemDBHelper = new ItemDBHelper(context);
                int rowsAffected = itemDBHelper.deleteItemFromCos(id);

                if (rowsAffected != 0) {
                    Toast.makeText(context, "Item deleted from cos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error deleting item", Toast.LENGTH_SHORT).show();
                }
            }
        });


        String priceToString = String.valueOf(price);
        String qtyToString = String.valueOf(qty);

        nameTextView.setText(name);
        qtyTextView.setText(qtyToString);
        priceTextView.setText(priceToString);
        imageView.setImageResource(imageResourceId);

    }

}
