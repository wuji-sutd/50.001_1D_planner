package com.example.a50001_1d_planner;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import distributeTimeSlotsPackage.AvailableDay;
import distributeTimeSlotsPackage.ConsolidatedAvailableDays;
import distributeTimeSlotsPackage.TaskSlots;
import distributeTimeSlotsPackage.TimeSlots;

public class Task implements Comparable<Task> {
    private String TAG = "TaskClass";
    private long taskID;
    private long userID; // Declaring task using user ID in case if two persons have the same name
    private String title;
    private double estHours;
    private String dueDate;
    private String startDate;
    private String weeklyR;
    private double hoursPerWeek;
    private double time;
    private Calendar dueDateCal;
    private Calendar startDateCal;
    public int numTaskSlotsNeeded;
    public boolean tightSchedule = false;
    private ArrayList<TaskSlots> taskSlots = new ArrayList<>();

    public Task(long userID, long taskID, String title, String estHours, String startDate, String dueDate){
        assignFromConstructor(userID, taskID, title, estHours, startDate, dueDate, 23.30);
    }

    public Task(long userID, long taskID, String title, String estHours, String startDate, String dueDate, double time){
        assignFromConstructor(userID, taskID, title, estHours, startDate, dueDate, time);
    }

    private void assignFromConstructor(long userID, long taskID, String title, String estHours, String startDate, String dueDate, double time){
        this.userID = userID;
        this.title = title;
        this.taskID = taskID;
        dueDateCal = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        this.dueDate = dueDate;
        String[] dueDateComponents = dueDate.split("/"); //DD/MM/YYYY
        int hour = (int) time;
        int min = time-hour ==0? 0:30;
        //set(int year, int month, int date, int hourOfDay, int minute)
        dueDateCal.set(Integer.parseInt(dueDateComponents[2]),Integer.parseInt(dueDateComponents[1]),Integer.parseInt(dueDateComponents[0]), hour, min);
        dueDateCal.set(Calendar.HOUR_OF_DAY,hour);

        startDateCal = Calendar.getInstance();
        this.startDate = startDate;
        String[] startDateComponents = startDate.split("/"); //DD/MM/YYYY
        hour = (int) time;
        min = time-hour ==0? 0:30;
        //set(int year, int month, int date, int hourOfDay, int minute)
        startDateCal.set(Integer.parseInt(startDateComponents[2]),Integer.parseInt(startDateComponents[1]),Integer.parseInt(startDateComponents[0]), hour, min);
        startDateCal.set(Calendar.HOUR_OF_DAY,hour);

        this.time = time;
        this.estHours = Double.parseDouble(estHours);
        int numOfWeekBetween = (int)((dueDateCal.get(Calendar.DAY_OF_YEAR) - startDateCal.get(Calendar.DAY_OF_YEAR) + 365)%365 + 1)/7;
        this.hoursPerWeek = Math.ceil(this.estHours/numOfWeekBetween);
        this.hoursPerWeek = hoursPerWeek<1? 1:hoursPerWeek;
        this.numTaskSlotsNeeded = (int)this.estHours * 2;
        addTaskSlots();
    }

    public void addTaskSlots() {
        for (int i = 0; i < numTaskSlotsNeeded; i++) {
            taskSlots.add(new TaskSlots(title,i));
        }
    }

