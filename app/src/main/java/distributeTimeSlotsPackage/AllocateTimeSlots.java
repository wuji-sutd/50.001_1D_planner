package distributeTimeSlotsPackage;

import android.util.Log;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import com.example.a50001_1d_planner.Task;

public class AllocateTimeSlots {
    private static String TAG = "AllocateTimeSlotsClass";
    public static Calendar today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
    public static String AllocateTime(ArrayList<TimeSlots> allTimeSlots, ArrayList<Task> allTasks, int numSlotToFill, int numSlotLeftToFill, int currTimeSlotIndex) {
        Log.d(TAG,"reached function");
        if (numSlotLeftToFill == 0) {
            Log.d(TAG,"no slots left to fill");

            for (Task t : allTasks) {
                if (t.completeChecks()!=1) {
                    return t.getTitle() + "," + t.completeChecks();
                }
            }
            return "1";
        }
        for (Task t : allTasks) {
            if (t.partialChecks()!=1) {
                Log.d(TAG,"partialCheck");
                return t.getTitle() + "," + t.partialChecks();
            }
        }
        TimeSlots currentTimeSlot = allTimeSlots.get(currTimeSlotIndex); //get closest time slot to current day
        if(currentTimeSlot.getCal().get(Calendar.DAY_OF_YEAR)==today.get(Calendar.DAY_OF_YEAR) && currentTimeSlot.getCal().get(Calendar.YEAR)==today.get(Calendar.YEAR)){
            Log.d(TAG,"today");
            while(currentTimeSlot.getCal().compareTo(today)<0){
                Log.d(TAG,"older");
                currTimeSlotIndex++;
                currentTimeSlot = allTimeSlots.get(currTimeSlotIndex);
            }
        }
        while(currentTimeSlot.getIsBreak()){
            currTimeSlotIndex++;
            currentTimeSlot = allTimeSlots.get(currTimeSlotIndex);
        }


            //Task currentTask = chooseTaskToAssign(allTasks);
        String isAppropriateTimeSlot = "1";
        int count = 0;
        while (currTimeSlotIndex < allTimeSlots.size() - 1) {
            while (count < allTasks.size()) {
                if (allTasks.get(count).checkAssigned()) {
                    Log.d(TAG,"all assigned");
                    count++;
                    continue;
                }
                //if allocating time slot is before the start date for the task, skip the task
                if(allTimeSlots.get(currTimeSlotIndex).getCal().compareTo(allTasks.get(count).getStartDateCal())<0){
                    Log.d(TAG,"start time interfering");
                    Log.d(TAG,allTimeSlots.get(currTimeSlotIndex).getCal().getTime().toString());
                    Log.d(TAG,allTasks.get(count).getStartDateCal().getTime().toString());
                    Log.d(TAG,allTimeSlots.get(currTimeSlotIndex).getCal().getTimeInMillis()+","+allTasks.get(count).getStartDateCal().getTimeInMillis());
                    count++;
                    continue;
                }

                allTasks.get(count).assignLatestTimeSlot(currentTimeSlot);
                for (Task t : allTasks) {
                    t.printTimeSlots(TAG);
                }
                isAppropriateTimeSlot = AllocateTime(allTimeSlots, allTasks, numSlotToFill, numSlotLeftToFill - 1,currTimeSlotIndex+1);
                if (!isAppropriateTimeSlot.equals("1")) {
                    Log.d(TAG,"not 1: "+isAppropriateTimeSlot);
                    if(isAppropriateTimeSlot.split(",")[1].equals("-3"))
                        return isAppropriateTimeSlot;

                    //if(isAppropriateTimeSlot.split(",")[1].equals("-2")) {
                        allTasks.get(count).removeLatestTimeSlot();
                    //}
                    //else return isAppropriateTimeSlot;
                } else return "1";
                count++;
            }
            int currentDayofYear = currentTimeSlot.getDayofYear();
            while (currTimeSlotIndex < allTimeSlots.size() - 1) {
                if(allTimeSlots.get(currTimeSlotIndex).getDayofYear()!=currentDayofYear) break;
                currTimeSlotIndex++;
            }
            while(currentTimeSlot.getIsBreak()){
                currTimeSlotIndex++;
                currentTimeSlot = allTimeSlots.get(currTimeSlotIndex);
            }
            for(Task t:allTasks){
                if(!t.checkAssigned()) {
                    if (allTimeSlots.get(currTimeSlotIndex).getCal().compareTo(t.getCal())>0){
                        Log.d(TAG, "timeslot over due date");
                        Log.d(TAG, allTimeSlots.get(currTimeSlotIndex).toString());
                        Log.d(TAG, t.getCal().getTime().toString());
                        return "all,-3"; //completely failed
                    }
                }
            }
            currentTimeSlot = allTimeSlots.get(currTimeSlotIndex);
            count=0;
        }
        return isAppropriateTimeSlot;
    }

}
