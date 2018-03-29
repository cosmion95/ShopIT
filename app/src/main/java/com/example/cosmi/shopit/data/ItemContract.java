package com.example.cosmi.shopit.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
/**
 * Created by cosmi on 3/28/2018.
 */

public final class ItemContract {

    public static final String CONTENT_AUTHORITY = "com.example.cosmi.shopit";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ITEMS = "items";

    public static final class ItemEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String TABLE_NAME = "items";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_STOCK = "stoc";
        public static final String COLUMN_CATEGORY = "category";

        public static final int CATEOGRY_LAPTOPS = 0;
        public static final int CATEGORY_DESKTOPS = 1;
        public static final int CATEOGRY_MICE = 2;
        public static final int CATEOGRY_KEYBOARDS = 3;
        public static final int CATEOGRY_HEADPHONES = 4;
        public static final int CATEOGRY_MONITORS = 5;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static boolean isValidCategory(int category) {
            if (category == CATEOGRY_LAPTOPS || category == CATEGORY_DESKTOPS || category == CATEOGRY_MICE ||
                    category == CATEOGRY_KEYBOARDS || category == CATEOGRY_HEADPHONES || category == CATEOGRY_MONITORS) {
                return true;
            }
            else {
                return false;
            }
        }
    }

}
