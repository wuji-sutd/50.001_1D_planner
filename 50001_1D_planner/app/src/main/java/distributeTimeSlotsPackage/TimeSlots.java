package distributeTimeSlotsPackage;


import java.util.Calendar;
import java.util.Locale;

public class TimeSlots {
    public static double  duration = 0.5; //0.5h == 30min
    private double time;
    private Calendar cal;
    private TaskSlots assignedTaskSlot = null;

    public TimeSlots(int year, int month, int date, double time){
        cal = Calendar.getInstance();
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

    public TaskSlots getAssignedTaskSlot() {
        return assignedTaskSlot;
    }

    public void setAssignedTaskSlot(TaskSlots assignedTaskSlot) {
        this.assignedTaskSlot = assignedTaskSlot;
    }

    public double getTime() {
        return time;
    }
}
