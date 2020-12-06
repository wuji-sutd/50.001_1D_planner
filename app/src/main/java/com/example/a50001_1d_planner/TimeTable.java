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
import android.widget.LinearLayout.LayoutParams;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import distributeTimeSlotsPackage.AllocateTimeSlots;
import distributeTimeSlotsPackage.AvailableDay;
import distributeTimeSlotsPackage.TaskSlots;
import distributeTimeSlotsPackage.TimeSlots;

public class TimeTable extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static String currentYearIntentKey = "CURRENTYEAR";
    public static String currentMonthIntentKey = "CURRENTMONTH";
    public static String currentDayIntentKey = "CURRENTDAY";
    public static String canDisplayIntentKey = "CANDISPLAY";

    private int currentYearIntent = -1;
    private int currentMonthIntent = -1;
    private int currentDayIntent = -1;

    private TaskDAO taskDAO;
    private WorkingHoursDAO workingHoursDAO;
    ArrayList<AvailableDay> availableDays;
    private String TAG = "TimeTableActivity";
    private ArrayList<Task> tasks;
    private boolean canDisplay;

    private LinearLayout timeTableDisplayLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        timeTableDisplayLayout = findViewById(R.id.timeTableTaskDisplay);
        this.taskDAO = new TaskDAO(this);
        this.workingHoursDAO = new WorkingHoursDAO(this);
        //get all timeslots
        ArrayList<TimeSlots> timeslots = new ArrayList<>();
        //get all tasks
        HashMap<String, Integer> numSlotsPerWeek = new HashMap<>();
        workingHoursDAO.getAllAvailableTimeSlots(timeslots,numSlotsPerWeek);
        for(TimeSlots ts: timeslots){
            Log.d(TAG,"timeslots: " +ts);
        }
        tasks = taskDAO.getAllTasks(timeslots);
        ArrayList<AvailableDay> recentlyUpdatedDays = workingHoursDAO.getRecentlyUpdatedAvailableDays();
        availableDays = workingHoursDAO.getAllAvailableDays();
        canDisplay = true;

        for(Task t:tasks){
            Log.d(TAG,"start:" +t.getTitle()+t.getTaskID() +":" +t.getNumTaskSlotsNeeded());
        }

        //check if any of the working days were updated first
        if(recentlyUpdatedDays.size()>0){
            if(!GetAllocatedTasks.changeAvailableDaysFixed(this,tasks,timeslots,availableDays)) canDisplay =false;
            else{
                for(AvailableDay availDay: recentlyUpdatedDays){
                    workingHoursDAO.removeHasChangedAvailableDays(availDay);
                }
            }
        } else {
            //check if all the tasks have time slots, if not then allocate tasks
            ArrayList<Task> unassignedTasks = new ArrayList<>();
            boolean needsAssigning = false;
            for (Task t : tasks) {
                if (!t.checkAssigned()) {
                    unassignedTasks.add(t);
                    needsAssigning = true;
                }
                Log.d(TAG,"else:" +t.getTitle());

            }
            if (needsAssigning) {
                if (!GetAllocatedTasks.addNewTaskAfter(this, unassignedTasks, tasks, timeslots, availableDays,numSlotsPerWeek)) {
                    canDisplay = false;
                }
            }
        }
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
        displayDayTasks(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH));

        //if all tasks have time slots, show the task for that particular day
        //for now idk what this page does so im just gonna show all the time slots
        Button menuButton = findViewById(R.id.menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMenuIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(toMenuIntent);
            }
        });

        Button date = findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateFormat = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        TextView textView = findViewById(R.id.textView);
        textView.setText(currentDateFormat);
        currentYearIntent = year;
        currentMonthIntent = month;
        currentDayIntent = dayOfMonth;
        displayDayTasks(year,month,dayOfMonth);
    }
    public void displayDayTasks(int curYear, int curMonth, int curDay){
        timeTableDisplayLayout.removeAllViews();
        if(canDisplay) {
            Log.d(TAG,"can display");
            //display all the time slots
            ArrayList<TaskSlots> currDayTaskSlots = new ArrayList<>();
            boolean needEnd = true;
            for (Task t : tasks) {
                Log.d(TAG,t.getTitle() +t.getTaskID());
                for (TaskSlots ts : t.getTaskSlots()) {
                    Log.d(TAG,ts.getNameTask() + ts.getTaskTime());
                    Calendar taskSlotCal = ts.getTimeSlots().getCal();
                    if (taskSlotCal.get(Calendar.YEAR) == curYear &&
                            taskSlotCal.get(Calendar.MONTH) == curMonth &&
                            taskSlotCal.get(Calendar.DAY_OF_MONTH) == curDay) {
                        currDayTaskSlots.add(ts);
                    }
                }
            }
            if(currDayTaskSlots.size()>0) {
                //StringBuilder taskMergeString = new StringBuilder();
                Calendar currentDay = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
                currentDay.set(curYear,curMonth,curDay);
                ArrayList<Double> breakHours = availableDays.get(currentDay.get(Calendar.DAY_OF_WEEK)-1).getBreakHoursDouble();
                Log.d(TAG,"current Day: "+ currentDay.get(Calendar.DAY_OF_WEEK));

                for(double breakHour:breakHours){
                    Log.d(TAG,"break hour found:" + breakHour);
                    TaskSlots currentBreakTask = new TaskSlots("Break",99999); //fake id
                    currentDay.set(Calendar.HOUR_OF_DAY,(int)breakHour);
                    currentDay.set(Calendar.MINUTE,breakHour-(int)breakHour==0?0:30);
                    if(currentDay.compareTo(Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore")))>0) {
                        TimeSlots breaktimeSlot = new TimeSlots(currentDay);
                        currentBreakTask.setTimeSlots(breaktimeSlot);
                        currDayTaskSlots.add(currentBreakTask);
                        Log.d(TAG, "found break");
                    }
                }
                Collections.sort(currDayTaskSlots);

                StringBuilder mergeContinuousTaskBuilder = new StringBuilder();
                mergeContinuousTaskBuilder.append(currDayTaskSlots.get(0).getStartTimeString());
                String currentTaskName = currDayTaskSlots.get(0).getNameTask();
                for (int i=0;i<currDayTaskSlots.size();i++) {
                    if(!currentTaskName.equals(currDayTaskSlots.get(i).getNameTask())){
                        mergeContinuousTaskBuilder.append("-");
                        String currentEndTime = (i==1? currDayTaskSlots.get(0).getEndTimeString(): currDayTaskSlots.get(i-1).getEndTimeString());
                        Log.d(TAG,currentTaskName);
                        Log.d(TAG,currDayTaskSlots.get(i).getEndTimeString());
                        mergeContinuousTaskBuilder.append(currentEndTime);
                        mergeContinuousTaskBuilder.append(" ");
                        mergeContinuousTaskBuilder.append(currentTaskName);
                        addTimeSlotsToLayout(mergeContinuousTaskBuilder.toString(),true);
                        currentTaskName = currDayTaskSlots.get(i).getNameTask();
                        mergeContinuousTaskBuilder = new StringBuilder();
                        mergeContinuousTaskBuilder.append(currDayTaskSlots.get(i).getStartTimeString());
                    }
                }
                mergeContinuousTaskBuilder.append("-");
                mergeContinuousTaskBuilder.append(currDayTaskSlots.get(currDayTaskSlots.size()-1).getEndTimeString());
                mergeContinuousTaskBuilder.append(" ");
                mergeContinuousTaskBuilder.append(currentTaskName);
                addTimeSlotsToLayout(mergeContinuousTaskBuilder.toString(),true);
            }
            else{
                addTimeSlotsToLayout("No tasks assigned today",false);
            }
        }
        else{
            addTimeSlotsToLayout("Please set more working hours",false);
        }

    }


    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }

    public void addTimeSlotsToLayout(String wordsToDisplay, boolean hasTask){
        TextView taskSlotTextView = new TextView(this);
        taskSlotTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        taskSlotTextView.setText(wordsToDisplay);
        taskSlotTextView.setTextSize(20);
        taskSlotTextView.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
        if(hasTask) {
            LinearLayout taskLinearLayout = new LinearLayout(this);
            taskLinearLayout.addView(taskSlotTextView);
            timeTableDisplayLayout.addView(taskLinearLayout);
        } else {
            timeTableDisplayLayout.addView(taskSlotTextView);
        }
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(TimeTable.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }
}