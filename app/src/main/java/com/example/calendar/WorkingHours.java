package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WorkingHours extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_hours);

        Button backToMenu = findViewById(R.id.backToMenu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMenuIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(backToMenuIntent);
            }
        });

        Button saveWorkingHours = findViewById(R.id.saveWorkingHours);
        saveWorkingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saveWorkingHoursIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(saveWorkingHoursIntent);
            }
        });

    }
}