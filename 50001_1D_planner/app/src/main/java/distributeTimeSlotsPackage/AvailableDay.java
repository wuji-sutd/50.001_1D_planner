package distributeTimeSlotsPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;


public class AvailableDay {
    private TreeMap<Double,Double> availableTimes;
    //private ArrayList<TimeSlots> timeSlots = new ArrayList<>();
    private int day;

    public AvailableDay(int day){
        this.day = day;
        this.availableTimes = new TreeMap<>();
    }
    public AvailableDay(int day, TreeMap<Double,Double> availableTimes){
        this.day = day;
        this.availableTimes = availableTimes;
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
        for(double key: availableTimes.keySet()){
            current = key;
            while(current!=availableTimes.get(key)){
                count++;
                current+=0.5;
            }
        }
        return count;
    }

    public int getDay() {
        return day;
    }


}
