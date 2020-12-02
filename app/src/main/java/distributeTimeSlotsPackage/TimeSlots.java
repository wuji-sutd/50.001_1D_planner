package distributeTimeSlotsPackage;


import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeSlots {
    public static double  duration = 0.5; //0.5h == 30min
    private double time;
    private Calendar cal;
    private TaskSlots assignedTaskSlot = null;
    private boolean isBreak;

    public TimeSlots(int year, int month, int date, double time){
        cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
        //set(int year, int month, int date, int hourOfDay, int minute)
        this.time = time;
        int hour = (int) time;
        int min = time-hour ==0? 0:30;
        cal.set(year,month,date,0,min,0);
        cal.set(Calendar.HOUR_OF_DAY,hour);
    }

    public TimeSlots(Calendar fixedCalendar){
        cal = fixedCalendar;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,"30 min Time Slot: %d %d %d at %d:%d",
                cal.get(Calendar.DAY_OF_MONTH),cal.get((Calendar.MONTH))+1, cal.get(Calendar.YEAR),
                cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
    }

    public Calendar getCal() {
        return cal;
    }

    public int getWeekofYear(){
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public int getDayofYear() {return cal.get(Calendar.DAY_OF_YEAR);}

    public TaskSlots getAssignedTaskSlot() {
        return assignedTaskSlot;
    }

    public void setAssignedTaskSlot(TaskSlots assignedTaskSlot) {
        this.assignedTaskSlot = assignedTaskSlot;
    }

    public double getTime() {
        return time;
    }

    public String getStartToEndTime(){
        String startTime = getStartTimeString(false);
        String endTime = getEndTimeString();
        return startTime + " - " + endTime;
    }

    public String getBreakStartToEndTime(){
        String startTime = getStartTimeString(true);
        String endTime = getEndTimeString();
        return startTime + " - " + endTime;
    }

    public String getStartTimeString(boolean hasBreak){
        double startMin = time-(int)time;
        String startTime;
        if(startMin==0) {
            if(hasBreak) startTime = String.format(Locale.ENGLISH,"%02d:15", (int)time);
            else startTime = String.format(Locale.ENGLISH,"%02d:00", (int)time);
        } else {
            if(hasBreak) startTime = String.format(Locale.ENGLISH,"%02d:45", (int)time);
            else startTime = String.format(Locale.ENGLISH,"%02d:30", (int)time);
        }
        return startTime;
    }

    public String getEndTimeString(){
        double startMin = time-(int)time;
        String endTime;
        if(startMin==0){
            endTime = String.format(Locale.ENGLISH,"%02d:30", (int)time);
        }
        else{
            endTime = String.format(Locale.ENGLISH,"%02d:00", (int)time+1);
        }
        return endTime;
    }

    public void setIsBreak(){
        isBreak = true;
    }

    public boolean getIsBreak(){
        return isBreak;
    }
}
