package com.example.chiragmahapatra.alarm_manager;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import android.provider.Settings.Secure;

import com.firebase.client.Firebase;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by chiragmahapatra on 8/27/16.
 */

class Phone {
    private String phNum;
    private Date callDate;
    private String callType;
    private String callDuration;
    private String formattedDate;

    public Phone(String phNum, Date callDate, String callType, String callDuration, String formattedDate) {
        this.phNum = phNum;
        this.callDate = callDate;
        this.callType = callType;
        this.callDuration = callDuration;
        this.formattedDate = formattedDate;
    }
    public String getPhNum() {
        return phNum;
    }
    public Date getCallDate() {
        return callDate;
    }

    public String getCallType() {
        return callType;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public String getFormattedDate() {
        return formattedDate;
    }
}

public class AlarmService extends IntentService {
    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        String deviceId = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);
        ArrayList<Phone> callDetails = getCallDetailsFromCallLog();
        Firebase.setAndroidContext(this);
        try {
            Firebase myFirebaseRef = new Firebase("https://missedcall-c258c.firebaseio.com/");
            myFirebaseRef.child(deviceId).setValue(callDetails);
            Log.i("AlarmService", callDetails.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Phone> getCallDetailsFromCallLog() {
        ArrayList <Phone> phones = new ArrayList<Phone>();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String phNum = managedCursor.getString(number);
            String callTypeCode = managedCursor.getString(type);
            String strcallDate = managedCursor.getString(date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            Date callDate = new Date(Long.valueOf(strcallDate));
            String formattedDate = sdf.format(callDate);
            String callDuration = managedCursor.getString(duration);
            String callType = null;
            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
            }
            Phone phone = new Phone(phNum, callDate, callType, callDuration, formattedDate);
            phones.add(phone);
        }
        managedCursor.close();
        return phones;
    }
}