    public boolean checkEnoughTime(ArrayList<AvailableDay> availableDays){
        if(availableDays.size()==0) return false;
        //Calendar today = Calendar.getInstance();
        int timeFound = 0;
        int tempTimeFound = 0;
        int numOfWeekBetween = (dueDateCal.get(Calendar.WEEK_OF_YEAR) - startDateCal.get(Calendar.WEEK_OF_YEAR) + 52)%52;

        //get number of slots for week 0
        for(int i =startDateCal.get(Calendar.DAY_OF_WEEK);i!=Calendar.SATURDAY+2;i++){
            if(i == 8) {
                tempTimeFound+= availableDays.get(Calendar.SUNDAY-1).getNumberOfSlots()/2;
            } else {
                tempTimeFound+= availableDays.get(i-1).getNumberOfSlots()/2;
            }
        }
        //System.out.println(tempTimeFound);
        timeFound +=tempTimeFound<hoursPerWeek? tempTimeFound:hoursPerWeek;
        tempTimeFound = 0;
        //get number of slots for last week
        for(int i =dueDateCal.get(Calendar.DAY_OF_WEEK);i!=Calendar.SUNDAY;i--){
            tempTimeFound+= availableDays.get(i-1).getNumberOfSlots()/2;
        }
        //System.out.println(tempTimeFound);
        timeFound +=tempTimeFound<hoursPerWeek? tempTimeFound:hoursPerWeek;
        //rest of the weeks
        timeFound+=(numOfWeekBetween-1)>0? (numOfWeekBetween-1)*hoursPerWeek :0;
        Log.d(TAG,title);
        Log.d(TAG,String.valueOf(dueDateCal.get(Calendar.WEEK_OF_YEAR)));
        Log.d(TAG,String.valueOf(startDateCal.get(Calendar.WEEK_OF_YEAR)));
        Log.d(TAG,String.valueOf(numOfWeekBetween));
        Log.d(TAG,String.valueOf(timeFound));
        Log.d(TAG,String.valueOf(timeFound>=estHours));
        if(timeFound==estHours) {
            tightSchedule =true;
            Log.d(TAG,"tight " + title);
        }
        return timeFound>=estHours;
    }

    public int partialChecks(){
        //System.out.println("partial check" + name);
        if(!checkBeforeDeadLine()) return -1;
        if(!checkMaxWeeklyHours()) return -2;
        if(!checkAfterBeginTime()) return -4;
        return 1;
    }

    public int completeChecks(){
        if(!checkBeforeDeadLine()) return -1;
        if(!checkMaxWeeklyHours()) return -2;
        if(!checkAssigned()) return -3;
        if(!checkAfterBeginTime()) return -4;
        return 1;
    }

    public boolean checkAssigned(){
        for(TaskSlots ts:taskSlots){
            if (ts.getTimeSlots()==null)
                return false;
        }
        return true;
    }
    private boolean checkAfterBeginTime(){
        for(TaskSlots ts:taskSlots){
            if (ts.getTimeSlots() ==null) continue;
            if(ts.getTimeSlots().getCal().getTimeInMillis()-startDateCal.getTimeInMillis()<0){
                return false;
            }
        }
        return true;
    }

    private boolean checkBeforeDeadLine(){
        for(TaskSlots ts:taskSlots){
            if (ts.getTimeSlots() ==null) continue;
            if(ts.getTimeSlots().getCal().getTimeInMillis()-dueDateCal.getTimeInMillis()>=0){
                return false;
            }
        }
        return true;
    }

    private boolean checkMaxWeeklyHours(){
        HashMap<String,Integer> weeklyHours = new HashMap<>();
        //System.out.println(taskSlots.size());
        for(TaskSlots ts:taskSlots){
            if (ts.getTimeSlots() ==null) continue;
            Calendar this_c = ts.getTimeSlots().getCal();
            int weekOfYear = this_c.get(Calendar.WEEK_OF_YEAR);
            int year = this_c.get(Calendar.YEAR);
            String WeekYear = weekOfYear +","+ year;
            if(!weeklyHours.containsKey(WeekYear)){
                weeklyHours.put(WeekYear,1);
            }
            else{
                weeklyHours.put(WeekYear,weeklyHours.get(WeekYear)+1);
            }
        }
        for(int numSlots: weeklyHours.values()){
            if(numSlots>hoursPerWeek*2) {
                Log.d(TAG,"numSlots:"+numSlots);
                Log.d(TAG,"hours per week:"+hoursPerWeek);
                return false;
            }
        }
        return true;
    }
    public void assignLatestTimeSlot(TimeSlots timeSlot){
        for(TaskSlots ts: taskSlots){
            if(ts.getTimeSlots() == null){
                ts.setTimeSlots(timeSlot);
                timeSlot.setAssignedTaskSlot(ts);
                return;
            }
        }
    }

    public void removeLatestTimeSlot(){
        for(int i = taskSlots.size()-1;i>=0;i--){
            if(taskSlots.get(i).getTimeSlots()!=null){
                taskSlots.get(i).getTimeSlots().setAssignedTaskSlot(null);
                taskSlots.get(i).setTimeSlots(null);
                return;
            }
        }
    }

