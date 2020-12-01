package com.example.a50001_1d_planner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import distributeTimeSlotsPackage.AvailableDay;
import distributeTimeSlotsPackage.TaskSlots;
import distributeTimeSlotsPackage.TimeSlots;


public class CheckList extends AppCompatActivity {
    private Calendar currentCalendar;

    private TaskDAO taskDAO;
    private WorkingHoursDAO workingHoursDAO;
    private String TAG = "TimeTableActivity";
    private ArrayList<Task> tasks;
    private boolean canDisplay;
    private boolean isTodayList;

    private LinearLayout checkListDisplayLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_date);
        TextView textDate = findViewById(R.id.text_date);

        Intent intent = getIntent();
        int currentYear = intent.getIntExtra(TimeTable.currentYearIntentKey,-1);
        int currentMonth = intent.getIntExtra(TimeTable.currentMonthIntentKey,-1);
        int currentDay = intent.getIntExtra(TimeTable.currentDayIntentKey,-1);
        canDisplay = intent.getBooleanExtra(TimeTable.canDisplayIntentKey,false);

        currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
        if(currentYear!=-1) {
            isTodayList = false;
            currentCalendar.set(Calendar.YEAR, currentYear);
            currentCalendar.set(Calendar.MONTH, currentMonth);
            currentCalendar.set(Calendar.DAY_OF_MONTH, currentDay);
            String currentDateFormat = DateFormat.getDateInstance(DateFormat.FULL).format(currentCalendar.getTime());
            textDate.setText(currentDateFormat);
        } else {
            isTodayList = true;
            textDate.setText("Today");
        }

        Button backToMain = findViewById(R.id.backToMain);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMainIntent = new Intent(getApplicationContext(), TimeTable.class);
                startActivity(backToMainIntent);
            }
        });

        checkListDisplayLayout = findViewById(R.id.checkListTaskDisplay);
        this.taskDAO = new TaskDAO(this);
        this.workingHoursDAO = new WorkingHoursDAO(this);
        tasks = new ArrayList<>();
        //get all timeslots
        ArrayList<TimeSlots> timeslots = new ArrayList<>();
        //get all tasks
        HashMap<String, Integer> numSlotsPerWeek = new HashMap<>();
        workingHoursDAO.getAllAvailableTimeSlots(timeslots,numSlotsPerWeek);
        tasks =  (ArrayList<Task>)taskDAO.getAllTasks(timeslots);
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
        displayDayTasks(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH));

    }

    public void displayDayTasks(int curYear, int curMonth, int curDay){
        checkListDisplayLayout.removeAllViews();
        if(canDisplay) {
            Log.d(TAG,"can display");
            //display all the time slots
            String output = "";
            for(Task t: tasks){
                ArrayList<String> timeslotsStringArray = t.getArrayListOfTimeSlots();
                for(String timeslotsString: timeslotsStringArray) {
                    output+=timeslotsString+"\n";
                }
            }
            Log.d(TAG,"output: " +output);
            ArrayList<TaskSlots> currDayTaskSlots = new ArrayList<>();
            for (Task t : tasks) {
                for (TaskSlots ts : t.getTaskSlots()) {
                    Calendar taskSlotCal = ts.getTimeSlots().getCal();
                    if (taskSlotCal.get(Calendar.YEAR) == curYear &&
                            taskSlotCal.get(Calendar.MONTH) == curMonth &&
                            taskSlotCal.get(Calendar.DAY_OF_MONTH) == curDay) {
                        currDayTaskSlots.add(ts);
                    }
                }
            }
            if(currDayTaskSlots.size()>0) {
                for (TaskSlots ts : currDayTaskSlots) {
                    addTimeSlotsToLayout(ts.toString(),true);
                }
            }
            else{
                addTimeSlotsToLayout("No tasks assigned today",false);
            }
        }
        else{
            addTimeSlotsToLayout("Please set more working hours",false);
        }

    }


    public void addTimeSlotsToLayout(String wordsToDisplay, boolean hasTask){
        TextView taskSlotTextView = new TextView(this);
        taskSlotTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        taskSlotTextView.setText(wordsToDisplay);
        taskSlotTextView.setTextSize(20);
        taskSlotTextView.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
//        if(isTodayList){
//            Button doneButton = new Button(this);
//            doneButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT));
//            doneButton.setText("Done");
//            doneButton.setTextSize(10);
//        }
        if(hasTask) {
            LinearLayout taskLinearLayout = new LinearLayout(this);
            taskLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            taskLinearLayout.addView(taskSlotTextView);
            checkListDisplayLayout.addView(taskLinearLayout);
        } else {
            checkListDisplayLayout.addView(taskSlotTextView);
        }
    }
}
