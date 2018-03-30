package com.example.cosmi.shopit;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.cosmi.shopit.data.CosContract;

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
    public void bindView(View view, Context context, Cursor cursor) {

        //find all the list views
        //ImageView imageView = view.findViewById(R.id.cos_item_image);
        TextView nameTextView = view.findViewById(R.id.cos_item_name);
        TextView qtyTextView = view.findViewById(R.id.cos_item_qty);
        TextView priceTextView =  view.findViewById(R.id.cos_item_price);

        //find all the item information from the db using the cursor
        int namePosition = cursor.getColumnIndex(CosContract.CosEntry.COLUMN_NAME);
        int qtyPosition = cursor.getColumnIndex(CosContract.CosEntry.COLUMN_QTY);
        int pricePosition = cursor.getColumnIndex(CosContract.CosEntry.COLUMN_PRICE);

        String name = cursor.getString(namePosition);
        Integer qty = cursor.getInt(qtyPosition);
        Integer price = cursor.getInt(pricePosition);

        String priceToString = String.valueOf(price);
        String qtyToString = String.valueOf(qty);

        nameTextView.setText(name);
        qtyTextView.setText(qtyToString);
        priceTextView.setText(priceToString);

    }


}
