package com.mogli.notificationlog2;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.TimeZone;

public class ReminderActivity extends AppCompatActivity {

    private Uri notifUri;
    private TextView title;
    private Button btn;
    private int count = 0;
    private int year;
    private int month;
    private int dayOfMonth;
    private int currentHour;
    private int currentMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Intent intent = getIntent();
        notifUri = intent.getData();

//        Log.v("remainderAct", "uri : " + notifUri);

        btn = (Button) findViewById(R.id.date_time_set);
        title = (TextView) findViewById(R.id.textViewReminder);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);
//        String[] projection = new String[]{NotificationsContract.NotifEntry._ID, NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_NAME, NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TEXT};
//        String selection = " " + NotificationsContract.NotifEntry._ID + " == ?" ;
//        String[]selectionArgs = new String[]{notifUri.getLastPathSegment()};
//        Cursor cursor = getContentResolver().query(notifUri, projection, selection, selectionArgs, null);
//        String text = "";
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                text = "" + cursor.getInt(0) + "   name :" + cursor.getString(1) + "    text : " + cursor.getString(2);
//                Log.v("Reminder", "text : " + text);
//            }
//        }
//        getCalendarId();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0){
                    year = datePicker.getYear();
                    month = datePicker.getMonth();
                    dayOfMonth = datePicker.getDayOfMonth();
                    datePicker.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    count++;
                    btn.setText("Set Reminder");
                    title.setText("Set time");
                }else if(count == 1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        currentHour = timePicker.getHour();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Android version not Supported",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        currentMinute = timePicker.getMinute();
                    }

                    setReminder();
                }
            }
        });

    }


    private void setReminder() {
        Calendar calendar = new GregorianCalendar(year,
                month,
                dayOfMonth,
                currentHour,
                currentMinute);
        long currTime = System.currentTimeMillis();
        long reminderSetTime = calendar.getTimeInMillis();
//        Log.v("Reminder", "curr: " + currTime + "  reminder: " + reminderSetTime + " diff : " + (reminderSetTime - currTime));
        if (reminderSetTime < currTime) {
            Toast.makeText(getApplicationContext(), "Reminder can't be set before current time", Toast.LENGTH_SHORT);
            return;
        } else {
            String[] projection = new String[]{NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_NAME, NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TEXT};
            String selection = " " + NotificationsContract.NotifEntry._ID + " == ?" ;
            String[]selectionArgs = new String[]{notifUri.getLastPathSegment()};
            Cursor cursor = getContentResolver().query(notifUri, projection, selection, selectionArgs, null);
            String text = "";
            if (cursor != null && cursor.moveToFirst()) {
                text = cursor.getString(cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_NAME)) + "\n"+
                        cursor.getString(cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TEXT));
            } else {
                Toast.makeText(getApplicationContext(), "Failed to set reminder. Try again later", Toast.LENGTH_SHORT);
                return;
            }
            Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
            ContentResolver cr = getContentResolver();
            TimeZone timeZone = TimeZone.getDefault();
//            Log.v("reminder desc", " " + text);
            /** Inserting an event in calendar. */
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.TITLE, "Notifications Logger Reminder");
            values.put(CalendarContract.Events.DESCRIPTION, text);
            values.put(CalendarContract.Events.ALL_DAY, 0);
            // event starts at time set
            values.put(CalendarContract.Events.DTSTART, reminderSetTime);
            // ends 10 minutes from time set
            long endtime = reminderSetTime + 10 * 60 * 1000;
            values.put(CalendarContract.Events.DTEND, endtime);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            values.put(CalendarContract.Events.HAS_ALARM, 1);
            Uri event = cr.insert(EVENTS_URI, values);

            // Display event id
//                Toast.makeText(getApplicationContext(), "Event added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();
//            Log.v("Event added ID: ", " " + event.getLastPathSegment());

            try {
                /** Adding reminder for event added. */
                Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
                ContentValues values1 = new ContentValues();
//                Log.v("Reminder ID :", " " + Long.parseLong(event.getLastPathSegment()));
                values1.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
                values1.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                values1.put(CalendarContract.Reminders.MINUTES, 10);
                Uri reminderSet = cr.insert(REMINDERS_URI, values1);

                Toast.makeText(this,"Reminder Added Successfully",Toast.LENGTH_SHORT).show();
                //Display reminder id
                if(reminderSet != null){
                    Toast.makeText(this, "Reminder added Successfully " , Toast.LENGTH_SHORT).show();
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(Toast.LENGTH_SHORT); // As I am using LENGTH_LONG in Toast
                                ReminderActivity.this.finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
//                Toast.makeText(this, "Reminder added :: ID :: " + reminderSet.getLastPathSegment(), Toast.LENGTH_SHORT).show();
//                Log.v("Reminder added :ID :", " " + reminderSet.getLastPathSegment());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        finish();
    }


    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }

    private void getCalendarId() {
        try {
            ContentResolver contentResolver = getContentResolver();

            // Fetch a list of all calendars synced with the device, their display names and whether the

            Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                    (new String[]{"_id", "calendar_displayName"}), null, null, null);

//                HashSet<String> calendarIds = new HashSet<String>();


            Log.v("calid", "Count=" + cursor.getCount());
            if (cursor.getCount() > 0) {
                Log.v("calid", "the control is just inside of the cursor.count loop");
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String name = cursor.getString(1);
                    Log.v("CalID", "Id: " + _id + "  " + name);
//                            calendarIds.add(_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
