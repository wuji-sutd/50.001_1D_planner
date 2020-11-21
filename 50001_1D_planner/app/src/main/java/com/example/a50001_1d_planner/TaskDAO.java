package com.example.a50001_1d_planner;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
            DBHelper.TaskCol6_WeeklyR
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

    public Task createTask(long userID, String title, String estHours, String dueDate, String weeklyR) {
        ContentValues values = new ContentValues();

        values.put(DBHelper.TaskCol2_UserID, userID);
        values.put(DBHelper.TaskCol3_Title, title);
        values.put(DBHelper.TaskCol4_EstHours, estHours);
        values.put(DBHelper.TaskCol5_DueDate, dueDate);
        values.put(DBHelper.TaskCol6_WeeklyR, weeklyR);

        long insertID = mDatabase.insert(DBHelper.TaskTableName, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TaskTableName, mAllColumns,
                DBHelper.TaskCol1_TaskID + " = " + insertID,
                null, null, null, null);
        cursor.moveToFirst();
        Task newTask = cursorToTask(cursor);
        cursor.close();
        return newTask;
    }

    public void deleteTask(Task task) {
        long id = task.getTaskID();
        System.out.println("The deleted task has the id: " + id);
        mDatabase.delete(DBHelper.TaskTableName, DBHelper.TaskCol1_TaskID + " = " + id, null);
    }

    public List<Task> getAllTasks() {
        List<Task> listOfTasks = new ArrayList<Task>();

        Cursor cursor = mDatabase.query(DBHelper.TaskTableName, mAllColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (! cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            listOfTasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return listOfTasks;
    }

    public List<Task> getTasksOfUser(long userID) {
        List<Task> listOfTasks = new ArrayList<Task>();

        Cursor cursor = mDatabase.query(DBHelper.TaskTableName, mAllColumns,
                DBHelper.TaskCol2_UserID + " = ?",
                new String[] {String.valueOf(userID)},
                null, null, null, null);

        cursor.moveToFirst();
        while (! cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            listOfTasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return listOfTasks;
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setTaskID(cursor.getLong(0));
        task.setUserID(cursor.getLong(1));
        task.setTitle(cursor.getString(2));
        task.setEstHours(cursor.getString(3));
        task.setDueDate(cursor.getString(4));
        task.setWeeklyR(cursor.getString(5));

        /*
        Might also need to find user's name by the user ID
        */

        return task;
    }

}
