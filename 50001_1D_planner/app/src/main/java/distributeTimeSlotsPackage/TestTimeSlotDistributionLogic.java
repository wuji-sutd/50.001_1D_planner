package distributeTimeSlotsPackage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

public class TestTimeSlotDistributionLogic {
    //
    //    private static final int testingNumMonths = 2;
    //    //replace in GetDataBaseTimeSlots
    //    public static void addAvailableDaysFixed(){
    //        TreeMap<Double,Double> mondayTimePeriod = new TreeMap<>();
    //        mondayTimePeriod.put(10.0,12.0);
    //        mondayTimePeriod.put(14.5,17.0);
    //        TreeMap<Double,Double> tuesdayTimePeriod = new TreeMap<>();
    //        tuesdayTimePeriod.put(9.0,13.0);
    //        TreeMap<Double,Double> wednesdayTimePeriod = new TreeMap<>();
    //        wednesdayTimePeriod.put(8.0,9.0);
    //        wednesdayTimePeriod.put(17.5,20.0);
    //        TreeMap<Double,Double> thursdayTimePeriod = new TreeMap<>();
    //        thursdayTimePeriod.put(8.0,9.0);
    //        thursdayTimePeriod.put(17.5,20.0);
    //        TreeMap<Double,Double> fridayTimePeriod = new TreeMap<>();
    //        fridayTimePeriod.put(8.0,9.0);
    //        fridayTimePeriod.put(17.5,20.0);
    //        TreeMap<Double,Double> sundayTimePeriod = new TreeMap<>();
    //        sundayTimePeriod.put(10.0,12.0);
    //        sundayTimePeriod.put(14.0,18.0);
    //
    //        ConsolidatedAvailableDays consolidatedAvailableDays = ConsolidatedAvailableDays.getInstance();
    //        consolidatedAvailableDays.setAvailableTime(Calendar.MONDAY,mondayTimePeriod);
    //        consolidatedAvailableDays.setAvailableTime(Calendar.TUESDAY,tuesdayTimePeriod);
    //        consolidatedAvailableDays.setAvailableTime(Calendar.WEDNESDAY,wednesdayTimePeriod);
    //        consolidatedAvailableDays.setAvailableTime(Calendar.THURSDAY,thursdayTimePeriod);
    //        consolidatedAvailableDays.setAvailableTime(Calendar.FRIDAY,fridayTimePeriod);
    //        consolidatedAvailableDays.setAvailableTime(Calendar.SUNDAY, sundayTimePeriod);
    //    }
    //
    //    //replaced in GetDataBaseTasks
    //
    //    public static void addTasksFixed(ArrayList<Task> tasks){
    //        //note: ArrayList are passed by reference
    //        //Task(String name, int dueYear, int dueMonth, int dueDay, double time, int hoursNeeded, int hoursPerWeek)
    //        tasks.add(new Task("Com Struct Problems",2020,Calendar.DECEMBER,30,13.5,14,3));
    //        tasks.add(new Task("Com Struct Project",2020,Calendar.DECEMBER,30,23.30,14,5));
    //        tasks.add(new Task("Java Project",2020,Calendar.DECEMBER,20,10,3,3));
    //        tasks.add(new Task("Algo Problems",2021,Calendar.JANUARY,1,14,14,5));
    //        tasks.add(new Task("Algo Revision",2020,Calendar.DECEMBER,22,14,9,3));
    //        tasks.add(new Task("HASS Reading",2020,Calendar.DECEMBER,21,10,5,5));
    //        ConsolidatedAvailableDays cd = ConsolidatedAvailableDays.getInstance();
    //        AvailableDay[] availableDays = cd.getAvailableDays();
    //        for(AvailableDay a : availableDays){
    //            if(a==null) System.out.println("fds");
    //            else System.out.println(a.getNumberOfSlots());
    //        }
    //    }
    //
    //
    //
    //    //this should be wherever the user adds or edits a task and when task cannot be finished on time
    //    public static boolean checkHoursPerWeekEnough(ArrayList<Task> tasks, int numCurrentWeekAvailable){
    //        for(Task t:tasks){
    //            if(!t.checkEnoughTime(numCurrentWeekAvailable)){
    //                System.out.println(t.getName() + ": the number of hours a week is insufficient to complete the task");
    //                return false;
    //            }
    //        }
    //        return true;
    //    }
    //
    //    public static boolean checkEnoughTimeSlots(ArrayList<Task> tasks, HashMap<String, Integer> numSlotsPerWeek, ArrayList<TimeSlots> timeslots, int totalTimeSlotsNeeded){
    //        Calendar currentDay = Calendar.getInstance();
    //        String weekYear = currentDay.get(Calendar.WEEK_OF_YEAR)+","+currentDay.get(Calendar.YEAR);
    //        if(!checkHoursPerWeekEnough(tasks, numSlotsPerWeek.containsKey(weekYear) ? numSlotsPerWeek.get(weekYear) :0)) return false;
    //        return totalTimeSlotsNeeded<=timeslots.size();
    //    }
    //
    //    public static void addToExistingTimeTable(ArrayList<Task> taskToTry, ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
    //        //get whatever timeslot is left
    //        ArrayList<TimeSlots> availableTimeslots = new ArrayList<>();
    //        GetDatabaseTimeSlots.getAvailableTimeSlots(timeslots, availableTimeslots);
    //
    //        if(!callAllocation(taskToTry,availableTimeslots,numSlotsPerWeek)){
    //            //create a new list of everything so as not to overwrite anything
    //            ArrayList<Task> allTasksClone = new ArrayList<>();
    //            for (Task currentTask : tasks) {
    //                //String name, int dueYear, int dueMonth, int dueDay, double time, int hoursNeeded, int hoursPerWeek
    //                allTasksClone.add(new Task(currentTask.getName(), currentTask.getDueYear(), currentTask.getDueMonth(), currentTask.getDueDay(), currentTask.getTime(), currentTask.getHoursNeededLeft(), currentTask.getHoursPerWeek()));
    //            }
    //
    //            //remove any tasks that are already over
    //            for (Iterator<Task> itr = allTasksClone.iterator(); itr.hasNext();) {
    //                if (itr.next().getHoursNeeded()==0) {
    //                    itr.remove();
    //                }
    //            }
    //            ArrayList<TimeSlots> timeslotsNew = new ArrayList<>();
    //            HashMap<String, Integer> numSlotsPerWeekNew = new HashMap<>();
    //            GetDatabaseTimeSlots.addAvailableTimeSlots(timeslotsNew, numSlotsPerWeekNew);
    //            System.out.println("trying with all time slots");
    //            if(callAllocation(allTasksClone,timeslotsNew,numSlotsPerWeekNew)){
    //                //copy over
    //                tasks = allTasksClone;
    //                timeslots = timeslotsNew;
    //                numSlotsPerWeek = numSlotsPerWeekNew;
    //            }
    //        }
    //    }
    //
    //    //after the initial setting up, the user adds a new task
    //    public static void addNewTaskAfter(ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
    //        ArrayList<Task> addedTask = new ArrayList<>();
    //        addedTask.add(new Task("Hass Project",2020,Calendar.DECEMBER,20,12,22,6));
    //        tasks.add(new Task("Hass Project",2020,Calendar.DECEMBER,20,12,22,6));
    //        System.out.println("trying with available first");
    //        addToExistingTimeTable(addedTask, tasks, timeslots, numSlotsPerWeek);
    //    }
    //
    //    public static void taskNotCompleted(String notCompletedTaskName, int numSlotsMissed, ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
    //        ArrayList<Task> incompleteTask = new ArrayList<>();
    //        for(Task t :tasks){
    //            if(t.getName().equals(notCompletedTaskName)){
    //                t.remakeTimeSlots(numSlotsMissed);
    //                incompleteTask.add(t);
    //                break;
    //            }
    //        }
    //        if(incompleteTask.size()==0){
    //            System.out.println("cannot find task");
    //            return;
    //        }
    //        addToExistingTimeTable(incompleteTask, tasks, timeslots, numSlotsPerWeek);
    //    }
    //
    //    public static void changeAvailableDaysFixed(ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
    //        System.out.println("\n\nTesting changeAvailableDaysFixed");
    //        //pretend that monday is not free anymore
    //        ConsolidatedAvailableDays consolidatedAvailableDays = ConsolidatedAvailableDays.getInstance();
    //        consolidatedAvailableDays.setAvailableTime(Calendar.MONDAY,null);
    //        for(Task task:tasks){
    //            task.remakeTimeSlots();
    //        }
    //        //remove any tasks that are already over
    //        for (Iterator<Task> itr = tasks.iterator(); itr.hasNext();) {
    //            if (itr.next().getHoursNeeded()==0) {
    //                itr.remove();
    //            }
    //        }
    //
    //        //remake all the available time slots
    //        numSlotsPerWeek.clear();
    //        timeslots.clear();
    //        GetDatabaseTimeSlots.addAvailableTimeSlots(timeslots, numSlotsPerWeek);
    //        callAllocation(tasks,timeslots,numSlotsPerWeek);
    //    }
    //
    //    public static boolean callAllocation(ArrayList<Task> tasks,ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
    //        int totalTimeSlotsNeeded = 0;
    //        for(Task t:tasks){
    //            totalTimeSlotsNeeded +=t.getNumTaskSlotsNeeded();
    //        }
    //        if(!checkEnoughTimeSlots(tasks,numSlotsPerWeek,timeslots, totalTimeSlotsNeeded)) {
    //            System.out.println("Need more time slots!!!");
    //            return false;
    //        }
    //
    //        Collections.sort(tasks);
    //        for(Task t:tasks) System.out.println(t.getName());
    //        String outputOfAllocation = AllocateTimeSlots.AllocateTime(timeslots,tasks,totalTimeSlotsNeeded,totalTimeSlotsNeeded,0);
    //        if(outputOfAllocation.equals("1")) {
    //            for (Task t : tasks) {
    //                t.printTimeSlots();
    //            }
    //            return true;
    //        }
    //        else{
    //            //just for reference
    //            //if(!checkBeforeDeadLine()) return -1;
    //            //if(!checkMaxWeeklyHours()) return -2;
    //            //if(!checkAssigned()) return -3;
    //
    //            String[] outputSplit = outputOfAllocation.split(",");
    //            if ("-3".equals(outputSplit[1])) {
    //                System.out.println("Not Enough Time Slots for all Tasks");
    //            }
    //            return false;
    //        }
    //    }
    //
    //    public static void main(String[] args){
    //        //figure out which days of the week the user is free
    //        addAvailableDaysFixed();
    //        //NOTE: only creating time slots for 2 months first
    //        ArrayList<TimeSlots> timeslots = new ArrayList<>();
    //        HashMap<String, Integer> numSlotsPerWeek = new HashMap<>();
    //        GetDatabaseTimeSlots.addAvailableTimeSlots(timeslots, numSlotsPerWeek);
    //
    //        ArrayList<Task> tasks = new ArrayList<>();
    //        addTasksFixed(tasks);
    //
    //        callAllocation(tasks,timeslots,numSlotsPerWeek);
    //
    //
    //        //things the user might do next
    //        //uncomment to test
    //        //1 add a new task, try to fill up availableTime slots before redoing the assignment
    //        //addNewTaskAfter(tasks, timeslots, numSlotsPerWeek);
    //
    //        //2 say that a task is not done, try to fill up availableTime slots before redoing the assignment
    //        //taskNotCompleted("HASS Reading", 2, tasks, timeslots, numSlotsPerWeek);
    //
    //        //3 changed available days
    //        //changeAvailableDaysFixed(tasks, timeslots, numSlotsPerWeek);
    //
    //
    // }

}

