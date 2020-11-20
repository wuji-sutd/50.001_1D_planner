package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button backToMenu = findViewById(R.id.backToMenu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMenuIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(backToMenuIntent);
            }
        });

        Button toAccount = findViewById(R.id.account);
        toAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAccountIntent = new Intent(getApplicationContext(), Account.class);
                startActivity(toAccountIntent);
            }
        });


    }
}