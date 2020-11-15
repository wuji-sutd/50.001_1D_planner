package distributeTimeSlotsPackage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class TestTimeSlotDistributionLogic {
    private static final int testingNumMonths = 2;

    public static void addAvailableDaysFixed(){
        HashMap<Double,Double> mondayTimePeriod = new HashMap<>();
        mondayTimePeriod.put(10.0,12.0);
        mondayTimePeriod.put(14.5,17.0);
        HashMap<Double,Double> tuesdayTimePeriod = new HashMap<>();
        tuesdayTimePeriod.put(9.0,13.0);
        HashMap<Double,Double> wednesdayTimePeriod = new HashMap<>();
        wednesdayTimePeriod.put(8.0,9.0);
        wednesdayTimePeriod.put(17.5,20.0);
        HashMap<Double,Double> thursdayTimePeriod = new HashMap<>();
        thursdayTimePeriod.put(8.0,9.0);
        thursdayTimePeriod.put(17.5,20.0);
        HashMap<Double,Double> fridayTimePeriod = new HashMap<>();
        fridayTimePeriod.put(8.0,9.0);
        fridayTimePeriod.put(17.5,20.0);
        HashMap<Double,Double> sundayTimePeriod = new HashMap<>();
        sundayTimePeriod.put(10.0,12.0);
        sundayTimePeriod.put(14.0,18.0);

        ConsolidatedAvailableDays consolidatedAvailableDays = ConsolidatedAvailableDays.getInstance();
        consolidatedAvailableDays.setAvailableTime(Calendar.MONDAY,mondayTimePeriod);
        consolidatedAvailableDays.setAvailableTime(Calendar.TUESDAY,tuesdayTimePeriod);
        consolidatedAvailableDays.setAvailableTime(Calendar.WEDNESDAY,wednesdayTimePeriod);
        consolidatedAvailableDays.setAvailableTime(Calendar.THURSDAY,thursdayTimePeriod);
        consolidatedAvailableDays.setAvailableTime(Calendar.FRIDAY,fridayTimePeriod);
        consolidatedAvailableDays.setAvailableTime(Calendar.SUNDAY, sundayTimePeriod);
    }

    public static void addAvailableTimeSlots(ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek, int numberOfMonths){
        ConsolidatedAvailableDays consolidatedAvailableDays = ConsolidatedAvailableDays.getInstance();
        Calendar nMonthsFromNow = Calendar.getInstance();
        nMonthsFromNow.add(Calendar.MONTH,numberOfMonths);
        Calendar currentDay = Calendar.getInstance();
        AvailableDay[] availableDays = consolidatedAvailableDays.getAvailableDays();

        while(currentDay.get(Calendar.YEAR)!=nMonthsFromNow.get(Calendar.YEAR)
                || currentDay.get(Calendar.MONTH)!=nMonthsFromNow.get(Calendar.MONTH)
                || currentDay.get(Calendar.DAY_OF_MONTH)!=nMonthsFromNow.get(Calendar.DAY_OF_MONTH)){
            HashMap<Double,Double> dayTimePeriod = availableDays[currentDay.get(Calendar.DAY_OF_WEEK)].getAvailableTimes();
            String weekYear = currentDay.get(Calendar.WEEK_OF_YEAR) +","+ currentDay.get(Calendar.YEAR);
            for(double key: dayTimePeriod.keySet()){
                double current = key;
                double endTime = dayTimePeriod.get(key);
                while(current!=endTime){
                    //int year, int month, int date, double time
                    if(numSlotsPerWeek.containsKey(weekYear)){
                        numSlotsPerWeek.put(weekYear,numSlotsPerWeek.get(weekYear)+1);
                    } else {
                        numSlotsPerWeek.put(weekYear,1);
                    }
                    timeslots.add(new TimeSlots(currentDay.get(Calendar.YEAR), currentDay.get(Calendar.MONTH),currentDay.get(Calendar.DAY_OF_MONTH),current));
                    current +=0.5;
                }
            }
            currentDay.add(Calendar.DATE,1);
        }
    }

    public static void addTasksFixed(ArrayList<Task> tasks){
        //note: ArrayList are passed by reference
        //Task(String name, int dueYear, int dueMonth, int dueDay, double time, int hoursNeeded, int hoursPerWeek)
        tasks.add(new Task("Com Struct Problems",2020,Calendar.DECEMBER,30,13.5,14,2));
        tasks.add(new Task("Com Struct Project",2020,Calendar.DECEMBER,30,23.30,12,2));
        tasks.add(new Task("Java Project",2020,Calendar.NOVEMBER,20,10,3,3));
        tasks.add(new Task("Algo Problems",2021,Calendar.JANUARY,1,14,14,5));
        tasks.add(new Task("Algo Revision",2020,Calendar.DECEMBER,1,14,9,3));
        tasks.add(new Task("HASS Reading",2020,Calendar.NOVEMBER,21,10,10,20));
    }

    public static boolean checkHoursPerWeekEnough(ArrayList<Task> tasks, int numCurrentWeekAvailable){
        for(Task t:tasks){
            if(!t.checkEnoughTime(numCurrentWeekAvailable)){
                System.out.println(t.getName() + ": the number of hours a week is insufficient to complete the task");
                return false;
            }
        }
        return true;
    }

    public static boolean checkEnoughTimeSlots(ArrayList<Task> tasks, HashMap<String, Integer> numSlotsPerWeek, ArrayList<TimeSlots> timeslots, int totalTimeSlotsNeeded){
        Calendar currentDay = Calendar.getInstance();
        String weekYear = currentDay.get(Calendar.WEEK_OF_YEAR)+","+currentDay.get(Calendar.YEAR);
        if(!checkHoursPerWeekEnough(tasks, numSlotsPerWeek.containsKey(weekYear) ? numSlotsPerWeek.get(weekYear) :0)) return false;
        return totalTimeSlotsNeeded<=timeslots.size();
    }

    public static void getAvailableTimeSlots(ArrayList<TimeSlots> timeslots, ArrayList<TimeSlots> availableTimeslots){
        for(TimeSlots ts: timeslots){
            if (ts.getAssignedTaskSlot()==null){
                availableTimeslots.add(ts);
            }
        }
    }

    public static void addToExistingTimeTable(ArrayList<Task> taskToTry, ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
        //get whatever timeslot is left
        ArrayList<TimeSlots> availableTimeslots = new ArrayList<>();
        getAvailableTimeSlots(timeslots, availableTimeslots);

        if(!callAllocation(taskToTry,availableTimeslots,numSlotsPerWeek)){
            //create a new list of everything so as not to overwrite anything
            ArrayList<Task> allTasksClone = new ArrayList<>();
            for (Task currentTask : tasks) {
                //String name, int dueYear, int dueMonth, int dueDay, double time, int hoursNeeded, int hoursPerWeek
                allTasksClone.add(new Task(currentTask.getName(), currentTask.getDueYear(), currentTask.getDueMonth(), currentTask.getDueDay(), currentTask.getTime(), currentTask.getHoursNeededLeft(), currentTask.getHoursPerWeek()));
            }

            //remove any tasks that are already over
            for (Iterator<Task> itr = allTasksClone.iterator(); itr.hasNext();) {
                if (itr.next().getHoursNeeded()==0) {
                    itr.remove();
                }
            }
            ArrayList<TimeSlots> timeslotsNew = new ArrayList<>();
            HashMap<String, Integer> numSlotsPerWeekNew = new HashMap<>();
            addAvailableTimeSlots(timeslotsNew, numSlotsPerWeekNew, testingNumMonths);
            System.out.println("trying with all time slots");
            if(callAllocation(allTasksClone,timeslotsNew,numSlotsPerWeekNew)){
                //copy over
                tasks = allTasksClone;
                timeslots = timeslotsNew;
                numSlotsPerWeek = numSlotsPerWeekNew;
            }
        }
    }

    //after the initial setting up, the user adds a new task
    public static void addNewTaskAfter(ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
        ArrayList<Task> addedTask = new ArrayList<>();
        addedTask.add(new Task("Hass Project",2020,Calendar.DECEMBER,20,12,22,6));
        tasks.add(new Task("Hass Project",2020,Calendar.DECEMBER,20,12,22,6));
        System.out.println("trying with available first");
        addToExistingTimeTable(addedTask, tasks, timeslots, numSlotsPerWeek);
    }

    public static void taskNotCompleted(String notCompletedTaskName, int numSlotsMissed, ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
        ArrayList<Task> incompleteTask = new ArrayList<>();
        for(Task t :tasks){
            if(t.getName().equals(notCompletedTaskName)){
                t.remakeTimeSlots(numSlotsMissed);
                incompleteTask.add(t);
                break;
            }
        }
        if(incompleteTask.size()==0){
            System.out.println("cannot find task");
            return;
        }
        addToExistingTimeTable(incompleteTask, tasks, timeslots, numSlotsPerWeek);
    }

    public static void changeAvailableDaysFixed(ArrayList<Task> tasks, ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
        System.out.println("\n\nTesting changeAvailableDaysFixed");
        //pretend that monday is not free anymore
        ConsolidatedAvailableDays consolidatedAvailableDays = ConsolidatedAvailableDays.getInstance();
        consolidatedAvailableDays.setAvailableTime(Calendar.MONDAY,null);
        for(Task task:tasks){
            task.remakeTimeSlots();
        }
        //remove any tasks that are already over
        for (Iterator<Task> itr = tasks.iterator(); itr.hasNext();) {
            if (itr.next().getHoursNeeded()==0) {
                itr.remove();
            }
        }

        //remake all the available time slots
        numSlotsPerWeek.clear();
        timeslots.clear();
        addAvailableTimeSlots(timeslots, numSlotsPerWeek, testingNumMonths);
        callAllocation(tasks,timeslots,numSlotsPerWeek);
    }

    public static boolean callAllocation(ArrayList<Task> tasks,ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
        int totalTimeSlotsNeeded = 0;
        for(Task t:tasks){
            totalTimeSlotsNeeded +=t.getNumTaskSlotsNeeded();
        }
        if(!checkEnoughTimeSlots(tasks,numSlotsPerWeek,timeslots, totalTimeSlotsNeeded)) {
            System.out.println("Need more time slots!!!");
            return false;
        }

        Collections.sort(tasks);
        String outputOfAllocation = AllocateTimeSlots.AllocateTime(timeslots,tasks,totalTimeSlotsNeeded,totalTimeSlotsNeeded,0);
        if(outputOfAllocation.equals("1")) {
            for (Task t : tasks) {
                t.printTimeSlots();
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
            if ("-1".equals(outputSplit[1])) {
                System.out.println("Not Enough Time Slots for all Tasks");
            }
            return false;
        }
    }

    public static void main(String[] args){
        //figure out which days of the week the user is free
        addAvailableDaysFixed();
        //NOTE: only creating time slots for 2 months first
        ArrayList<TimeSlots> timeslots = new ArrayList<>();
        HashMap<String, Integer> numSlotsPerWeek = new HashMap<>();
        addAvailableTimeSlots(timeslots, numSlotsPerWeek,testingNumMonths);

        ArrayList<Task> tasks = new ArrayList<>();
        addTasksFixed(tasks);

        callAllocation(tasks,timeslots,numSlotsPerWeek);


        //things the user might do next
        //uncomment to test
        //1 add a new task, try to fill up availableTime slots before redoing the assignment
        //addNewTaskAfter(tasks, timeslots, numSlotsPerWeek);

        //2 say that a task is not done, try to fill up availableTime slots before redoing the assignment
        //taskNotCompleted("HASS Reading", 2, tasks, timeslots, numSlotsPerWeek);

        //3 changed available days
        changeAvailableDaysFixed(tasks, timeslots, numSlotsPerWeek);



    }

}

