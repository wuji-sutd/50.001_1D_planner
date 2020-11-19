package distributeTimeSlotsPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class ConsolidatedAvailableDays {
    private static ConsolidatedAvailableDays instance = null;
    private AvailableDay[] availableDays = new AvailableDay[8];

    private ConsolidatedAvailableDays(){
        makeAvailableDays();
    }

    private void makeAvailableDays(){
        for(int i=1;i<8;i++){
            availableDays[i] = new AvailableDay(i);
        }
    }

    public static ConsolidatedAvailableDays getInstance(){
        if(instance==null) instance = new ConsolidatedAvailableDays();
        return instance;
    }
    public void setAvailableTime(int day, TreeMap<Double,Double> timePeriod){
        availableDays[day].resetAvailableTimes(timePeriod);
    }

    public void addAvailableTime(int day, double startTime, double endTime){
        availableDays[day].addAvailableTime(startTime,endTime);
    }

    public AvailableDay[] getAvailableDays() {
        return availableDays;
    }
    //get
}
