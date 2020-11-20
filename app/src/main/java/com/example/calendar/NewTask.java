package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewTask extends AppCompatActivity {

    private TaskDAO mtaskDAO;

    private EditText titleInput;
    private EditText estHoursInput;
    private EditText dueDateInput;
    private EditText weeklyRInput;

    private Button cancelNewTask;
    private Button saveNewTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        setUpPopUpWindow();

        // Initialize
        this.mtaskDAO = new TaskDAO(this);

        this.titleInput = findViewById(R.id.inputNewTaskTitle);
        this.estHoursInput = findViewById(R.id.inputEstHours);
        this.dueDateInput = findViewById(R.id.inputNewTaskDueDate);
        this.weeklyRInput = findViewById(R.id.inputWeeklyRecurring);

        this.cancelNewTask = findViewById(R.id.cancelNewTask);
        this.saveNewTask = findViewById(R.id.saveNewTask);

        addTaskData();
        backToMenu();

    }

    // Set up the dimension of pop-up window: 90% width * 60% height
    public void setUpPopUpWindow() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.9), (int) (height*0.6));
    }

    public void addTaskData() {
        saveNewTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long userID = 0; // Need a method to find which user created the task
                        String title = titleInput.getText().toString();
                        String estHours = estHoursInput.getText().toString();
                        String dueDate = dueDateInput.getText().toString();
                        String weeklyR = weeklyRInput.getText().toString();

                        Task createdTask = mtaskDAO.createTask(userID, title, estHours, dueDate, weeklyR);
                        Toast.makeText(NewTask.this, "New task added", Toast.LENGTH_LONG).show();

                        // Return back to menu page if new task is added
                        Intent cancelNewTaskIntent = new Intent(getApplicationContext(), Menu.class);
                        startActivity(cancelNewTaskIntent);
                    }
                }
        );
    }

    // Return back to menu page if the 'CANCEL' button is clicked
    public void backToMenu() {
        cancelNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelNewTaskIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(cancelNewTaskIntent);
            }
        });
    }

}