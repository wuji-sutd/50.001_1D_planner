package com.example.a50001_1d_planner;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import distributeTimeSlotsPackage.AllocateTimeSlots;
import distributeTimeSlotsPackage.AvailableDay;
import distributeTimeSlotsPackage.TimeSlots;

public class GetAllocatedTasks {
    private final static String TAG = "GetAllocatedTaskJava";

    //after the initial setting up, the user adds a new task
    public static boolean addNewTaskAfter(Context context, ArrayList<Task> newTasks, ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays, HashMap<String, Integer> numSlotsPerWeek){
        Log.d(TAG,"trying with available first");
        return addToExistingTimeTable(context, newTasks, tasks, timeslots, availableDays, numSlotsPerWeek);

    }

    //this should be wherever the user adds or edits a task and when task cannot be finished on time
    public static boolean checkHoursPerWeekEnough(ArrayList<Task> tasksToCheck, ArrayList<AvailableDay> availableDays){
        for(Task t:tasksToCheck){
            if(!t.checkEnoughTime(availableDays)){
                Log.d(TAG,t.getTitle() + ": the number of hours a week is insufficient to complete the task");
                return false;
            }
        }
        return true;
    }

    public static boolean checkEnoughTimeSlots(ArrayList<Task> tasksToCheck, ArrayList<TimeSlots> timeslots, int totalTimeSlotsNeeded, ArrayList<AvailableDay> availableDays){
        if(!checkHoursPerWeekEnough(tasksToCheck,availableDays)) return false;
        return totalTimeSlotsNeeded<=timeslots.size();
    }

    public static boolean addToExistingTimeTable(Context context, ArrayList<Task> taskToTry, ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays, HashMap<String, Integer> numSlotsPerWeek){
        TaskDAO taskDAO = new TaskDAO(context);
        WorkingHoursDAO workingHoursDAO = new WorkingHoursDAO(context);
        //get whatever timeslot is left
        ArrayList<TimeSlots> availableTimeslots = new ArrayList<>();
        workingHoursDAO.getAvailableTimeSlots(timeslots, availableTimeslots);

        if(!callAllocation(context, taskToTry,availableTimeslots,availableDays)){
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
            if(callAllocation(context, allTasksClone,timeslotsNew,availableDays)){
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
    public static void taskNotCompleted(Context context, String notCompletedTaskName, int numSlotsMissed, ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays,HashMap<String, Integer> numSlotsPerWeek){
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
        addToExistingTimeTable(context,incompleteTask, tasks, timeslots, availableDays, numSlotsPerWeek);
    }

    public static boolean changeAvailableDaysFixed(Context context, ArrayList<Task> tasks,ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays){
        TaskDAO taskDAO = new TaskDAO(context);
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
        return callAllocation(context, tasks,timeslots,availableDays);
    }

    public static boolean callAllocation(Context context, ArrayList<Task> allocationTasks,ArrayList<TimeSlots> timeslots, ArrayList<AvailableDay> availableDays){
        TaskDAO taskDAO = new TaskDAO(context);
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
