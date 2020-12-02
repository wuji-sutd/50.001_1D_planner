package com.example.a50001_1d_planner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import distributeTimeSlotsPackage.TestTimeSlotDistributionLogic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //NOTE: if need to clean the database, uncomment this
//        DBHelper dbHelper = new DBHelper(this);
//        dbHelper.onUpgrade(dbHelper.getWritableDatabase(),0,0);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button toMain = findViewById(R.id.logIn);
        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if {
                //    Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                //}
                //else if !={
                //    Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                //}
                //else {
                    Intent toMainIntent = new Intent(getApplicationContext(), TimeTable.class);
                    startActivity(toMainIntent);
                //}
            }
        });

        Button toSignUp = findViewById(R.id.signUp);
        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSignUpIntent = new Intent(getApplicationContext(), SignUpPage.class);
                startActivity(toSignUpIntent);
            }
        });

    }
}