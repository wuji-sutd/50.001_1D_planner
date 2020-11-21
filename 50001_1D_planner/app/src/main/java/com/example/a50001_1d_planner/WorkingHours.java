package com.example.a50001_1d_planner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import distributeTimeSlotsPackage.AvailableDay;

public class WorkingHours extends AppCompatActivity {
    private String TAG = "WorkingHoursActivity";
    private WorkingHoursDAO workingHoursDAO;

    private Button backToMenu;
    private Button saveWorkingHours;
    private EditText monHours;
    private EditText tuesHours;
    private EditText wedHours;
    private EditText thursHours;
    private EditText friHours;
    private EditText satHours;
    private EditText sunHours;

    //TODO: if the working hours exist in the DB, show it onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_hours);
        this.workingHoursDAO = new WorkingHoursDAO(this);
        final long userID = 0;
        monHours = findViewById(R.id.MondayWorkingHours);
        tuesHours = findViewById(R.id.TuesdayWorkingHours);
        wedHours = findViewById(R.id.WednesdayWorkingHours);
        thursHours = findViewById(R.id.ThursdayWorkingHours);
        friHours = findViewById(R.id.FridayWorkingHours);
        satHours = findViewById(R.id.SaturdayWorkingHours);
        sunHours = findViewById(R.id.SundayWorkingHours);

        //get the existing working hours, this is assuming one time slot only
        ArrayList<AvailableDay> availableDayArrayList = workingHoursDAO.getAllAvailableDays();
        if(availableDayArrayList.size()>0){
            for(AvailableDay availableDay:availableDayArrayList){
                TreeMap<Double,Double> availableTimes =  availableDay.getAvailableTimes();
                if(availableTimes.size()>0){
                    StringBuilder formattedText = new StringBuilder();
                    for(double key: availableTimes.keySet()){
                        int hour = (int)key;
                        formattedText.append(hour);
                        formattedText.append("/");
                        formattedText.append((key-hour)==0? "00": "30");
                        formattedText.append(" to ");
                        double endTime = availableTimes.get(key);
                        hour = (int)endTime;
                        formattedText.append(hour);
                        formattedText.append("/");
                        formattedText.append((key-hour)==0? "00": "30");
                    }
                    getCorrespondingDayEditText(availableDay.getDay()).setText(formattedText.toString());
                }
            }
        }

        backToMenu = findViewById(R.id.backToMenu);
        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMenuIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(backToMenuIntent);
            }
        });

        saveWorkingHours = findViewById(R.id.saveWorkingHours);
        saveWorkingHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workingHoursDAO.createWorkingHours(userID, Calendar.MONDAY, getFormattedWorkingHours(monHours.getText().toString()));
                workingHoursDAO.createWorkingHours(userID, Calendar.TUESDAY, getFormattedWorkingHours(tuesHours.getText().toString()));
                workingHoursDAO.createWorkingHours(userID, Calendar.WEDNESDAY, getFormattedWorkingHours(wedHours.getText().toString()));
                workingHoursDAO.createWorkingHours(userID, Calendar.THURSDAY, getFormattedWorkingHours(thursHours.getText().toString()));
                workingHoursDAO.createWorkingHours(userID, Calendar.FRIDAY, getFormattedWorkingHours(friHours.getText().toString()));
                workingHoursDAO.createWorkingHours(userID, Calendar.SATURDAY, getFormattedWorkingHours(satHours.getText().toString()));
                workingHoursDAO.createWorkingHours(userID, Calendar.SUNDAY, getFormattedWorkingHours(sunHours.getText().toString()));
                Intent saveWorkingHoursIntent = new Intent(getApplicationContext(), Menu.class);
                startActivity(saveWorkingHoursIntent);
            }
        });
    }

    //NOTE: if allowing for more than one time slot: available times should be "13.5-15,17-18"
    //currently accepts only one input can make it an arrayList in the future
    public String getFormattedWorkingHours(String rawHours){
        if(rawHours.isEmpty()) return "";
        String[] hours = rawHours.split("to");
        Log.d(TAG,rawHours);
        Log.d(TAG,hours[0]+","+hours[1]);
        String[] startTimeSplit = hours[0].trim().split("/");
        String startMin = startTimeSplit[1].equals("00")? "0":"5";
        String startTime = startTimeSplit[0]+"."+startMin;
        String[] endTimeSplit = hours[1].trim().split("/");
        String endMin = endTimeSplit[1].equals("00")? "0":"5";
        String endTime = endTimeSplit[0]+"."+endMin;
        return startTime + "-" + endTime;
    }

    public EditText getCorrespondingDayEditText(int day){
        switch (day){
            case Calendar.MONDAY:
                return monHours;
            case Calendar.TUESDAY:
                return tuesHours;
            case Calendar.WEDNESDAY:
                return wedHours;
            case Calendar.THURSDAY:
                return thursHours;
            case Calendar.FRIDAY:
                return friHours;
            case Calendar.SATURDAY:
                return satHours;
            default:
                return sunHours;
        }
    }
}