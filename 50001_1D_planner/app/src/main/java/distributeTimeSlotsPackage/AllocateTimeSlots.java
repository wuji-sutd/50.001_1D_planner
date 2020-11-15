package distributeTimeSlotsPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class AllocateTimeSlots {
    //things to think about later, if like math sci math because of the randomness merge to math math sci

    public static String AllocateTime(ArrayList<TimeSlots> allTimeSlots, ArrayList<Task> allTasks, int numSlotToFill, int numSlotLeftToFill, int currTimeSlotIndex) {
        if (numSlotLeftToFill == 0) {
            for (Task t : allTasks) {
                if (t.completeChecks()!=1) {
                    return t.getName() + "," + t.completeChecks();
                }
            }
            return "1";
        }
        for (Task t : allTasks) {
            if (t.partialChecks()!=1)
                return t.getName() + "," + t.partialChecks();
        }
        TimeSlots currentTimeSlot = allTimeSlots.get(currTimeSlotIndex); //get closest time slot to current day
        //Task currentTask = chooseTaskToAssign(allTasks);
        String isAppropriateTimeSlot = "1";
        int count = 0;
        int chosenRandom;
        Random r = new Random();
        while (currTimeSlotIndex < allTimeSlots.size() - 1) {
            while (count < allTasks.size()) {
                if (allTasks.get(count).checkAssigned()) {
                    count++;
                    continue;
                }
                allTasks.get(count).assignLatestTimeSlot(currentTimeSlot);
//                for (Task t : allTasks) {
//                    t.printTimeSlots();
//                }
                isAppropriateTimeSlot = AllocateTime(allTimeSlots, allTasks, numSlotToFill, numSlotLeftToFill - 1,currTimeSlotIndex+1);
                if (!isAppropriateTimeSlot.equals("1")) {
                    if(isAppropriateTimeSlot.split(",")[1].equals("-2")) {
                        allTasks.get(count).removeLatestTimeSlot();
                    }
                    else return isAppropriateTimeSlot;
                } else return "1";
                count++;
            }
            int currentWeekofYear = currentTimeSlot.getWeekofYear();
            while (currTimeSlotIndex < allTimeSlots.size() - 1) {
                if(allTimeSlots.get(currTimeSlotIndex).getWeekofYear()!=currentWeekofYear) break;
                currTimeSlotIndex++;
            }
            currentTimeSlot = allTimeSlots.get(currTimeSlotIndex);
            count=0;
        }
        return isAppropriateTimeSlot;
    }


}
