package com.example.cosmi.shopit;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cosmi.shopit.data.ItemDBHelper;
import com.example.cosmi.shopit.data.UserContract;

/**
 * Created by cosmi on 3/30/2018.
 */

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput;
    EditText passwordInput;
    Button loginButton;
    ItemDBHelper itemDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.login_username);
        passwordInput = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.activity_login_login_button);

        itemDBHelper = new ItemDBHelper(this);


        Button registerButton = findViewById(R.id.activity_login_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        final Button loginButton = findViewById(R.id.activity_login_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    public void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.length() < 1 || password.length() < 1){
            return;
        }

        long id = itemDBHelper.authenticate(username, password);

        if (id < 0){
            Toast.makeText(this,"Username and password do not match!!!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,"Login succesfull!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, CatalogActivity.class);
            Uri currentUserUri = ContentUris.withAppendedId(UserContract.UserEntry.USER_CONTENT_URI, id);
            intent.setData(currentUserUri);
            startActivity(intent);
        }


    }

}
