package com.example.a50001_1d_planner;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpPage extends AppCompatActivity {

    private UserDAO mUserDAO;

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;

    private Button submission;
    private Button backToLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        // Initialize
        this.mUserDAO = new UserDAO(this);

        this.nameInput = findViewById(R.id.inputNewUserName);
        this.emailInput = findViewById(R.id.inputNewUserEmail);
        this.passwordInput = findViewById(R.id.inputNewUserPassword);
        this.confirmPasswordInput = findViewById(R.id.inputNewUserConfirmPassword);

        this.submission = findViewById(R.id.submitNewUserInfo);
        this.backToLogIn = findViewById(R.id.backToLogIn);

        addNewUser();
        backToLogIn();

    }

    // Create a new user and save into database if the 'SUBMIT' button is clicked
    public void addNewUser() {
        submission.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              String name = nameInput.getText().toString();
                                              String email = emailInput.getText().toString();
                                              String password = passwordInput.getText().toString();
                                              String confirmPassword = confirmPasswordInput.getText().toString();

                                              boolean isValidPassword = checkPassword(password, confirmPassword);

                                              // If the two passwords are not identical: input again
                                              if (! isValidPassword)
                                                  Toast.makeText(SignUpPage.this, "Invalid Password, please try again", Toast.LENGTH_LONG).show();
                                              else {
                                                  User createdUser = mUserDAO.createUser(name, email, password);
                                                  Toast.makeText(SignUpPage.this, "Sign up successfully", Toast.LENGTH_LONG).show();

                                                  // If they are identical, save it and return to the log in page
                                                  Intent backToLogInIntent = new Intent(getApplicationContext(), MainActivity.class);
                                                  startActivity(backToLogInIntent);
                                              }

                                          }
                                      }
        );

    }

    // Check if the two input passwords are identical
    public boolean checkPassword(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        }
        else {
            Toast.makeText(SignUpPage.this, "Invalid password, please try again", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // Go back to the log in page if the 'BACK' button is clicked
    public void backToLogIn() {
        backToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToLogInIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backToLogInIntent);
            }
        });
    }

}