package com.tapbi.demomessage.receive;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationManagerCompat;

import com.tapbi.demomessage.R;

public class SMSReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }
                String sender = messages[0].getOriginatingAddress();
                String message = sb.toString();

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("SMS_RECEIVED_ACTION");
                broadcastIntent.putExtra("sender",sender);
                broadcastIntent.putExtra("message", message);
                context.sendBroadcast(broadcastIntent);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                
                Notification noti = new Notification.Builder(context)
                        .setContentTitle("Message " + sender.toString())
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_message)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_user))
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(122, noti);
            }
        }
    }
}