    public void remakeTimeSlots(){
        remakeTimeSlots(0);
    }

    public void remakeTimeSlots(int numMissedSlots){
        estHours = getHoursNeededLeft() + (double)numMissedSlots/2;
        Calendar today = Calendar.getInstance();
        numTaskSlotsNeeded = (int)estHours*2;
        for(TaskSlots ts: taskSlots){
            if(ts.getTimeSlots()!=null)
                ts.getTimeSlots().setAssignedTaskSlot(null);
        }
        taskSlots.clear();
        for (int i = 0; i < numTaskSlotsNeeded; i++) {
            taskSlots.add(new TaskSlots(title,i));
        }
        if(today.getTimeInMillis()>startDateCal.getTimeInMillis())
            startDateCal = today;
    }

    //public void setTaskID(long taskID) { this.taskID = taskID; }
    public long getTaskID() { return taskID; }

    //public void setUserID(long userID) { this.userID = userID; }
    public long getUserID() { return userID; }

    //public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    //public void setEstHours(double estHours) { this.estHours = estHours; }
    public double getEstHours() { return estHours; }

    public String getStartDate() { return startDate; }

    //public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getDueDate() { return dueDate; }

    //public void setWeeklyR(String weeklyR) { this.weeklyR = weeklyR; }
    public String getWeeklyR() { return weeklyR; }

    public int getNumTaskSlotsNeeded() {
        return numTaskSlotsNeeded;
    }

    public Calendar getCal() {
        return dueDateCal;
    }

    public Calendar getStartDateCal() { return startDateCal; }

    public double getHoursNeededLeft(){
        Calendar today = Calendar.getInstance();
        double hoursLeft = estHours;
        for(TaskSlots ts: taskSlots){
            if(ts.getTimeSlots()!=null)
                if(ts.getTimeSlots().getCal().getTimeInMillis()<today.getTimeInMillis()){
                    hoursLeft-=0.5;
                }
        }
        return hoursLeft;
    }

    public double getHoursPerWeek() {
        return hoursPerWeek;
    }

    public double getTime() {
        return time;
    }

    @Override
    public int compareTo(Task t) {
        if(tightSchedule && t.tightSchedule || !tightSchedule&&!t.tightSchedule ) {
            if (dueDateCal.getTimeInMillis() < t.getCal().getTimeInMillis())
                return -1;
            else if (dueDateCal.getTimeInMillis() > t.getCal().getTimeInMillis())
                return 1;
            else return 0;
        }
        else if(tightSchedule) return -1;
        else return 1;

    }

    public void printTimeSlots(String TAG){
        Log.d(TAG,"Time Slots for " + title);
        for(TaskSlots ts: taskSlots){
            Log.d(TAG,ts.getTaskTime());
        }
    }

    //assignedTimeSlots column is structured like this
    //"int year, int month, int date, double time; int year, int month, int date, double time;" else ""

    public String getFormatTimeSlotsDB(String TAG){
        StringBuilder formatedTimeSlots = new StringBuilder();
        for(TaskSlots ts:taskSlots){
            Calendar timeSlotCal = ts.getTimeSlots().getCal();
            formatedTimeSlots.append(timeSlotCal.get(Calendar.YEAR));
            formatedTimeSlots.append(",");
            formatedTimeSlots.append(timeSlotCal.get(Calendar.MONTH));
            formatedTimeSlots.append(",");
            formatedTimeSlots.append(timeSlotCal.get(Calendar.DAY_OF_MONTH));
            formatedTimeSlots.append(",");
            formatedTimeSlots.append(ts.getTimeSlots().getTime());
            formatedTimeSlots.append(";");
        }
        String output = formatedTimeSlots.toString();
        Log.d(TAG,output);
        return output;
    }

    public ArrayList<String> getArrayListOfTimeSlots(){
        ArrayList<String> out = new ArrayList<>();
        for(TaskSlots ts:taskSlots){
            out.add(title+" : "+ ts.getTimeSlots().toString());
        }
        return out;
    }

}