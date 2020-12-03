package com.example.a50001_1d_planner;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

import distributeTimeSlotsPackage.AvailableDay;
import distributeTimeSlotsPackage.TimeSlots;

public class EditTask extends AppCompatActivity {

    private final String TAG = "EditTaskActivity";
    private final int NUM_DAYS_BEFORE_RECURRENCE = 3;
    private TaskDAO taskDAO;
    private WorkingHoursDAO workingHoursDAO;
    private ArrayList<Task> tasks;

    private Spinner titleSelection;
    private NumberPicker estHoursInput;
    private NumberPicker estMinInput;
    private DatePicker dueDateInput;

    private Switch weeklyRecSwitch;

    private Button cancelEditTask;
    private Button saveEditTask;
    private Button deleteEditTask;
    private RadioGroup weeklyRecurringDueDate;

    //radio buttons
    private RadioButton radioMon;
    private RadioButton radioTues;
    private RadioButton radioWed;
    private RadioButton radioThurs;
    private RadioButton radioFri;
    private RadioButton radioSat;
    private RadioButton radioSun;

    private ArrayList<Task> focusingTasks;
    Calendar today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
    Calendar dueDateCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));

    //TODO: add a delete button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);
        Log.d(TAG,"calendar time" + today.getTime().toString());
        weeklyRecSwitch = (Switch)findViewById(R.id.editWeeklyRecSwitch);
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
        this.taskDAO = new TaskDAO(this);
        this.titleSelection = findViewById(R.id.selectEditTask);
        focusingTasks = new ArrayList<>();
        HashSet<String> taskNameSelection = getTaskNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,taskNameSelection.toArray(new String[taskNameSelection.size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        titleSelection.setAdapter(adapter);

        titleSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFieldsForTask(titleSelection.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        this.estHoursInput = findViewById(R.id.inputEditEstHours);
        this.estHoursInput.setMaxValue(23);
        this.estHoursInput.setMinValue(0);
        this.estMinInput = findViewById(R.id.inputEditEstMin);

        this.estMinInput.setMaxValue(1);
        this.estMinInput.setMinValue(0);
        String[] minuteValues = {"00","30"};
        estMinInput.setDisplayedValues(minuteValues);

        this.dueDateInput = findViewById(R.id.inputEditTaskDueDate);
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        this.dueDateInput.init(year, month, day, null);

        this.cancelEditTask = findViewById(R.id.cancelEditTask);
        this.saveEditTask = findViewById(R.id.saveEditTask);
        this.deleteEditTask = findViewById(R.id.deleteEditTask);
        this.weeklyRecurringDueDate = findViewById(R.id.editRecurrenceDaySelection_radio);

        weeklyRecSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weeklyRecurringDueDate.setVisibility(View.VISIBLE);
                } else {
                    weeklyRecurringDueDate.setVisibility(View.GONE);
                }
            }
        });
        addTaskData();
        backToMenu();
        deleteEditTaskFromDB();
    }

    public void addTaskData() {
        this.radioMon = findViewById(R.id.radio_mon);
        this.radioTues = findViewById(R.id.radio_tues);
        this.radioWed = findViewById(R.id.radio_wed);
        this.radioThurs = findViewById(R.id.radio_thurs);
        this.radioFri = findViewById(R.id.radio_fri);
        this.radioSat = findViewById(R.id.radio_sat);
        this.radioSun = findViewById(R.id.radio_sun);

        saveEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long userID = 0; // Need a method to find which user created the task
                String title = titleSelection.getSelectedItem().toString();
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
                    Toast.makeText(EditTask.this, "Please input title", Toast.LENGTH_SHORT).show();
                } else if (estHoursInput.getValue()==0 && estMinInput.getValue()==0){
                    Toast.makeText(EditTask.this, "Please select estimated time", Toast.LENGTH_SHORT).show();
                } else if(isValidDueDate==-1){
                    Toast.makeText(EditTask.this,"Due date invalid",Toast.LENGTH_LONG).show();
                } else if(isValidDueDate==0){
                    Toast.makeText(EditTask.this,"Due date over",Toast.LENGTH_LONG).show();
                } else {
                    //check how different the task is from the original task
                    //first figure out what the task is
                    //if estimated hours are different only remove time slots, allocate correct number of task slots and reassign
                    //if due date different is longer, and weekly occuring add new tasks, if due date is shorter, remove tasks
                    //if due date different not weekly recurring, change the due date and weekly hours and reassign
                    //if weekly recurring change to non-weekly recurring, find out due date and save it as a single task
                    //if not weekly recurring and then becomes weekly recurring, delete task and redistribute
                    //if weekly recurring and changed date of recurrence, delete tasks and redistribute
                    if(weeklyR && focusingTasks.size()==1) { //changing from non-weekly to weekly
                        focusingTasks.get(0).remakeTimeSlots();
                        taskDAO.deleteTask(focusingTasks.get(0));
                        createWeeklyOccuringTasks(recurringDueDate, userID, title, estHours,false);
                    } else if (weeklyR && recurringDueDate!=focusingTasks.get(0).getCal().get(Calendar.DAY_OF_WEEK)){
                        //check if days for recurrance is diff
                        for(Task ft:focusingTasks) {
                            ft.remakeTimeSlots();
                            taskDAO.deleteTask(ft);
                        }
                        createSingleTask(userID, title, estHours);
                    } else if(focusingTasks.size()>1){ //changing from weekly to non-weekly
                        for(Task ft:focusingTasks) {
                            ft.remakeTimeSlots();
                            taskDAO.deleteTask(ft);
                        }
                        createSingleTask(userID, title, estHours);
                    } else { //not changing weekly occurring doesn't require complete recreation
                        if (dueDateInput.getYear() != focusingTasks.get(focusingTasks.size() - 1).getCal().get(Calendar.YEAR) ||
                                dueDateInput.getMonth() != focusingTasks.get(focusingTasks.size() - 1).getCal().get(Calendar.MONTH) ||
                                dueDateInput.getDayOfMonth() != focusingTasks.get(focusingTasks.size() - 1).getCal().get(Calendar.DAY_OF_MONTH)) {
                            //due date different
                            if (weeklyR) {
                                if (dueDateCal.getTime().after(focusingTasks.get(focusingTasks.size() - 1).getCal().getTime())) {
                                    //if the new due date is after the original due date add dates
                                    createWeeklyOccuringTasks(recurringDueDate, userID, title, estHours,true);
                                } else { //remove dates if new due date is before original due date
                                    for (Task ft : focusingTasks) {
                                        if (ft.getCal().getTime().after(dueDateCal.getTime())) {
                                            ft.remakeTimeSlots();
                                            taskDAO.deleteTask(ft);
                                        }
                                    }
                                }
                            } else {//not weekly occurring but due date diff
                                focusingTasks.get(0).changeDueDateCal(dueDateCal);
                            }
                        }
                        if ((estHoursInput.getValue() + estMinInput.getValue() * 0.5) != focusingTasks.get(0).getHoursNeededLeft()) {
                            //estimatedTime is different
                            for(Task ft:focusingTasks) {
                                ft.changeEstimatedHours(estHoursInput.getValue(),estMinInput.getValue());
                            }
                        }
                        for(Task ft:focusingTasks) {
                            taskDAO.updateTaskTimeSlot(ft);
                        }
                    }
                    Toast.makeText(EditTask.this, "Task Edited", Toast.LENGTH_LONG).show();
                    // Return back to menu page if new task is added
                    finish();
                }
            }
        });
    }

    // Return back to menu page if the 'CANCEL' button is clicked
    public void backToMenu() {
        cancelEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void deleteEditTaskFromDB() {
        deleteEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Task ft:focusingTasks){
                    Log.d(TAG,ft.getTitle()+ft.getTaskID());
                    ft.remakeTimeSlots();
                    taskDAO.deleteTask(ft);
                }
                Intent deleteEditTaskIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(deleteEditTaskIntent);
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

    public void createSingleTask(long userID, String title, String estHours){
        String startDate = today.get(Calendar.DAY_OF_MONTH) + "/" +
                today.get(Calendar.MONTH) + "/" + today.get(Calendar.YEAR);
        String endDate = dueDateCal.get(Calendar.DAY_OF_MONTH) + "/" +
                dueDateCal.get(Calendar.MONTH) + "/" + dueDateCal.get(Calendar.YEAR);
        taskDAO.createTask(userID, title, estHours, startDate, endDate);

    }


    public void createWeeklyOccuringTasks(int recurringDueDate, long userID, String title, String estHours,boolean addingNewRecurrence){
        String startDate, endDate;
        Calendar currentRecurring = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));

        int daysBetween =recurringDueDate-today.get(Calendar.DAY_OF_WEEK);;
        if(addingNewRecurrence) {
            daysBetween = 0;
            Task finalTask = focusingTasks.get(focusingTasks.size()-1);
            currentRecurring.set(finalTask.getCal().get(Calendar.YEAR),finalTask.getCal().get(Calendar.MONTH),finalTask.getCal().get(Calendar.DAY_OF_MONTH));
        }
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
                taskDAO.createTask(userID, title, estHours, startDate, endDate);
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
                taskDAO.createTask(userID, title, estHours, startDate, endDate);
                currentRecurring.add(Calendar.DATE,7 - NUM_DAYS_BEFORE_RECURRENCE);
            } else {
                currentRecurring.add(Calendar.DATE, daysBetween + 7 - NUM_DAYS_BEFORE_RECURRENCE);
            }
        }
        currentRecurring.set(Calendar.HOUR_OF_DAY,0);
        currentRecurring.set(Calendar.MINUTE,0);

        while(currentRecurring.getTimeInMillis()<=dueDateCal.getTimeInMillis()){
            startDate = currentRecurring.get(Calendar.DAY_OF_MONTH)+"/"+
                    currentRecurring.get(Calendar.MONTH)+"/"+ currentRecurring.get(Calendar.YEAR);
            currentRecurring.add(Calendar.DATE,NUM_DAYS_BEFORE_RECURRENCE);
            if(currentRecurring.getTimeInMillis()>dueDateCal.getTimeInMillis()) break;
            endDate = currentRecurring.get(Calendar.DAY_OF_MONTH)+"/"+
                    currentRecurring.get(Calendar.MONTH)+"/"+ currentRecurring.get(Calendar.YEAR);
            taskDAO.createTask(userID, title, estHours, startDate, endDate);
            currentRecurring.add(Calendar.DATE,7-NUM_DAYS_BEFORE_RECURRENCE);
        }
    }

    public HashSet<String> getTaskNames(){
        HashSet<String> tasksNames = new HashSet<>();
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

        for(Task t:tasks){
            Log.d(TAG,"start:" +t.getTitle()+t.getTaskID() +":" +t.getNumTaskSlotsNeeded());
            tasksNames.add(t.getTitle());
        }

        for(String s :tasksNames) {
            Log.d(TAG,"hashset: "+ s);
        }
        return tasksNames;
    }

    public void updateFieldsForTask(String taskName){
        focusingTasks.clear();
        for(Task t:tasks){
            if(t.getTitle().equals(taskName)){
                focusingTasks.add(t);
            }
        }
        if(focusingTasks.size() == 0){
            Log.d(TAG,"cannot find focusing task");
            return;
        }
        if(focusingTasks.size() >1){ //recurring task
            weeklyRecSwitch.setChecked(true); //may need to automatically change the view for this
            int recurringDay = focusingTasks.get(0).getCal().get(Calendar.DAY_OF_WEEK);
            setSelectedRadioButton(recurringDay);
        } else{
            weeklyRecSwitch.setChecked(false);
        }

        Calendar getDueDate = focusingTasks.get(focusingTasks.size()-1).getCal();
        dueDateInput.updateDate(getDueDate.get(Calendar.YEAR),getDueDate.get(Calendar.MONTH),getDueDate.get(Calendar.DAY_OF_MONTH));
        double estimatedHoursLeft = focusingTasks.get(focusingTasks.size()-1).getHoursNeededLeft();
        estHoursInput.setValue((int)estimatedHoursLeft);
        estMinInput.setValue(estimatedHoursLeft - (int)estimatedHoursLeft==0? 0:1);
    }

    public void setSelectedRadioButton(int recurringDay){
        switch (recurringDay){
            case Calendar.MONDAY:
                weeklyRecurringDueDate.check(R.id.radio_mon);
                break;
            case Calendar.TUESDAY:
                weeklyRecurringDueDate.check(R.id.radio_tues);
                break;
            case Calendar.WEDNESDAY:
                weeklyRecurringDueDate.check(R.id.radio_wed);
                break;
            case Calendar.THURSDAY:
                weeklyRecurringDueDate.check(R.id.radio_thurs);
                break;
            case Calendar.FRIDAY:
                weeklyRecurringDueDate.check(R.id.radio_fri);
                break;
            case Calendar.SATURDAY:
                weeklyRecurringDueDate.check(R.id.radio_sat);
                break;
            case Calendar.SUNDAY:
                weeklyRecurringDueDate.check(R.id.radio_sun);
                break;
        }
    }
}
