package com.ghosts.android.stopwatch;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Allam on 13/07/2016.
 */
public class AlertService extends IntentService {
    private static final String TAG = "StopWatchService";

    public static Intent newIntent(Context context) {
        return new Intent(context, AlertService.class);
    }

    public AlertService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Allam" , "repeated");
    }

    public static void setServiceOn(Context context, boolean isOn) {
        Intent serviceIntent = newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Toast.makeText(context,  new Utility(context).getRepeativeTime() + " ", Toast.LENGTH_SHORT).show();
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, new Utility(context).getRepeativeTime(),
                    new Utility(context).getRepeativeTime(), pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }


}
