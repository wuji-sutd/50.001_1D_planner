package distributeTimeSlotsPackage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

public class GetDatabaseTimeSlots {
    private static ConsolidatedAvailableDays consolidatedAvailableDays = ConsolidatedAvailableDays.getInstance();
    private static final int NUMBER_OF_MONTHS = 2;

    //get string from database
    /* Structure of days kept in the database
     * "MON,10.5-17.5+20.0-23.5,TUES,10.5-18.5+20.0-23.5,FRI,10.5-17.5"
     * */
    private static void getAvailableDaysFromDataBase(){
        //TODO: get string from database
        String dataBaseDaysTime = "MON,10.5-17.5;20.0-23.5,TUES,10.5-18.5;20.0-23.5,FRI,10.5-17.5";
        String[] daysTime = dataBaseDaysTime.split(",");
        ConsolidatedAvailableDays consolidatedAvailableDays = ConsolidatedAvailableDays.getInstance();

        for(int i = 0;i<daysTime.length;i+=2){
            String[] timeSlots = daysTime[i+1].split(";");
            TreeMap<Double,Double> currentTime = new TreeMap<>();
            for(int j = 0;j<timeSlots.length;j++){
                String[] startEnd = timeSlots[j].split("-");
                double start = Double.parseDouble(startEnd[0]);
                double end = Double.parseDouble(startEnd[1]);
                currentTime.put(start,end);
            }
            switch (daysTime[i]){
                case ("MON"):
                    consolidatedAvailableDays.setAvailableTime(Calendar.MONDAY,currentTime);
                    break;
                case("TUES"):
                    consolidatedAvailableDays.setAvailableTime(Calendar.TUESDAY,currentTime);
                    break;
                case("WED"):
                    consolidatedAvailableDays.setAvailableTime(Calendar.WEDNESDAY,currentTime);
                    break;
                case("THURS"):
                    consolidatedAvailableDays.setAvailableTime(Calendar.THURSDAY,currentTime);
                    break;
                case("FRI"):
                    consolidatedAvailableDays.setAvailableTime(Calendar.FRIDAY,currentTime);
                    break;
                case("SAT"):
                    consolidatedAvailableDays.setAvailableTime(Calendar.SATURDAY,currentTime);
                    break;
                case("SUN"):
                    consolidatedAvailableDays.setAvailableTime(Calendar.SUNDAY,currentTime);
                    break;
            }
        }
    }

    //because there are 2 arrayLists to return, just give the arrayList to return in advance
    public static void addAvailableTimeSlots(ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
        Calendar nMonthsFromNow = Calendar.getInstance();
        nMonthsFromNow.add(Calendar.MONTH,NUMBER_OF_MONTHS);
        Calendar currentDay = Calendar.getInstance();
        AvailableDay[] availableDays = consolidatedAvailableDays.getAvailableDays();

        //go from today to n months from now
        while(currentDay.get(Calendar.YEAR)!=nMonthsFromNow.get(Calendar.YEAR)
                || currentDay.get(Calendar.MONTH)!=nMonthsFromNow.get(Calendar.MONTH)
                || currentDay.get(Calendar.DAY_OF_MONTH)!=nMonthsFromNow.get(Calendar.DAY_OF_MONTH)){
            TreeMap<Double,Double> dayTimePeriod = availableDays[currentDay.get(Calendar.DAY_OF_WEEK)].getAvailableTimes();
            String weekYear = currentDay.get(Calendar.WEEK_OF_YEAR) +","+ currentDay.get(Calendar.YEAR);
            //for each day, check what time periods are available
            for(double key: dayTimePeriod.keySet()){
                //add timeslots for every 30min
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

    //get the remaining available timeslots
    public static void getAvailableTimeSlots(ArrayList<TimeSlots> timeslots, ArrayList<TimeSlots> availableTimeslots){
        for(TimeSlots ts: timeslots){
            if (ts.getAssignedTaskSlot()==null){
                availableTimeslots.add(ts);
            }
        }
    }
}
