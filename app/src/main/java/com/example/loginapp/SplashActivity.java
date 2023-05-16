package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Intent main =new Intent(SplashActivity.this, LoginActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                startActivity(main);
                finish();
            }
        },2000);
    }
}