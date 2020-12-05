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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private UserDAO userDAO; //ADDED
    private EditText usernameEditText; //ADDED
    private EditText passwordEditText; //ADDED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //NOTE: if need to clean the database, uncomment this
//        DBHelper dbHelper = new DBHelper(this);
//        dbHelper.onUpgrade(dbHelper.getWritableDatabase(),0,0);
        getSupportActionBar().hide();
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.activity_title);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDAO = new UserDAO(this); //ADDED

        usernameEditText = findViewById(R.id.inputUsername); //ADDED
        passwordEditText = findViewById(R.id.inputPassword); //ADDED

        Button toMain = findViewById(R.id.logIn);
        toMain.setOnClickListener(new View.OnClickListener() { //ADDED AND CHANGED
            @Override
            public void onClick(View v) {
                if(!checkFieldsPopulated()){
                    Toast.makeText(getBaseContext(),"Please fill in your login details", Toast.LENGTH_SHORT).show();
                    return;
                }
                User user = userDoesExists();
                if(user == null) {
                    Toast.makeText(getBaseContext(),"User name invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isCorrectPassword(user)) {
                    Toast.makeText(getBaseContext(),"Password invalid for user", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent toMainIntent = new Intent(getApplicationContext(), TimeTable.class);
                startActivity(toMainIntent);
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
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    //ADDED FROM HERE
    public boolean checkFieldsPopulated(){
        return !usernameEditText.getText().toString().isEmpty() && !passwordEditText.getText().toString().isEmpty();
    }

    public User userDoesExists(){
        ArrayList<User> allUsers = userDAO.getAllUsers();
        if(allUsers.size()==0) return null;
        for(User user:allUsers){
            if(user.getName().equals(usernameEditText.getText().toString())) return user;
        }
        return null;
    }

    public boolean isCorrectPassword(User user){
        return passwordEditText.getText().toString().equals(user.getPassword());
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }


}