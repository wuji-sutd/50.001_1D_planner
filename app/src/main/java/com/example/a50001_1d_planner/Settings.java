package com.example.a50001_1d_planner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ThemedSpinnerAdapter;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.AppTheme, true);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        Button backToMenu = findViewById(R.id.backToMenu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMenuIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(backToMenuIntent);
            }
        });

//        Button toThemes = findViewById(R.id.theme);
//        toThemes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent toThemesIntent = new Intent(getApplicationContext(), Themes.class);
//                startActivity(toThemesIntent);
//            }
//        });

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