package com.example.chiragmahapatra.alarm_manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;


public class MainActivity extends AppCompatActivity {

    private int CALL_LOG_PERMISSION_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        if (isReadCallLogAllowed()) {
            startCron();
        } else {
            Log.i("AppPermissions", "Call log permission not granted");
            Toast.makeText(getApplicationContext(), "Please grant call log permissions",
                    Toast.LENGTH_LONG);
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.READ_CALL_LOG},
                    CALL_LOG_PERMISSION_CODE);
        }
    }

    private boolean isReadCallLogAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CALL_LOG);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == CALL_LOG_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                startCron();
                Toast.makeText(this,"Permission granted now you can read the call log",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void startCron()
    {
        Intent intent=new Intent(getApplicationContext(), CronReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, CronReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                1000, pIntent);
    }
}
