package com.example.teamv.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.teamv.R;

public class WelcomeActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister;
    // Chuc Thien
    private String PREFERENCE_KEY = "LogIn_SharePreferences";
    private String LOGIN_KEY = "LOGIN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Chuc Thien
        checkLogin();


        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
            }
        });
    }
    // Chuc Thien
    void checkLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_KEY,MODE_PRIVATE);
        if (sharedPreferences.getBoolean(LOGIN_KEY,false)) {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));

        }
    }
}