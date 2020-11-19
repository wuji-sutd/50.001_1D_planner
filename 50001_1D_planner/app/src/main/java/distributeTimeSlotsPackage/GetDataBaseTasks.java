package distributeTimeSlotsPackage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

public class GetDataBaseTasks {

    //get string from database
    /*
     * Structure of Tasks kept in the database
     * Name of column = TASKNAME_DB
     * columns in table: String name, int dueYear, int dueMonth, int dueDay, double time, double hoursNeeded, double hoursPerWeek, String timeSlots
     * timeSlots column is structured like this
     * "int year, int month, int date, double time; int year, int month, int date, double time;" else "NULL"
     * */
    private static void getTasksFromDataBase(ArrayList<Task> tasks, ArrayList<TimeSlots> timeSlots){
        //TODO: get task info from Tasks table
        String listOfTaskDBCol = "IDK YET";
        String taskNames = "Task 1, Task 2, Task 3";
        Calendar c = Calendar.getInstance();
        for(String taskName:taskNames.split(",")){
            // TODO: get the corresponding Task info and change the values of these variables
            String name = taskName;
            int dueYear = 2020;
            int dueMonth = Calendar.DECEMBER;
            int dueDay = 30;
            double time = 13.5;
            double hoursNeeded = 12;
            double hoursPerWeek =4;
            String assignedTimeSlots = "NULL";
            Task current = new Task(name,dueYear,dueMonth,dueDay,time,hoursNeeded,hoursPerWeek);
            if(!assignedTimeSlots.equals("NULL")){
                String[] timeslotsDetails = assignedTimeSlots.split(";");
                for(int j = 0; j<timeslotsDetails.length;j++){
                    String[] details = timeslotsDetails[j].split(",");
                    int hour = (int) Double.parseDouble(details[3]);
                    int min = time-hour ==0? 0:30;
                    c.set(Integer.parseInt(details[0]),Integer.parseInt(details[1]),Integer.parseInt(details[2]),0,min,0);
                    c.set(Calendar.HOUR_OF_DAY,hour);
                    TimeSlots foundts = null;
                    for(TimeSlots ts:timeSlots){
                        if(ts.getCal().getTimeInMillis()==c.getTimeInMillis())
                            foundts = ts;
                    }
                    current.assignLatestTimeSlot(foundts);
                }
            }
            tasks.add(current);

        }
    }
}
