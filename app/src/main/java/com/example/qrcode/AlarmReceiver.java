package com.example.qrcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("STOP_ALARM".equals(intent.getAction())) {
            // Stop the AlarmService
            Intent serviceIntent = new Intent(context, AlarmService.class);
            context.stopService(serviceIntent);
        } else {
            // Start the AlarmService when the alarm is triggered
            Intent serviceIntent = new Intent(context, AlarmService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
            Log.d("AlarmReceiver", "Alarm Received, starting service...");
        }
    }
}
