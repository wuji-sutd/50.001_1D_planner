package com.example.a50001_1d_planner;

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
    private TaskDAO taskDAO;
    private WorkingHoursDAO workingHoursDAO;
    private String TAG = "TimeTableActivity";
    private ArrayList<Task> tasks;
    private boolean canDisplay;

    private LinearLayout timeTableDisplayLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        timeTableDisplayLayout = findViewById(R.id.timeTableTaskDisplay);
        this.taskDAO = new TaskDAO(this);
        this.workingHoursDAO = new WorkingHoursDAO(this);
        tasks = new ArrayList<>();
        //get all timeslots
        ArrayList<TimeSlots> timeslots = new ArrayList<>();
        //get all tasks
        tasks = taskDAO.getAllTasks(timeslots);
        HashMap<String, Integer> numSlotsPerWeek = new HashMap<>();
        workingHoursDAO.getAllAvailableTimeSlots(timeslots,numSlotsPerWeek);
        ArrayList<AvailableDay> recentlyUpdatedDays = workingHoursDAO.getRecentlyUpdatedAvailableDays();
        ArrayList<AvailableDay> availableDays = workingHoursDAO.getAllAvailableDays();
        canDisplay = true;

        //check if any of the working days were updated first
        if(recentlyUpdatedDays.size()>0){
            if(!changeAvailableDaysFixed(timeslots,availableDays)) canDisplay =false;
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
            }
            if (needsAssigning) {
                if (!addNewTaskAfter(unassignedTasks, timeslots, availableDays,numSlotsPerWeek)) {
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

        Button checkList = findViewById(R.id.checkList);
        checkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkListIntent = new Intent(getApplicationContext(), CheckList.class);
                startActivity(checkListIntent);
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
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        TextView textView = findViewById(R.id.textView);
        textView.setText(currentDateString);
        displayDayTasks(year,month,dayOfMonth);
    }
    public void displayDayTasks(int curYear, int curMonth, int curDay){
        timeTableDisplayLayout.removeAllViews();
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
                    LinearLayout taskLinearLayout = new LinearLayout(this);
                    TextView taskSlotTextView = new TextView(this);
                    taskSlotTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
                    taskSlotTextView.setText(ts.toString());
                    taskSlotTextView.setTextSize(20);
                    taskSlotTextView.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
                    taskLinearLayout.addView(taskSlotTextView);
                    timeTableDisplayLayout.addView(taskLinearLayout);
                }
            }
            else{
                TextView taskSlotTextView = new TextView(this);
                taskSlotTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                taskSlotTextView.setText("No tasks assigned today");
                taskSlotTextView.setTextSize(20);
                taskSlotTextView.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
                timeTableDisplayLayout.addView(taskSlotTextView);
            }

        }
        else{
            TextView taskSlotTextView = new TextView(this);
            taskSlotTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            taskSlotTextView.setText("Please set more working hours");
            taskSlotTextView.setTextSize(10);
            taskSlotTextView.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            timeTableDisplayLayout.addView(taskSlotTextView);
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


    //after the initial setting up, the user adds a new task
    public boolean addNewTaskAfter(ArrayList<Task> newTasks, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays, HashMap<String, Integer> numSlotsPerWeek){
        Log.d(TAG,"trying with available first");
        return addToExistingTimeTable(newTasks, timeslots, availableDays, numSlotsPerWeek);
    }

    //this should be wherever the user adds or edits a task and when task cannot be finished on time
    public boolean checkHoursPerWeekEnough(ArrayList<Task> tasksToCheck, ArrayList<AvailableDay> availableDays){
        for(Task t:tasksToCheck){
            if(!t.checkEnoughTime(availableDays)){
                Log.d(TAG,t.getTitle() + ": the number of hours a week is insufficient to complete the task");
                return false;
            }
        }
        return true;
    }

    public boolean checkEnoughTimeSlots(ArrayList<Task> tasksToCheck, ArrayList<TimeSlots> timeslots, int totalTimeSlotsNeeded, ArrayList<AvailableDay> availableDays){
        if(!checkHoursPerWeekEnough(tasksToCheck,availableDays)) return false;
        return totalTimeSlotsNeeded<=timeslots.size();
    }

    public boolean addToExistingTimeTable(ArrayList<Task> taskToTry, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays, HashMap<String, Integer> numSlotsPerWeek){
        //get whatever timeslot is left
        ArrayList<TimeSlots> availableTimeslots = new ArrayList<>();
        workingHoursDAO.getAvailableTimeSlots(timeslots, availableTimeslots);

        if(!callAllocation(taskToTry,availableTimeslots,availableDays)){
            //create a new list of everything so as not to overwrite anything
            ArrayList<Task> allTasksClone = new ArrayList<>();
            for (Task currentTask : tasks) {
                //String name, int dueYear, int dueMonth, int dueDay, double time, int hoursNeeded, int hoursPerWeek
                allTasksClone.add(new Task(currentTask.getUserID(),currentTask.getTaskID(),currentTask.getTitle(), String.valueOf(currentTask.getEstHours()), currentTask.getStartDate(), currentTask.getDueDate(), currentTask.getTime()));
            }
            //remove any tasks that are already over
            for (Iterator<Task> itr = allTasksClone.iterator(); itr.hasNext();) {
                Task curTask = itr.next();
                if (curTask.getEstHours()==0) {
                    taskDAO.deleteTask(curTask);
                    itr.remove();
                }
            }
            ArrayList<TimeSlots> timeslotsNew = new ArrayList<>();
            HashMap<String, Integer> numSlotsPerWeekNew = new HashMap<>();
            workingHoursDAO.getAllAvailableTimeSlots(timeslotsNew, numSlotsPerWeekNew);
            Log.d(TAG,"trying with all time slots");
            if(callAllocation(allTasksClone,timeslotsNew,availableDays)){
                //copy over
                tasks = allTasksClone;
                timeslots = timeslotsNew;
                numSlotsPerWeek = numSlotsPerWeekNew;
                return true;
            } else return false;
        }
        return true;
    }

    //TODO: implement this when there is a button to say that a task is not completed
    public void taskNotCompleted(String notCompletedTaskName, int numSlotsMissed, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays,HashMap<String, Integer> numSlotsPerWeek){
        ArrayList<Task> incompleteTask = new ArrayList<>();
        for(Task t :tasks){
            if(t.getTitle().equals(notCompletedTaskName)){
                t.remakeTimeSlots(numSlotsMissed);
                incompleteTask.add(t);
                break;
            }
        }
        if(incompleteTask.size()==0){
            Log.d(TAG,"cannot find task");
            return;
        }
        addToExistingTimeTable(incompleteTask, timeslots, availableDays, numSlotsPerWeek);
    }

    public boolean changeAvailableDaysFixed(ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays){
        Log.d(TAG,"\n\nTesting changeAvailableDaysFixed");
        for(Task task:tasks){
            task.remakeTimeSlots();
        }
        //remove any tasks that are already over
        for (Iterator<Task> itr = tasks.iterator(); itr.hasNext();) {
            Task curTask = itr.next();
            if (curTask.getEstHours()==0) {
                taskDAO.deleteTask(curTask);
                itr.remove();
            }
        }
        for(TimeSlots ts :timeslots){
            Log.d(TAG,ts.toString());
        }
        return callAllocation(tasks,timeslots,availableDays);
    }

    public boolean callAllocation(ArrayList<Task> allocationTasks,ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays){
        int totalTimeSlotsNeeded = 0;
        for(Task t:allocationTasks){
            totalTimeSlotsNeeded +=t.getNumTaskSlotsNeeded();
        }
        if(!checkEnoughTimeSlots(allocationTasks,timeslots, totalTimeSlotsNeeded,availableDays)) {
            Log.d(TAG,"Need more time slots!!!");
            return false;
        }
        Log.d(TAG,"total time slots needed:"+totalTimeSlotsNeeded);

        Collections.sort(allocationTasks);
        for(Task t:allocationTasks) {
            Log.d(TAG,t.getTitle());
            Log.d(TAG,"startTime: "+t.getStartDateCal().getTime().toString());
        }
        String outputOfAllocation = AllocateTimeSlots.AllocateTime(timeslots,allocationTasks,totalTimeSlotsNeeded,totalTimeSlotsNeeded,0);
        if(outputOfAllocation.equals("1")) {
            //update database
            for (Task t : allocationTasks) {
                Log.d(TAG,"allocated");
                t.printTimeSlots(TAG);
                taskDAO.updateTaskTimeSlot(t);
            }
            return true;
        }
        else{
            /*just for reference
                //if(!checkBeforeDeadLine()) return -1;
                //if(!checkMaxWeeklyHours()) return -2;
                //if(!checkAssigned()) return -3;
             */
            String[] outputSplit = outputOfAllocation.split(",");
            if ("-3".equals(outputSplit[1])) {
                Log.d(TAG,"Not Enough Time Slots for all Tasks");
            }
            return false;
        }
    }


}