package distributeTimeSlotsPackage;

import java.util.ArrayList;
import java.util.HashMap;


public class AvailableDay {
    private HashMap<Double,Double> availableTimes;
    //private ArrayList<TimeSlots> timeSlots = new ArrayList<>();
    private int day;

    public AvailableDay(int day){
        this.day = day;
        this.availableTimes = new HashMap<>();
    }
    public AvailableDay(int day, HashMap<Double,Double> availableTimes){
        this.day = day;
        this.availableTimes = availableTimes;
    }

    public void addAvailableTime(double startTime, double endTime){
        availableTimes.put(startTime,endTime);
    }

    public void resetAvailableTimes(HashMap<Double,Double> newAvailableTimes){
        if(newAvailableTimes==null){
            availableTimes.clear();
            return;
        }
        availableTimes = newAvailableTimes;
    }
    //TODO: add a conflict check to make sure availableTime doesn't have overlaps
    public void checkConflict(){

    }

    public HashMap<Double,Double> getAvailableTimes() {
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
