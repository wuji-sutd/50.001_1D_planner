package com.example.a50001_1d_planner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import distributeTimeSlotsPackage.AvailableDay;

public class WorkingHours extends AppCompatActivity {
    private String TAG = "WorkingHoursActivity";
    private WorkingHoursDAO workingHoursDAO;
    /*
    private Button backToMenu;
    private Button saveWorkingHours;
    private EditText monHours;
    private EditText tuesHours;
    private EditText wedHours;
    private EditText thursHours;
    private EditText friHours;
    private EditText satHours;
    private EditText sunHours;
    */
    private TimePicker Mondaystart;
    private TimePicker Mondayend;
    private TimePicker Tuesdaystart;
    private TimePicker Tuesdayend;
    private TimePicker Wednesdaystart;
    private TimePicker Wednesdayend;
    private TimePicker Thursdaystart;
    private TimePicker Thursdayend;
    private TimePicker Fridaystart;
    private TimePicker Fridayend;
    private TimePicker Saturdaystart;
    private TimePicker Saturdayend;
    private TimePicker Sundaystart;
    private TimePicker Sundayend;

    //TODO: if the working hours exist in the DB, show it onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_hours);
        this.workingHoursDAO = new WorkingHoursDAO(this);
        final long userID = 0;

        this.Mondaystart = findViewById(R.id.Mondaystart);
        this.Mondaystart.setIs24HourView(true);
        this.Mondayend = findViewById(R.id.Mondayend);
        this.Mondayend.setIs24HourView(true);

        this.Tuesdaystart = findViewById(R.id.Tuesdaystart);
        this.Tuesdaystart.setIs24HourView(true);
        this.Tuesdayend = findViewById(R.id.Tuesdayend);
        this.Tuesdayend.setIs24HourView(true);

        this.Wednesdaystart = findViewById(R.id.Wednesdaystart);
        this.Wednesdaystart.setIs24HourView(true);
        this.Wednesdayend = findViewById(R.id.Wednesdayend);
        this.Wednesdayend.setIs24HourView(true);

        this.Thursdaystart = findViewById(R.id.Thursdaystart);
        this.Thursdaystart.setIs24HourView(true);
        this.Thursdayend = findViewById(R.id.Thursdayend);
        this.Thursdayend.setIs24HourView(true);

        this.Fridaystart = findViewById(R.id.Fridaystart);
        this.Fridaystart.setIs24HourView(true);
        this.Fridayend = findViewById(R.id.Fridayend);
        this.Fridayend.setIs24HourView(true);

        this.Saturdaystart = findViewById(R.id.Saturdaystart);
        this.Saturdaystart.setIs24HourView(true);
        this.Saturdayend = findViewById(R.id.Saturdayend);
        this.Saturdayend.setIs24HourView(true);

        this.Sundaystart = findViewById(R.id.Sundaystart);
        this.Sundaystart.setIs24HourView(true);
        this.Sundayend = findViewById(R.id.Sundayend);
        this.Sundayend.setIs24HourView(true);

        //get the existing working hours, this is assuming one time slot only
        ArrayList<AvailableDay> availableDayArrayList = workingHoursDAO.getAllAvailableDays();
        if(availableDayArrayList.size()>0){
            for(AvailableDay availableDay:availableDayArrayList){
                TreeMap<Double,Double> availableTimes =  availableDay.getAvailableTimes();
                if(availableTimes.size()>0){
                    for(double key: availableTimes.keySet()){
                        int hour = (int)key;
                        getCorrespondingDayEditText(availableDay.getDay(),true).setCurrentHour(hour);
                        getCorrespondingDayEditText(availableDay.getDay(),true).setCurrentMinute((key-hour)==0? 0:30);

                        double endTime = availableTimes.get(key);
                        hour = (int)endTime;
                        getCorrespondingDayEditText(availableDay.getDay(),false).setCurrentHour(hour);
                        getCorrespondingDayEditText(availableDay.getDay(),false).setCurrentMinute((endTime-hour)==0? 0:30);
                    }
                }
            }
        }

        Button backToMenu = findViewById(R.id.backToMenu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMenuIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(backToMenuIntent);
            }
        });

        Button saveWorkingHours = findViewById(R.id.saveWorkingHours);
        saveWorkingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workingHoursDAO.createWorkingHours(userID, Calendar.MONDAY, getFormattedWorkingHours(Mondaystart,Mondayend));
                workingHoursDAO.createWorkingHours(userID, Calendar.TUESDAY, getFormattedWorkingHours(Tuesdaystart,Tuesdayend));
                workingHoursDAO.createWorkingHours(userID, Calendar.WEDNESDAY, getFormattedWorkingHours(Wednesdaystart,Wednesdayend));
                workingHoursDAO.createWorkingHours(userID, Calendar.THURSDAY, getFormattedWorkingHours(Thursdaystart,Thursdayend));
                workingHoursDAO.createWorkingHours(userID, Calendar.FRIDAY, getFormattedWorkingHours(Fridaystart,Fridayend));
                workingHoursDAO.createWorkingHours(userID, Calendar.SATURDAY, getFormattedWorkingHours(Saturdaystart,Saturdayend));
                workingHoursDAO.createWorkingHours(userID, Calendar.SUNDAY, getFormattedWorkingHours(Sundaystart,Sundayend));

                Intent saveWorkingHoursIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(saveWorkingHoursIntent);
            }
        });

    }

    //NOTE: if allowing for more than one time slot: available times should be "13.5-15,17-18"
    //currently accepts only one input can make it an arrayList in the future
    public String getFormattedWorkingHours(TimePicker dayStart, TimePicker dayEnd){
        int startHour= dayStart.getCurrentHour();
        int startMin = dayStart.getCurrentMinute();
        int endHour= dayEnd.getCurrentHour();
        int endMin = dayEnd.getCurrentMinute();
        Log.d(TAG,String.format("%d:%d-%d:%d",startHour,startMin,endHour,endMin));
        String startMinString = startMin==0? "0":"5";
        String startTime = startHour+"."+startMinString;
        String endMinString = endMin==0? "0":"5";
        String endTime = endHour+"."+endMinString;
        return startTime + "-" + endTime;
    }

    public TimePicker getCorrespondingDayEditText(int day,boolean isStart){
        switch (day){
            case Calendar.MONDAY:
                if(isStart) return Mondaystart;
                else return Mondayend;
            case Calendar.TUESDAY:
                if(isStart) return Tuesdaystart;
                else return Tuesdayend;
            case Calendar.WEDNESDAY:
                if(isStart) return Wednesdaystart;
                else return Wednesdayend;
            case Calendar.THURSDAY:
                if(isStart) return Thursdaystart;
                else return Thursdayend;
            case Calendar.FRIDAY:
                if(isStart) return Fridaystart;
                else return Fridayend;
            case Calendar.SATURDAY:
                if(isStart) return Saturdaystart;
                else return Saturdayend;
            default:
                if(isStart) return Sundaystart;
                else return Saturdayend;
        }
    }

}


