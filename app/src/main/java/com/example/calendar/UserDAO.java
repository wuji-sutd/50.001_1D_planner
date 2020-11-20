package com.example.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

public class UserDAO {

    public static final String TAG = "UserDAO";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private Context mContext;
    private String[] mAllColumns = {
            DBHelper.UserCol1_UserID,
            DBHelper.UserCol2_Name,
            DBHelper.UserCol3_Email,
            DBHelper.UserCol4_Password
    };

    public UserDAO(Context context) {
        this.mContext = context;
        mDBHelper = new DBHelper(context);
        // Open the Database
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on Opening Database " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void open() throws SQLException {
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public void close() {
        mDBHelper.close();
    }

    public User createUser(String name, String email, String password) {
        ContentValues values = new ContentValues();

        values.put(DBHelper.UserCol2_Name, name);
        values.put(DBHelper.UserCol3_Email, email);
        values.put(DBHelper.UserCol4_Password, password);

        long insertID = mDatabase.insert(DBHelper.UserTableName, null, values);
        Cursor cursor = mDatabase.query(DBHelper.UserTableName, mAllColumns,
                DBHelper.UserCol1_UserID + " = " + insertID,
                null, null, null, null);
        cursor.moveToFirst();
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;

    }

    public void deleteUser(User user) {
        long ID = user.getUserID();

        // Delete all tasks this user created?
        /*
        TaskDAO taskDAO = new TaskDAO(mContext);
        List<Task> listOfTasks = taskDAO.getTasksOfUser(ID);
        if (listOfTasks != null && !listOfTasks.isEmpty()) {
            for (Task t: listOfTasks) {
                taskDAO.deleteTask(t);
            }
        }
        */

        System.out.println("User ID " + ID + " has been deleted");
        mDatabase.delete(DBHelper.UserTableName,
                DBHelper.UserCol1_UserID + " = " + ID, null);
    }

    public User getUserByID(long ID) {
        Cursor cursor = mDatabase.query(DBHelper.UserTableName, mAllColumns,
                DBHelper.UserCol1_UserID + " = ?",
                new String[] {String.valueOf(ID)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        assert cursor != null;
        return cursorToUser(cursor);
    }

    protected User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserID(cursor.getLong(0));
        user.setName(cursor.getString(1));
        user.setEmail(cursor.getString(2));
        user.setPassword(cursor.getString(3));
        return user;
    }


}
