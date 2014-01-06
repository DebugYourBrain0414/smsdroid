package de.ub0r.android.smsdroid;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ub0r.android.lib.Log;

/**
 * Save messages sent by WebSMS to internal SMS database.
 */
public class WebSMSBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "WebSMSBroadcastReceiver";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive(context, " + intent + ")");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Log.e(TAG, TAG + " not available on API " + Build.VERSION.SDK_INT);
            return;
        }

        if ("de.ub0r.android.websms.SEND_SUCCESSFUL".equals(intent.getAction())) {
            try {
                Bundle extras = intent.getExtras();
                if (extras == null) {
                    Log.w(TAG, "empty extras");
                    return;
                }
                String[] recipients = extras.getStringArray("address");
                if (recipients == null || recipients.length == 0) {
                    Log.w(TAG, "empty recipients");
                    return;
                }

                Pattern numberPattern = Pattern.compile("<(.*?)>");
                for (int i = 0; i < recipients.length; ++i) {
                    // check whether we got a already known address with name
                    //Log.w(TAG, "before recipients: " + recipients[i]);
                    if (recipients[i].contains("<")) {
                        Matcher m = numberPattern.matcher(recipients[i]);
                        if (m.find()) {
                            recipients[i] = m.group(1);
                        } else {
                            Log.w(TAG, "Pattern failed.");
                            recipients[i] = recipients[i].split(" ")[0];
                        }
                    } else {
                        //pure numeric
                        recipients[i] = recipients[i].split(" ")[0];
                    }
                    // Log.w(TAG, "after recipients: " + recipients[i]);
                }

                String body = extras.getString("body");
                if (TextUtils.isEmpty(body)) {
                    Log.w(TAG, "empty body");
                    return;
                }

                ContentResolver cr = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(Telephony.Sms.BODY, body);

                // Insert all sms as sent
                for (int i = 0; i < recipients.length; ++i) {
                    values.put(Telephony.Sms.ADDRESS, recipients[i]);
                    cr.insert(Telephony.Sms.Sent.CONTENT_URI, values);

                    Log.d(TAG, "Recipient " + i + " of " + recipients.length);
                    Log.d(TAG, "Insert sent SMS into database: " + recipients[i] + ", " + body);
                }
            } catch (Exception e) {
                Log.e(TAG, "unable to write messages to database", e);
            }
        }
    }
}