package com.example.cosmi.shopit.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cosmi on 3/30/2018.
 */

public final class UserContract {

    public static final String CONTENT_AUTHORITY = "com.example.cosmi.shopit";

    public static final Uri USER_BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USER = "user";

    public static final class UserEntry implements BaseColumns{

        //do not really need a list type, but who knows
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        public static final String TABLE_NAME = "user";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_ADMIN = "admin";

        public static final int NORMAL_USER = 0;
        public static final int ADMIN_USER = 1;

        public static final Uri USER_CONTENT_URI = Uri.withAppendedPath(USER_BASE_CONTENT_URI, PATH_USER);

        public static boolean isValidUser(int user){
            if (user == NORMAL_USER || user == ADMIN_USER) {
                return true;
            }
            else {
                return false;
            }
        }

    }

}
