package com.example.cosmi.shopit;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cosmi.shopit.data.ItemDBHelper;
import com.example.cosmi.shopit.data.UserContract;

/**
 * Created by cosmi on 3/30/2018.
 */

public class RegisterActivity extends AppCompatActivity {

    //inputs for username and password
    EditText usernameInput;
    EditText passwordInput;
    CheckBox isAdminInput;
    ItemDBHelper itemDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //instantiating the username, password and admin field
        usernameInput = findViewById(R.id.register_username);
        passwordInput = findViewById(R.id.register_password);
        isAdminInput = findViewById(R.id.is_admin);


        itemDBHelper = new ItemDBHelper(this);

        //setting up the register button
        Button registerButton  = findViewById(R.id.activity_register_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register a user into the database
                registerUser();
                //TODO log the user in and move to catalog activity
            }
        });

    }


    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.length() < 1 || password.length() < 1){
            Toast.makeText(this,"Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAdmin = isAdminInput.isChecked();
        int isAdminNr = UserContract.UserEntry.NORMAL_USER;
        if (isAdmin) {
            isAdminNr = UserContract.UserEntry.ADMIN_USER;
        }

//        ContentValues values = new ContentValues();
//        values.put(UserContract.UserEntry.COLUMN_USERNAME, username);
//        values.put(UserContract.UserEntry.COLUMN_PASSWORD, password);
//        values.put(UserContract.UserEntry.COLUMN_ADMIN, isAdminNr);

        //Uri uri = getContentResolver().insert(UserContract.UserEntry.USER_CONTENT_URI, values);

        long userId = itemDBHelper.addUser(username, password, isAdminNr);

        if (userId < 0) {
            Toast.makeText(this,"Error registering new user", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,"User has been registered", Toast.LENGTH_SHORT).show();
            //if registration has been succesfull move to login screen
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
