package com.example.cosmi.shopit.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cosmi on 3/29/2018.
 */

public final class CosContract {

    public static final String CONTENT_AUTHORITY = "com.example.cosmi.shopit";

    public static final Uri COS_BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COS = "cos";

    public static final class CosEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COS;

        public static final String TABLE_NAME = "cos";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_USERID = "userid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QTY = "qty";
        public static final String COLUMN_IMAGE = "image";

        public static final Uri COS_CONTENT_URI = Uri.withAppendedPath(COS_BASE_CONTENT_URI, PATH_COS);
    }
}
