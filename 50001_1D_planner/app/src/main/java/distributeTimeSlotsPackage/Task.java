package distributeTimeSlotsPackage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Task implements Comparable<Task> {
    private String name;
    private int dueYear,dueMonth,dueDay;
    private double hoursNeeded, hoursPerWeek;
    private double time;
    private Calendar cal;
    public int numTaskSlotsNeeded;
    private ArrayList<TaskSlots> taskSlots = new ArrayList<>();

    public Task(String name, int dueYear, int dueMonth, int dueDay, double time, double hoursNeeded, double hoursPerWeek){
        this.name = name;

        cal = Calendar.getInstance();
        //set(int year, int month, int date, int hourOfDay, int minute)
        int hour = (int) time;
        int min = time-hour ==0? 0:30;
        cal.set(dueYear,dueMonth,dueDay, hour, min);
        cal.set(Calendar.HOUR_OF_DAY,hour);
        this.time = time;
        this.hoursNeeded = hoursNeeded;
        this.hoursPerWeek = hoursPerWeek;
        this.numTaskSlotsNeeded = (int)hoursNeeded * 2;
        addTaskSlots();
    }
    public void addTaskSlots() {
        for (int i = 0; i < numTaskSlotsNeeded; i++) {
            taskSlots.add(new TaskSlots(name,i));
        }
    }

    //this method of checking for time isnt good enough cause if today is fri and the due date is on monday
    //it still thinks theres a week
    public boolean checkEnoughTime(int numCurrentWeekAvailable){
        Calendar today = Calendar.getInstance();
        int numOfWeekBetween = (cal.get(Calendar.WEEK_OF_YEAR) - today.get(Calendar.WEEK_OF_YEAR) + 52)%52;
        //System.out.println(cal.get(Calendar.WEEK_OF_YEAR));
        //System.out.println(today.get(Calendar.WEEK_OF_YEAR));
        //System.out.println(numOfWeekBetween);
        return ((numOfWeekBetween)*hoursPerWeek + numCurrentWeekAvailable)>=hoursNeeded;
    }

    public int partialChecks(){
        //System.out.println("partial check" + name);
        if(!checkBeforeDeadLine()) return -1;
        if(!checkMaxWeeklyHours()) return -2;
        return 1;
    }

    public int completeChecks(){
        if(!checkBeforeDeadLine()) return -1;
        if(!checkMaxWeeklyHours()) return -2;
        if(!checkAssigned()) return -3;
        return 1;
    }

    public boolean checkAssigned(){
        for(TaskSlots ts:taskSlots){
            if (ts.getTimeSlots()==null)
                    return false;
        }
        return true;
    }

    private boolean checkBeforeDeadLine(){
        for(TaskSlots ts:taskSlots){
            if (ts.getTimeSlots() ==null) continue;
            if(ts.getTimeSlots().getCal().getTimeInMillis()-cal.getTimeInMillis()>=0){
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
        //System.out.println(weeklyHours.keySet());
        //System.out.println(weeklyHours.values());
        for(int numSlots: weeklyHours.values()){
            if(numSlots>hoursPerWeek*2) {
            //System.out.println("numSlots:"+numSlots);
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
        hoursNeeded = getHoursNeededLeft() + (double)numMissedSlots/2;
        numTaskSlotsNeeded = (int)hoursNeeded*2;
        for(TaskSlots ts: taskSlots){
            ts.getTimeSlots().setAssignedTaskSlot(null);
        }
        taskSlots.clear();
        for (int i = 0; i < numTaskSlotsNeeded; i++) {
            taskSlots.add(new TaskSlots(name,i));
        }
    }

    public int getNumTaskSlotsNeeded() {
        return numTaskSlotsNeeded;
    }

    public Calendar getCal() {
        return cal;
    }

    public String getName() {
        return name;
    }

    public int getDueDay() {
        return dueDay;
    }

    public int getDueMonth() {
        return dueMonth;
    }

    public int getDueYear() {
        return dueYear;
    }

    public double getHoursNeeded() {
        return hoursNeeded;
    }

    public double getHoursNeededLeft(){
        Calendar today = Calendar.getInstance();
        double hoursLeft = hoursNeeded;
        for(TaskSlots ts: taskSlots){
            if(ts!=null)
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
        if(cal.getTimeInMillis()<t.getCal().getTimeInMillis())
            return -1;
        else if(cal.getTimeInMillis()>t.getCal().getTimeInMillis())
            return 1;
        else return 0;
    }

    public void printTimeSlots(){
        System.out.println("Time Slots for " + name);
        for(TaskSlots ts: taskSlots){
            System.out.println(ts.getTaskTime());
        }
    }
}