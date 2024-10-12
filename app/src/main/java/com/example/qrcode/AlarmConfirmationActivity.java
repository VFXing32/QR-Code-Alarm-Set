package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_confirmation);

        TextView alarmInfoTextView = findViewById(R.id.alarmInfoTextView);

        // Get the alarm time passed from MainActivity
        Intent intent = getIntent();
        String alarmTime = intent.getStringExtra("ALARM_TIME");

        // Set the alarm time in the TextView
        alarmInfoTextView.setText("Alarm set for: " + alarmTime);
    }
}
