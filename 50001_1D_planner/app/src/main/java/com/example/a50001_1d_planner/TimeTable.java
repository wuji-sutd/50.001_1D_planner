package com.example.a50001_1d_planner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import distributeTimeSlotsPackage.AllocateTimeSlots;
import distributeTimeSlotsPackage.AvailableDay;
import distributeTimeSlotsPackage.ConsolidatedAvailableDays;
import distributeTimeSlotsPackage.GetDatabaseTimeSlots;
import distributeTimeSlotsPackage.TimeSlots;

public class TimeTable extends AppCompatActivity {
    private TaskDAO taskDAO;
    private WorkingHoursDAO workingHoursDAO;
    private String TAG = "TimeTableActivity";

    private LinearLayout timeTableDisplayLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        timeTableDisplayLayout = findViewById(R.id.timeTableDisplay);
        this.taskDAO = new TaskDAO(this);
        this.workingHoursDAO = new WorkingHoursDAO(this);
        //get all timeslots
        ArrayList<TimeSlots> timeslots = new ArrayList<>();
        //get all tasks
        ArrayList<Task> tasks = taskDAO.getAllTasks(timeslots);
        HashMap<String, Integer> numSlotsPerWeek = new HashMap<>();
        workingHoursDAO.getAllAvailableTimeSlots(timeslots,numSlotsPerWeek);
        ArrayList<AvailableDay> recentlyUpdatedDays = workingHoursDAO.getRecentlyUpdatedAvailableDays();
        ArrayList<AvailableDay> availableDays = workingHoursDAO.getAllAvailableDays();
        boolean canDisplay = true;

        //check if any of the working days were updated first
        if(recentlyUpdatedDays.size()>0){
            if(!changeAvailableDaysFixed(tasks, timeslots,availableDays)) canDisplay =false;
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
                if (!addNewTaskAfter(tasks, unassignedTasks, timeslots, availableDays,numSlotsPerWeek)) {
                    canDisplay = false;
                }
            }
        }
        if(canDisplay){
            //display all the time slots
            for(Task t: tasks){
                ArrayList<String> timeslotsStringArray = t.getArrayListOfTimeSlots();
                for(String timeslotsString: timeslotsStringArray) {
                    TextView tTimeSlots = new TextView(this);
                    tTimeSlots.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    timeTableDisplayLayout.addView(tTimeSlots);
                }
            }
        } else {
            TextView t = new TextView(this);
            t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            timeTableDisplayLayout.addView(t);
        }

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
    }

    //after the initial setting up, the user adds a new task
    public boolean addNewTaskAfter(ArrayList<Task> tasks, ArrayList<Task> newTasks, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays, HashMap<String, Integer> numSlotsPerWeek){
        Log.d(TAG,"trying with available first");
        return addToExistingTimeTable(newTasks, tasks, timeslots, availableDays, numSlotsPerWeek);
    }

    //this should be wherever the user adds or edits a task and when task cannot be finished on time
    public boolean checkHoursPerWeekEnough(ArrayList<Task> tasks, ArrayList<AvailableDay> availableDays){
        for(Task t:tasks){
            if(!t.checkEnoughTime(availableDays)){
                Log.d(TAG,t.getTitle() + ": the number of hours a week is insufficient to complete the task");
                return false;
            }
        }
        return true;
    }

    public boolean checkEnoughTimeSlots(ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, int totalTimeSlotsNeeded, ArrayList<AvailableDay> availableDays){
        if(!checkHoursPerWeekEnough(tasks,availableDays)) return false;
        return totalTimeSlotsNeeded<=timeslots.size();
    }

    public boolean addToExistingTimeTable(ArrayList<Task> taskToTry, ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays, HashMap<String, Integer> numSlotsPerWeek){
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
                if (itr.next().getEstHours()==0) {
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
    public void taskNotCompleted(String notCompletedTaskName, int numSlotsMissed, ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays,HashMap<String, Integer> numSlotsPerWeek){
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
        addToExistingTimeTable(incompleteTask, tasks, timeslots, availableDays, numSlotsPerWeek);
    }

    //TODO: implement this when I figure out how to tell if the working hours are changed
    public boolean changeAvailableDaysFixed(ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays){
        Log.d(TAG,"\n\nTesting changeAvailableDaysFixed");
        for(Task task:tasks){
            task.remakeTimeSlots();
        }
        //remove any tasks that are already over
        for (Iterator<Task> itr = tasks.iterator(); itr.hasNext();) {
            if (itr.next().getEstHours()==0) {
                itr.remove();
            }
        }
        return callAllocation(tasks,timeslots,availableDays);
    }

    public boolean callAllocation(ArrayList<Task> tasks,ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays){
        int totalTimeSlotsNeeded = 0;
        for(Task t:tasks){
            totalTimeSlotsNeeded +=t.getNumTaskSlotsNeeded();
        }
        if(!checkEnoughTimeSlots(tasks,timeslots, totalTimeSlotsNeeded,availableDays)) {
            Log.d(TAG,"Need more time slots!!!");
            return false;
        }

        Collections.sort(tasks);
        for(Task t:tasks) Log.d(TAG,t.getTitle());
        String outputOfAllocation = AllocateTimeSlots.AllocateTime(timeslots,tasks,totalTimeSlotsNeeded,totalTimeSlotsNeeded,0);
        if(outputOfAllocation.equals("1")) {
            //update database
            for (Task t : tasks) {
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