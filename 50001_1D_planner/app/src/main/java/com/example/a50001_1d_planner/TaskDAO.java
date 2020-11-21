package com.example.a50001_1d_planner;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import distributeTimeSlotsPackage.TaskSlots;
import distributeTimeSlotsPackage.TimeSlots;

public class TaskDAO {

    public static final String TAG = "TaskDAO";

    private Context mContext;

    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private String[] mAllColumns = {
            DBHelper.TaskCol1_TaskID,
            DBHelper.TaskCol2_UserID, // ID of the user who created the task
            DBHelper.TaskCol3_Title,
            DBHelper.TaskCol4_EstHours,
            DBHelper.TaskCol5_DueDate,
            DBHelper.TaskCol6_StartDate,
            DBHelper.TaskCol7_AssignedTimeSlots
    };

    public TaskDAO(Context context) {
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

    public void createTask(long userID, String title, String estHours, String startDate, String dueDate) {
        ContentValues values = new ContentValues();

        values.put(DBHelper.TaskCol2_UserID, userID);
        values.put(DBHelper.TaskCol3_Title, title);
        values.put(DBHelper.TaskCol4_EstHours, estHours);
        values.put(DBHelper.TaskCol5_DueDate, dueDate);
        values.put(DBHelper.TaskCol6_StartDate, startDate);
        values.put(DBHelper.TaskCol7_AssignedTimeSlots, "");

        mDatabase.insert(DBHelper.TaskTableName, null, values);
        Log.d(TAG,"created col for task");
//        Cursor cursor = mDatabase.query(DBHelper.TaskTableName, mAllColumns,
//                DBHelper.TaskCol1_TaskID + " = " + insertID,
//                null, null, null, null);
        //cursor.moveToFirst();
        //Task newTask = cursorToTask(cursor);
        //cursor.close();
        //return newTask;
    }

    public void deleteTask(Task task) {
        long id = task.getTaskID();
        Log.d(TAG,"The deleted task has the id: " + id);
        mDatabase.delete(DBHelper.TaskTableName, DBHelper.TaskCol1_TaskID + " = " + id, null);
    }

    public void updateTaskTimeSlot(Task task){
        long id = task.getTaskID();

        ContentValues values = new ContentValues();
        values.put(DBHelper.TaskCol1_TaskID, id);
        values.put(DBHelper.TaskCol2_UserID, task.getUserID());
        values.put(DBHelper.TaskCol3_Title, task.getTitle());
        values.put(DBHelper.TaskCol4_EstHours, String.valueOf(task.getEstHours()));
        values.put(DBHelper.TaskCol5_DueDate, task.getDueDate());
        values.put(DBHelper.TaskCol6_StartDate, task.getStartDate());
        values.put(DBHelper.TaskCol7_AssignedTimeSlots, task.getFormatTimeSlotsDB(TAG));
        mDatabase.update(DBHelper.TaskTableName, values,DBHelper.TaskCol1_TaskID + " = " + id, null);
        Log.d(TAG,"updated TimeSlots for " +task.getTitle());
    }

    public ArrayList<Task> getAllTasks(ArrayList<TimeSlots> timeSlots) {
        ArrayList<Task> listOfTasks = new ArrayList<Task>();

        Cursor cursor = mDatabase.query(DBHelper.TaskTableName, mAllColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (! cursor.isAfterLast()) {
            Task task = cursorToTask(cursor, timeSlots);
            listOfTasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return listOfTasks;
    }

    public List<Task> getTasksOfUser(long userID, ArrayList<TimeSlots> timeSlots) {
        List<Task> listOfTasks = new ArrayList<Task>();

        Cursor cursor = mDatabase.query(DBHelper.TaskTableName, mAllColumns,
                DBHelper.TaskCol2_UserID + " = ?",
                new String[] {String.valueOf(userID)},
                null, null, null, null);

        cursor.moveToFirst();
        while (! cursor.isAfterLast()) {
            Task task = cursorToTask(cursor, timeSlots);
            listOfTasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return listOfTasks;
    }



    //assignedTimeSlots column is structured like this
    //"int year, int month, int date, double time; int year, int month, int date, double time;" else ""
    private Task cursorToTask(Cursor cursor, ArrayList<TimeSlots> timeSlots) {
        long taskID = cursor.getLong(0);
        long userID = cursor.getLong(1);
        String title= cursor.getString(2);
        String estHours= cursor.getString(3);
        String dueDate= cursor.getString(4);
        String startDate = cursor.getString(5);
        String assignedTimeSlots = cursor.getString(6);

        Task task = new Task(userID, taskID, title, estHours, dueDate, startDate);

        //checking if there are already assigned time slots
        Calendar c = Calendar.getInstance();
        if(!assignedTimeSlots.isEmpty()){
            String[] timeslotsDetails = assignedTimeSlots.split(";");
            for(int j = 0; j<timeslotsDetails.length;j++){
                String[] details = timeslotsDetails[j].split(",");
                int hour = (int) Double.parseDouble(details[3]);
                int min = Double.parseDouble(details[3])-hour ==0? 0:30;
                c.set(Integer.parseInt(details[0]),Integer.parseInt(details[1]),Integer.parseInt(details[2]),0,min,0);
                c.set(Calendar.HOUR_OF_DAY,hour);
                TimeSlots foundts = null;
                for(TimeSlots ts:timeSlots){
                    if(ts.getCal().getTimeInMillis()==c.getTimeInMillis())
                        foundts = ts;
                }
                task.assignLatestTimeSlot(foundts);
            }
        }
        /*
        Might also need to find user's name by the user ID
        */
        return task;
    }

}
