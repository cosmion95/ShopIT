package com.example.cosmi.shopit;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cosmi.shopit.data.ItemContract;
/**
 * Created by cosmi on 3/28/2018.
 */

public class ItemCursorAdapter extends CursorAdapter {

    //construct a new ItemCursorAdapter
    public ItemCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }


    //make a new blank list item view without any data
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return the list item view
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }




    //bind the item data in the current row pointed to by cursor to the given list item layout
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //find all the list views
        //ImageView imageView = (ImageView) view.findViewById(R.id.list_item_image);
        TextView nameTextView = (TextView) view.findViewById(R.id.list_item_name);
        TextView categoryTextView = (TextView) view.findViewById(R.id.list_item_category);
        TextView priceTextView = (TextView) view.findViewById(R.id.list_prod_price);
        TextView stockTextView = (TextView) view.findViewById(R.id.list_item_stock);

        //find all the item information from the db using the cursor
        int namePosition = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME);
        int categoryPosition = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_CATEGORY);
        int pricePosition = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE);
        int stockPosition = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_STOCK);

        String name = cursor.getString(namePosition);
        Integer category = cursor.getInt(categoryPosition);
        Integer price = cursor.getInt(pricePosition);
        Integer stock = cursor.getInt(stockPosition);

        String priceToString = String.valueOf(price);
        String categoryToString = String.valueOf(category);
        String stockToString = String.valueOf(stock);

        nameTextView.setText(name);
        categoryTextView.setText(categoryToString);
        priceTextView.setText(priceToString);
        stockTextView.setText(stockToString);

    }


}
