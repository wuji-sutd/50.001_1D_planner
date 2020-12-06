package com.example.a50001_1d_planner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final DBHelper dbHelper = new DBHelper(this);

        getTheme().applyStyle(R.style.AppTheme, true);
        setContentView(R.layout.activity_account);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        Button backToSettings = findViewById(R.id.backToSettings);
        backToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToSettingsIntent = new Intent(getApplicationContext(), Settings.class);
                startActivity(backToSettingsIntent);
            }
        });

        Button deleteButton = findViewById(R.id.deleteAccountButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.onUpgrade(dbHelper.getWritableDatabase(),0,0);
                Intent logOutIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(logOutIntent);
            }
        });

    }
}