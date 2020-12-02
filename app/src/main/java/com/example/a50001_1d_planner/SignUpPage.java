package com.example.a50001_1d_planner;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpPage extends AppCompatActivity {

    private UserDAO mUserDAO;

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private TextView errorBox;

    private Button submission;
    private Button backToLogIn;
    private String errorList = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        this.errorBox = findViewById(R.id.errorBox);

        // Initialize
        this.mUserDAO = new UserDAO(this);
        this.nameInput = findViewById(R.id.inputNewUsername);
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

                boolean validName = validName(name);
                boolean validEmail = validEmail(email);
                boolean validPassword = validPassword(password);
                boolean isSamePassword = checkPassword(password, confirmPassword);

                if (validName) {
                    if (validEmail) {
                        if (validPassword) {
                            if (isSamePassword) {
                                User createdUser = mUserDAO.createUser(name, email, password);
                                errorList = "";
                                Toast.makeText(SignUpPage.this, "Signed up successfully", Toast.LENGTH_LONG).show();
                                //Save account and return to the log in page
                                Intent backToLogInIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(backToLogInIntent);
                            }
                        }
                    }
                }

                errorBox.setText(errorList);
                errorList = "";
            }
        } );
    }

    // check if name is valid
    private boolean validName(String name) {
        if (name.length() > 0) {
            return true;
        }
        else {
            //Toast.makeText(SignUpPage.this, "Please enter name", Toast.LENGTH_SHORT).show();
            errorList = errorList + "Please enter username\n";
            return false;
        }
    }

    //Check if email is valid
    private boolean validEmail(String email) {
        if (!email.contains("@") || email.contains(" ") || email.length() == 0) {
            //Toast.makeText(SignUpPage.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            errorList = errorList + "Please enter valid email\n";
            return false;
        }
        else {
            return true;
        }
    }

    //Check if password is valid
    private boolean validPassword(String password) {
        if (password.length() < 8) {
            //Toast.makeText(SignUpPage.this, "Minimum 8 characters", Toast.LENGTH_SHORT).show();
            errorList = errorList + "Password minimum 8 characters\n";
            return false;
        }
        else {
            return true;
        }
    }

    // Check if the two input passwords are identical
    private boolean checkPassword(String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            return true;
        }
        else {
            //Toast.makeText(SignUpPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            errorList = errorList + "Passwords do not match";
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