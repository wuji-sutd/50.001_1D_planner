package com.example.a50001_1d_planner;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button backToMain = findViewById(R.id.backToMain);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMainIntent = new Intent(getApplicationContext(), TimeTable.class);
                startActivity(backToMainIntent);
            }
        });

        Button toSettings = findViewById(R.id.settings);
        toSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSettingsIntent = new Intent(getApplicationContext(), Settings.class);
                startActivity(toSettingsIntent);
            }
        });

        Button toSetWorkingHours = findViewById(R.id.setWorkingHours);
        toSetWorkingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSetWorkingHoursIntent = new Intent(getApplicationContext(), WorkingHours.class);
                startActivity(toSetWorkingHoursIntent);
            }
        });

        Button newTask = (Button) findViewById(R.id.addNewTaskButton);

        newTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this, NewTask.class));
            }
        });

    }
}