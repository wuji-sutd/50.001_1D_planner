package com.example.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "CalendarDatabase";

    // Database Name and Version
    public static final String DatabaseName = "Calendar.db";
    public static final int DatabaseVersion = 1;

    // Columns for the User Information Table
    public static final String UserTableName = "UserInfoTable";
    public static final String UserCol1_UserID = "UserID";
    public static final String UserCol2_Name = "Name";
    public static final String UserCol3_Email = "Email";
    public static final String UserCol4_Password = "Password";

    // Columns for the Task Information Table
    public static final String TaskTableName = "TaskInfoTable";
    public static final String TaskCol1_TaskID = "TaskID";
    public static final String TaskCol2_UserID = "TaskUserID"; // The ID of user who created the task
    public static final String TaskCol3_Title = "Title";
    public static final String TaskCol4_EstHours = "EstHours";
    public static final String TaskCol5_DueDate = "DueDate";
    public static final String TaskCol6_WeeklyR = "WeeklyRecurring";

    // SQL Statement for the User Information Table Creation
    private static final String SQL_CreateUserInfoTable = "CREATE TABLE "
            + UserTableName + "("
            + UserCol1_UserID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UserCol2_Name + " TEXT NOT NULL, "
            + UserCol3_Email + " TEXT NOT NULL, "
            + UserCol4_Password + " TEXT NOT NULL "
            + ");";

    // SQL Statement for the Task Information Table Creation
    private static final String SQL_CreateTaskInfoTable = "CREATE TABLE "
            + TaskTableName + "("
            + TaskCol1_TaskID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TaskCol2_UserID + " INTEGER NOT NULL, "
            + TaskCol3_Title + " TEXT NOT NULL, "
            + TaskCol4_EstHours + " TEXT NOT NULL, "
            + TaskCol5_DueDate + " TEXT NOT NULL, "
            + TaskCol6_WeeklyR + " TEXT NOT NULL "
            + ");";

    // Constructors
    public DBHelper(@Nullable Context context) {
        super(context, DatabaseName, null, DatabaseVersion);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DatabaseName, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CreateUserInfoTable);
        db.execSQL(SQL_CreateTaskInfoTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading the database from version " + oldVersion + " to " + newVersion);

        // Clear all data
        db.execSQL("DROP TABLE IF EXISTS " + UserTableName);
        db.execSQL("DROP TABLE IF EXISTS " + TaskTableName);

        // Recreate the tables
        onCreate(db);
    }

    /*// Insert New User Information
    public boolean insertUserData(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserCol2_Name, name);
        contentValues.put(UserCol3_Email, email);
        contentValues.put(UserCol4_Password, password);
        long result = db.insert(UserTableName, null, contentValues);
        return result != -1;
    }

    // Insert New Task Information
    public boolean insertTaskData(String name, String title, String estHours, String dueDate, String weeklyR) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskCol2_UserName, name);
        contentValues.put(TaskCol3_Title, title);
        contentValues.put(TaskCol4_EstHours, estHours);
        contentValues.put(TaskCol5_DueDate, dueDate);
        contentValues.put(TaskCol6_WeeklyR, weeklyR);
        long result = db.insert(TaskTableName, null, contentValues);
        return result != -1;
    }
     */

}
