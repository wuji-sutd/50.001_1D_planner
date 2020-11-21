package com.example.a50001_1d_planner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import distributeTimeSlotsPackage.AvailableDay;
import distributeTimeSlotsPackage.TimeSlots;

public class WorkingHoursDAO {
    private static final int NUMBER_OF_MONTHS = 2;
    public static final String TAG = "WorkingHoursDAO";

    private Context mContext;

    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private String[] mAllColumns = {
            DBHelper.WHCol1_WdID,
            DBHelper.WHCol2_UserID, // ID of the user who created the task
            DBHelper.WHCol3_WDDayOfWeek,
            DBHelper.WHCol4_AvailableHours,
            DBHelper.WHCol5_HasChanged,
    };

    public WorkingHoursDAO(Context context) {
        mDBHelper = new DBHelper((context));
        this.mContext = context;
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on opening database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException{
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public void close() {
        mDBHelper.close();
    }

    public void createWorkingHours(long userID, int dayOfWeek, String availableHours) {
        long count = DatabaseUtils.queryNumEntries(mDatabase, DBHelper.WorkingHoursTableName);
        if(count<7){
            for(int i = Calendar.SUNDAY; i<=Calendar.SATURDAY; i++) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.WHCol2_UserID, userID);
                values.put(DBHelper.WHCol3_WDDayOfWeek, i);
                if(i==dayOfWeek){
                    values.put(DBHelper.WHCol4_AvailableHours, availableHours);
                } else {
                    values.put(DBHelper.WHCol4_AvailableHours, "");
                }
                values.put(DBHelper.WHCol5_HasChanged,1);
                mDatabase.insert(DBHelper.WorkingHoursTableName, null, values);
            }
        }
        ContentValues values = new ContentValues();
        values.put(DBHelper.WHCol2_UserID, userID);
        values.put(DBHelper.WHCol3_WDDayOfWeek, dayOfWeek);
        values.put(DBHelper.WHCol4_AvailableHours, availableHours);
        values.put(DBHelper.WHCol5_HasChanged, 1);
        Cursor cursor = mDatabase.query(DBHelper.WorkingHoursTableName, mAllColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        for(int i =0; i<dayOfWeek-1;i++){
            cursor.moveToNext();
        }

        long WH_ID = cursor.getLong(0);
        values.put(DBHelper.WHCol1_WdID,WH_ID);
        mDatabase.update(DBHelper.WorkingHoursTableName,values,DBHelper.WHCol1_WdID + "=" + WH_ID,null);
        cursor.close();
    }

    public void removeHasChangedAvailableDays(AvailableDay availableDay){
        ContentValues values = new ContentValues();
        values.put(DBHelper.WHCol1_WdID, availableDay.getAvailableDayID());
        values.put(DBHelper.WHCol2_UserID, availableDay.getUserID());
        values.put(DBHelper.WHCol3_WDDayOfWeek, availableDay.getDay());
        values.put(DBHelper.WHCol4_AvailableHours, formatTimePeriodsToString(availableDay.getAvailableTimes()));
        values.put(DBHelper.WHCol5_HasChanged, 0);
        mDatabase.update(DBHelper.WorkingHoursTableName,values,DBHelper.WHCol1_WdID + "=" + availableDay.getAvailableDayID(),null);
    }

    public ArrayList<AvailableDay> getAllAvailableDays() {
        ArrayList<AvailableDay> listOfAvailableDays = new ArrayList<>();

        Cursor cursor = mDatabase.query(DBHelper.WorkingHoursTableName, mAllColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (! cursor.isAfterLast()) {
            AvailableDay availableDay = cursorToAvailableDay(cursor);
            listOfAvailableDays.add(availableDay);
            cursor.moveToNext();
        }
        cursor.close();
        return listOfAvailableDays;
    }

    //available times should be "13.5-15,17-18"
    private AvailableDay cursorToAvailableDay(Cursor cursor) {
        long WH_ID = cursor.getLong(0);
        long userID = cursor.getLong(1);
        int dayOfWeek= cursor.getInt(2);
        String availableHours= cursor.getString(3);
        TreeMap<Double, Double> availableTimes = new TreeMap<>();
        if(!availableHours.equals("")) {
            String[] availHoursSplit = availableHours.split(",");
            for (String s : availHoursSplit) {
                String[] startEnd = s.split("-");
                availableTimes.put(Double.parseDouble(startEnd[0]), Double.parseDouble(startEnd[1]));
            }
        }
        return new AvailableDay(userID, WH_ID, dayOfWeek, availableTimes);
    }

    //because there are 2 arrayLists to return, just give the arrayList to return in advance
    public void getAllAvailableTimeSlots(ArrayList<TimeSlots> timeslots, HashMap<String, Integer> numSlotsPerWeek){
        Calendar nMonthsFromNow = Calendar.getInstance();
        nMonthsFromNow.add(Calendar.MONTH,NUMBER_OF_MONTHS);
        Calendar currentDay = Calendar.getInstance();
        ArrayList<AvailableDay> availableDays = getAllAvailableDays();
        if(availableDays.size()==0) return;
        //go from today to n months from now
        while(currentDay.get(Calendar.YEAR)!=nMonthsFromNow.get(Calendar.YEAR)
                || currentDay.get(Calendar.MONTH)!=nMonthsFromNow.get(Calendar.MONTH)
                || currentDay.get(Calendar.DAY_OF_MONTH)!=nMonthsFromNow.get(Calendar.DAY_OF_MONTH)){
            TreeMap<Double,Double> dayTimePeriod = availableDays.get(currentDay.get(Calendar.DAY_OF_WEEK)-1).getAvailableTimes();
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
    public void getAvailableTimeSlots(ArrayList<TimeSlots> timeslots, ArrayList<TimeSlots> availableTimeslots){
        for(TimeSlots ts: timeslots){
            if (ts.getAssignedTaskSlot()==null){
                availableTimeslots.add(ts);
            }
        }
    }

    //NOTE: if allowing for more than one time slot: available times should be "13.5-15,17-18"
    //currently accepts only one input can make it an arrayList in the future
    public String formatTimePeriodsToString(TreeMap<Double,Double> timePeriods){
        StringBuilder formattedString = new StringBuilder();
        for(double startTime:timePeriods.keySet()){
            formattedString.append(startTime);
            formattedString.append("-");
            formattedString.append(timePeriods.get(startTime));
        }
        return formattedString.toString();
    }

    public ArrayList<AvailableDay> getRecentlyUpdatedAvailableDays(){
        ArrayList<AvailableDay> recentlyUpdatedAvailableDays = new ArrayList<>();

        Cursor cursor = mDatabase.query(DBHelper.WorkingHoursTableName, mAllColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (! cursor.isAfterLast()) {
            if(cursor.getInt(4)==1) {
                AvailableDay availableDay = cursorToAvailableDay(cursor);
                recentlyUpdatedAvailableDays.add(availableDay);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return recentlyUpdatedAvailableDays;
    }
}
