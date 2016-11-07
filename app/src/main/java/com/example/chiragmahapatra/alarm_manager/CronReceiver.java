package com.example.chiragmahapatra.alarm_manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chiragmahapatra on 8/27/16.
 */
public class CronReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;


    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent i = new Intent(context, AlarmService.class);
        context.startService(i);

    }
}
