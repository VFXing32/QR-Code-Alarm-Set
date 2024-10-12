# QR Code Alarm App

This Android application scans QR codes containing date and time information, sets alarms based on the scanned data, and notifies the user when the alarm goes off. 

## Features
- QR Code scanning to set alarms.
- Alarm confirmation with the set date and time.
- Alarm ringing functionality with sound.
- Stop alarm feature from the notification or within the app.

## Project Structure
- **MainActivity**: Handles QR code scanning and processes the date and time to set alarms.
- **AlarmRingingActivity**: Displays a screen with an alarm ringing message and a button to stop the alarm.
- **AlarmConfirmationActivity**): Displays confirmation of the alarm set based on the QR code scanned.
- **AlarmReceiver**: Handles broadcast events for starting and stopping alarms.
- **AlarmService**: Manages the foreground service that plays the alarm sound and displays a notification.

## Screens
1. **Main Screen** (`activity_main.xml`):
    - Displays the camera preview and a text prompt to scan a QR code. When a valid QR code is scanned, an alarm is set based on the date and time in the QR code. 
    - The result of the scan is also displayed on the screen.
    
2. **Alarm Ringing Screen** (`activity_alarm_ringing.xml`):
    - Displays a message when the alarm is ringing and provides a button to stop the alarm.
    
3. **Alarm Confirmation Screen** (`activity_alarm_confirmation.xml`):
    - Confirms the alarm time after scanning a QR code, showing the date and time in a formatted text view.

## How It Works
1. **QR Code Scanning**:
    - The app uses the camera to scan a QR code containing the alarm time in the format `yyyy-MM-dd hh:mm AM/PM Day`.
    - Once scanned, the app parses the date and time from the QR code and sets an alarm using Android's `AlarmManager`.

    <img src="Screenshot 2024-10-12 170911.png" alt="QR Screenshot" width="618" height="301">

2. **Alarm Service**:
    - The `AlarmService` plays the alarm sound in the background using the default alarm sound or a notification sound if the alarm sound is unavailable.
    - The app uses a foreground service to ensure that the alarm continues to play even if the app is in the background.

3. **Stopping the Alarm**:
    - The alarm can be stopped via the notification by pressing the "Stop" action or by using the "Stop" button in the `AlarmRingingActivity`.

## Permissions Required
- **Camera**: To scan QR codes.
- **Exact Alarms**: To set exact alarms based on the scanned QR code.
- **Post Notifications**: For displaying the alarm notification and stop functionality.

## How to Build and Run
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repository.git

## Contributing
Contributions are welcome! Please feel free to submit a pull request or open an issue if you have any suggestions for improvements.

## Contact
For any questions or issues, feel free to reach out:

GitHub: [VFXing32](https://github.com/VFXing32)
