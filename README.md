# 50.001_1D_planner
Andriod Mobile App Development
Overview:


Currently there are 9 activities layouts, each of them corresponds to one java class which encodes themselves. For a layout-class pair, both of them have the same name. The log in page is named as 'Main' and 'activity_main'.

After the log in page, the first activity appeared is our time table, if you click the top right 'CHECKLIST' button you'll go to the checklist. These are the only two activities without titles in their headings, they are replaced by date.

There are also other classes which do not have their corresponding layouts:
User ------------ Class for user
Task ------------ Class for task
DBHelper -------- SQLite Database Helper
UserDAO --------- Class contains basic operations for the user table
TaskDAO --------- Class contains basic operations for the task table

These five classes relates to our database. Our database is called 'Calendar.db', it contains two tables: User Table and Task Table.

To visualize the database, in android studio, while the app is running on your phone or emulator, go to View > Tool Windows > Device File Explorer > data > data > com.example.calendar > databases > Calendar.db. Save it or export it to your laptop and open it with corresponding app, then you'll see what's inside. If you want to see it again after you modify the database, you need to redo the above process and download it again.




TODO:


UI part:

1. For headings in all activities, we should always set the activity title (a textview) at the center of the screen width. Currently it stays at the center of its own textview box, so if there's only one 'BACK' button at its left, the title will be pushed towards right.

2. (Optional) For now all activities are using Linear Layout, which does not look flexible since the screen is not fully used. An alternative layout can be used if it can arrange the content better.

3. In the two main activities 'Time Table' & 'Checklist', instead of an activity title (like 'Time Table' and 'Check List'), the date should be displayed at the center of the heading. It can be displayed in the form of textview, for now I am leaving it as button.

4. (Optional) When inputing time slots, we might want to have an actual calendar that can be used to select days instead of key in everything.

5. In 'Set Working Hours', the user should be able to select multiple time slots, this can be done in the form of spinner (a list you can drop down and select one of the options). For now the input only takes one entire time slot.

6. (Optional) We should think more options that can be included in the 'Settings' activity, for now it only has an 'Account' option. Possible choices can be background color, text size, day/night mode and so on. If no time to do these, then just compress 'Settings' and 'Account' into one thing.

7. (Optional) Inside 'Account', the user can choose to upload his/her picture and display it in the 'Account' page, or select one from our default images.

8. (Optional) Inside 'Account', we can also include the user's personal information, like age and birthday. If we managed to send notifications to the user, don't forget to tell him/her 'Happy Birthday'.

9. Hopefully our app can looks nicer.





Algo part:

1. All tasks should be sorted and displayed in both 'Time Table' and 'Checklist' (maybe in checklist the tasks don't need to be sorted), and scrollable. After one task is finished, the user can mark it as 'finished' either by modify it under 'Edit Tasks' mode, or using the checklist directly.

2. (Optional) To edit tasks, we can go Menu > Edit Tasks, or when we are in 'Time Table' activity, we can press and hold the screen and directly enter 'Edit Tasks' mode. In the 'Edit Task' mode, the user should be able to first select which tasks need to be edited, then select the operation (such as remove, mark as finished, or edit task itself).

2.1. A third options for me, is to remove the 'Checklist' mode and only use the time table mode. Since the only usage of the checklist is to mark one task as finished, if we can press and hold then be able to edit the entire timetable, then an additional checklist might not be meaningful.

3. (Optional) We might want to save the finished tasks for the user, so it means 'Mark a task as finished' and 'Delete a task' are two different things.

4. (Optional) Our app can send notifications to the user.

5. (Optional) When switching between accounts, the user can switch directly from 'Settings', for now the only way to switch is to log out first, and log in with another account.

6. (Optional) Maybe we want to limit the form of user's password, such as it cannot be pure numbers.
