package distributeTimeSlotsPackage;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;


public class AvailableDay {
    private final String TAG = "AvailableDay";

    private TreeMap<Double,Double> availableTimes;
    //private ArrayList<TimeSlots> timeSlots = new ArrayList<>();
    private int day;
    private long availableDayID = 0;
    private long userID = 0;
    private String breakHours;

    public AvailableDay(int day){
        this.day = day;
        this.availableTimes = new TreeMap<>();
    }

    public AvailableDay( long userID, long availableDayID, int day, TreeMap<Double,Double> availableTimes, String breakHours){
        this.availableDayID =availableDayID;
        this.userID = userID;
        this.day = day;
        this.availableTimes = availableTimes;
        this.breakHours = breakHours;
        checkConflict();
    }

    public void addAvailableTime(double startTime, double endTime){
        availableTimes.put(startTime,endTime);
        checkConflict();
    }

    public void resetAvailableTimes(TreeMap<Double,Double> newAvailableTimes){
        if(newAvailableTimes==null){
            availableTimes.clear();
            return;
        }
        availableTimes = newAvailableTimes;
        checkConflict();
    }
    //a conflict check to make sure availableTime doesn't have overlaps, if there is merge the timings
    public void checkConflict(){
        for(Iterator<Double> keySetsItr = availableTimes.keySet().iterator();keySetsItr.hasNext();){
            double firstKey = keySetsItr.next();
            while(keySetsItr.hasNext()){
                double secondKey = keySetsItr.next();
                if(secondKey>firstKey && secondKey<=availableTimes.get(firstKey)){
                    double secondEnd = availableTimes.get(secondKey);
                    availableTimes.put(firstKey,secondEnd);
                    keySetsItr.remove();
                }
                else break;
            }
        }
    }

    public TreeMap<Double,Double> getAvailableTimes() {
        return availableTimes;
    }

    public int getNumberOfSlots(){
        int count = 0;
        double current = 0;
        for (double key : availableTimes.keySet()) {
            current = key;
            while (current != availableTimes.get(key)) {
                count++;
                current += 0.5;
            }
        }
        return count;

    }

    public int getDay() {
        return day;
    }

    public long getAvailableDayID() {
        return availableDayID;
    }

    public long getUserID() {
        return userID;
    }

    public String getBreakHoursString() { return breakHours;}

    //break times should be formatted as "13.5,15.5" or ""
    public ArrayList<Double> getBreakHoursDouble() {
        ArrayList<Double> breakHourArrayList = new ArrayList<>();
        Log.d(TAG,breakHours);

        if(!breakHours.isEmpty()){
            String[] workingHoursStrings = breakHours.split(",");
            for(String workingHour:workingHoursStrings){
                Log.d(TAG,"breakhours not empty" + workingHour);
                breakHourArrayList.add(Double.parseDouble(workingHour));
            }
        }
        return breakHourArrayList;
    }
}
