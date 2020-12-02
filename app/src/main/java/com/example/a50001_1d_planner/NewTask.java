package com.example.a50001_1d_planner;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NewTask extends AppCompatActivity {
    private final String TAG = "NewTaskActivity";
    private final int NUM_DAYS_BEFORE_RECURRENCE = 3;
    private TaskDAO mtaskDAO;

    private EditText titleInput;
    private NumberPicker estHoursInput;
    private NumberPicker estMinInput;
    private DatePicker dueDateInput;

    private Switch weeklyRecSwitch;

    private Button cancelNewTask;
    private Button saveNewTask;
    private RadioGroup weeklyRecurringDueDate;

    //radio buttons

    private RadioButton radioMon;
    private RadioButton radioTues;
    private RadioButton radioWed;
    private RadioButton radioThurs;
    private RadioButton radioFri;
    private RadioButton radioSat;
    private RadioButton radioSun;

    private int dueYear =0;
    Calendar today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
    Calendar dueDateCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        setUpPopUpWindow(0.9,0.7);
        Log.d(TAG,"calendar time" + today.getTime().toString());
        weeklyRecSwitch = (Switch)findViewById(R.id.weeklyRecSwitch);
        weeklyRecSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getBaseContext(),"Due date should reflect the very last recurring due date", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getBaseContext(),"Recurring mode Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize
        this.mtaskDAO = new TaskDAO(this);

        this.titleInput = findViewById(R.id.inputNewTaskTitle);

        this.estHoursInput = findViewById(R.id.inputEstHours);
        this.estHoursInput.setMaxValue(23);
        this.estHoursInput.setMinValue(0);
        this.estMinInput = findViewById(R.id.inputEstMin);
        //minpicker = new String[] {"00", "30"};
        //this.estMinInput.setDisplayedValues(minpicker);
        this.estMinInput.setMaxValue(1);
        this.estMinInput.setMinValue(0);
        String[] minuteValues = {"00","30"};
        estMinInput.setDisplayedValues(minuteValues);

        this.dueDateInput = findViewById(R.id.inputNewTaskDueDate);
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        this.dueDateInput.init(year, month, day, null);

        this.cancelNewTask = findViewById(R.id.cancelNewTask);
        this.saveNewTask = findViewById(R.id.saveNewTask);
        this.weeklyRecurringDueDate = findViewById(R.id.recurrenceDaySelection_radio);

        weeklyRecSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weeklyRecurringDueDate.setVisibility(View.VISIBLE);
                    setUpPopUpWindow(0.9,0.85);

                } else {
                    weeklyRecurringDueDate.setVisibility(View.GONE);
                    setUpPopUpWindow(0.9,0.7);
                }
            }
        });
        addTaskData();
        backToMenu();
    }

    // Set up the dimension of pop-up window: 90% width * 70% height
    public void setUpPopUpWindow(double widthProportion, double heightProportion) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*widthProportion), (int) (height*heightProportion));
    }

    public void addTaskData() {
        this.radioMon = findViewById(R.id.radio_mon);
        this.radioTues = findViewById(R.id.radio_tues);
        this.radioWed = findViewById(R.id.radio_wed);
        this.radioThurs = findViewById(R.id.radio_thurs);
        this.radioFri = findViewById(R.id.radio_fri);
        this.radioSat = findViewById(R.id.radio_sat);
        this.radioSun = findViewById(R.id.radio_sun);

        saveNewTask.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               long userID = 0; // Need a method to find which user created the task
               String title = titleInput.getText().toString();
               Log.d(TAG,String.valueOf(estMinInput.getValue()));
               String estHours = String.valueOf(estHoursInput.getValue());
               estHours+=(estMinInput.getValue()==0 ? ".0" : ".5");
               Log.d(TAG,estHours);
               String dueDate = getDateFromDatePicker();
               boolean weeklyR = weeklyRecSwitch.isChecked();
               int recurringDueDate = Calendar.SUNDAY;
               if(weeklyR){
                   recurringDueDate = getRecurringDueDate();
               }
               //check if due date is in the correct format
               int isValidDueDate = validDueDate(dueDate);
               if(title.length()==0){
                   Toast.makeText(NewTask.this,"Please input title",Toast.LENGTH_SHORT).show();
               } else if(isValidDueDate==-1){
                   Toast.makeText(NewTask.this,"Due date invalid",Toast.LENGTH_SHORT).show();
               } else if(isValidDueDate==0){
                   Toast.makeText(NewTask.this,"Due date over",Toast.LENGTH_SHORT).show();
               } else {
                   //if weekly occurring create many new Tasks
                   String startDate, endDate;
                   if(weeklyR) {
                       createWeeklyOccuringTasks(recurringDueDate, userID, title, estHours);
                   } else {
                       startDate = today.get(Calendar.DAY_OF_MONTH) + "/" +
                               today.get(Calendar.MONTH) + "/" + today.get(Calendar.YEAR);
                       endDate = dueDateCal.get(Calendar.DAY_OF_MONTH) + "/" +
                               dueDateCal.get(Calendar.MONTH) + "/" + dueDateCal.get(Calendar.YEAR);
                       mtaskDAO.createTask(userID, title, estHours, startDate, endDate);
                   }
                   Toast.makeText(NewTask.this, "New task added", Toast.LENGTH_LONG).show();
                   // Return back to menu page if new task is added
                   Intent cancelNewTaskIntent = new Intent(getApplicationContext(), Menu.class);
                   startActivity(cancelNewTaskIntent);
                }
            }
        });
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

    public String getDateFromDatePicker(){
        int day = dueDateInput.getDayOfMonth();
        int month = dueDateInput.getMonth();
        int year = dueDateInput.getYear();
        return String.format(Locale.ENGLISH,"%d/%d/%d",day,month+1,year);
    }

    //return 1 is valid, 0 if due date is over, -1 if date format wrong
    public int validDueDate(String dueDate){
        String[] dueDateComponents = dueDate.split("/");
        ArrayList<Integer> monthsWith31 = new ArrayList<>(Arrays.asList(1,3,5,7,8,10,12));
        boolean validInput = true;
        try{
            int day = Integer.parseInt(dueDateComponents[0]);
            int month = Integer.parseInt(dueDateComponents[1]);
            int year = Integer.parseInt(dueDateComponents[2]);
            if(month>0 && month<13 && year>=today.get(Calendar.YEAR) && day>0){
                if(monthsWith31.contains(month)){
                    if(day>31)return -1;
                } else if(month==2) {
                    if(year%4==0){
                        if(day>29) return -1;
                    }
                    else {
                        if(day>28) return -1;
                    }
                } else {
                    if(day>30) return -1;
                }
            } else return -1;
            dueDateCal.set(year,month-1,day,23,59);
            if(dueDateCal.getTimeInMillis()<today.getTimeInMillis()){
                return 0;
            }
            dueYear = year;
            return 1;

        }catch (NumberFormatException e){
            return -1;
        }
    }

    public int getRecurringDueDate(){
        if(radioMon.isChecked()){
            return Calendar.MONDAY;
        } else if(radioTues.isChecked()){
            return Calendar.TUESDAY;
        } else if(radioWed.isChecked()){
            return Calendar.WEDNESDAY;
        } else if(radioThurs.isChecked()){
            return Calendar.THURSDAY;
        } else if(radioFri.isChecked()){
            return Calendar.FRIDAY;
        } else if(radioSat.isChecked()){
            return Calendar.SATURDAY;
        } else {
            return Calendar.SUNDAY;
        }
    }


    public void createWeeklyOccuringTasks(int recurringDueDate, long userID, String title, String estHours){
        String startDate, endDate;
        int daysBetween = recurringDueDate-today.get(Calendar.DAY_OF_WEEK);
        Calendar currentRecurring = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
        //if today is the due date, just ignore today
        //we make currentRecurring to the start date for the task
        if(daysBetween==0){
            currentRecurring.add(Calendar.DATE,7 - NUM_DAYS_BEFORE_RECURRENCE);
        } else if(daysBetween>0){
            //if the number of days before the due date is less than the set number of days before, we start work today
            if(daysBetween<NUM_DAYS_BEFORE_RECURRENCE){
                //DD/MM/YYYY
                startDate = currentRecurring.get(Calendar.DAY_OF_MONTH)+"/"+
                        currentRecurring.get(Calendar.MONTH)+"/"+ currentRecurring.get(Calendar.YEAR);
                currentRecurring.add(Calendar.DATE,daysBetween);
                endDate = currentRecurring.get(Calendar.DAY_OF_MONTH)+"/"+
                        currentRecurring.get(Calendar.MONTH)+"/"+ currentRecurring.get(Calendar.YEAR);
                mtaskDAO.createTask(userID, title, estHours, startDate, endDate);
                currentRecurring.add(Calendar.DATE,7 - NUM_DAYS_BEFORE_RECURRENCE);
            } else {
                currentRecurring.add(Calendar.DATE,daysBetween-NUM_DAYS_BEFORE_RECURRENCE);
            }
        } else{
            if(daysBetween+7<NUM_DAYS_BEFORE_RECURRENCE){
                //DD/MM/YYYY
                startDate = currentRecurring.get(Calendar.DAY_OF_MONTH)+"/"+
                        currentRecurring.get(Calendar.MONTH)+"/"+ currentRecurring.get(Calendar.YEAR);
                currentRecurring.add(Calendar.DATE,daysBetween+7);
                endDate = currentRecurring.get(Calendar.DAY_OF_MONTH)+"/"+
                        currentRecurring.get(Calendar.MONTH)+"/"+ currentRecurring.get(Calendar.YEAR);
                mtaskDAO.createTask(userID, title, estHours, startDate, endDate);
                currentRecurring.add(Calendar.DATE,7 - NUM_DAYS_BEFORE_RECURRENCE);
            } else {
                currentRecurring.add(Calendar.DATE, daysBetween + 7 - NUM_DAYS_BEFORE_RECURRENCE);
            }
        }
        while(currentRecurring.getTimeInMillis()<=dueDateCal.getTimeInMillis()){
            startDate = currentRecurring.get(Calendar.DAY_OF_MONTH)+"/"+
                    currentRecurring.get(Calendar.MONTH)+"/"+ currentRecurring.get(Calendar.YEAR);
            currentRecurring.add(Calendar.DATE,NUM_DAYS_BEFORE_RECURRENCE);
            if(currentRecurring.getTimeInMillis()>dueDateCal.getTimeInMillis()) break;
            endDate = currentRecurring.get(Calendar.DAY_OF_MONTH)+"/"+
                    currentRecurring.get(Calendar.MONTH)+"/"+ currentRecurring.get(Calendar.YEAR);
            mtaskDAO.createTask(userID, title, estHours, startDate, endDate);
            currentRecurring.add(Calendar.DATE,7-NUM_DAYS_BEFORE_RECURRENCE);
        }
    }


}