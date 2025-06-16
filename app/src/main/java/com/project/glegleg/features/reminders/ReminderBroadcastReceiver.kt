package com.project.glegleg.features.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.project.glegleg.features.reminders.NotificationHelper

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "ReminderReceiver" // Tag untuk logging
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ReminderReceiver", "Alarm diterima! Waktunya Glegleg!")

        if (context == null) {
            Log.e(TAG, "Context is null, cannot proceed.")
            return
        }

        NotificationHelper.showReminderNotification(context)
    }
}