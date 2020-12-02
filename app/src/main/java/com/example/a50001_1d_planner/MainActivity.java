package com.example.a50001_1d_planner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import distributeTimeSlotsPackage.TestTimeSlotDistributionLogic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserDAO mUserDAO;

    private EditText nameInput;
    private EditText passwordInput;
//    private HashMap<String, String> dbInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //NOTE: if need to clean the database, uncomment next 2 lines
//       DBHelper dbHelper = new DBHelper(this);
//        dbHelper.onUpgrade(dbHelper.getWritableDatabase(),0,0);
///        dbInfo = dbHelper.getUserInfo();
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.nameInput = findViewById(R.id.inputUsername);
        this.passwordInput = findViewById(R.id.inputPassword);

        Button toMain = findViewById(R.id.logIn);
        toMain.setOnClickListener(new View.OnClickListener() {
            String username = nameInput.getText().toString();
            String password = passwordInput.getText().toString();



            @Override
            public void onClick(View v) {
///                if (!dbInfo.containsValue(username)) {
///                    Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
///                }
///                else if (!.contains(password)) {
///                    Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
///                }
///                else {
                    Intent toMainIntent = new Intent(getApplicationContext(), TimeTable.class);
                    startActivity(toMainIntent);
///                }
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