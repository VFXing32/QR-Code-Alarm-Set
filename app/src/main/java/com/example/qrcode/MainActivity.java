package com.example.qrcode;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 11;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{android.Manifest.permission.CAMERA};
    private static final String TAG = "QRScanner";
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 12; // Define this constant
    private PreviewView previewView;
    private TextView textView;

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        textView = findViewById(R.id.textView);

        if (allPermissionsGranted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
                } else {
                    startCamera();
                }
            } else {
                startCamera();
            }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @androidx.camera.core.ExperimentalGetImage
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), this::processImageProxy);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @androidx.camera.core.ExperimentalGetImage
    private void processImageProxy(ImageProxy imageProxy) {
        @SuppressWarnings("ConstantConditions")
        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();

        BarcodeScanning.getClient(options)
                .process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String qrCodeResult = barcode.getRawValue();
                        textView.setText(qrCodeResult); // Display the scanned text
                        processQRCodeResult(qrCodeResult); // Set the alarm based on QR code
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Barcode scanning failed", e))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void processQRCodeResult(String qrCodeResult) {
        String qrDateFormat = "yyyy-MM-dd hh:mm a EEEE";
        SimpleDateFormat dateFormat = new SimpleDateFormat(qrDateFormat);

        try {
            Date alarmDate = dateFormat.parse(qrCodeResult);
            if (alarmDate != null) {
                setAlarm(alarmDate);

                // Navigate to the AlarmConfirmationActivity and pass the alarm time
                Intent intent = new Intent(this, AlarmConfirmationActivity.class);
                intent.putExtra("ALARM_TIME", dateFormat.format(alarmDate));
                startActivity(intent);
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format in QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAlarm(Date alarmDate) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarmDate);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    try {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } catch (SecurityException e) {
                        Toast.makeText(this, "Permission required to set exact alarms.", Toast.LENGTH_SHORT).show();
                        requestExactAlarmPermission();
                    }
                } else {
                    Toast.makeText(this, "Cannot schedule exact alarms. Please enable the permission in settings.", Toast.LENGTH_SHORT).show();
                    requestExactAlarmPermission();
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Notification permission granted
            } else {
                Toast.makeText(this, "Notification permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @androidx.camera.core.ExperimentalGetImage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            if (resultCode == RESULT_OK) {
                startCamera();
            } else {
                Toast.makeText(this, "Exact alarm permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
