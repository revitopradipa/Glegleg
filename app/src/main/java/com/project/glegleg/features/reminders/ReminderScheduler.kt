package com.project.glegleg.features.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.concurrent.TimeUnit
import com.project.glegleg.utils.Constants

object ReminderScheduler {
    fun scheduleReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val triggerTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15)
        val interval = TimeUnit.SECONDS.toMillis(15)

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(Constants.EXTRA_REMINDER_ID, Constants.DEFAULT_REMINDER_ID)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.DEFAULT_REMINDER_ID,
            intent,
            pendingIntentFlags
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            interval,
            pendingIntent
        )

        Log.d("ReminderScheduler", "Alarm berulang dijadwalkan setiap 2 jam.")
    }

    fun cancelReminder(context: Context, reminderId: Int = Constants.DEFAULT_REMINDER_ID) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderBroadcastReceiver::class.java)

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.DEFAULT_REMINDER_ID,
            intent,
            pendingIntentFlags
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("ReminderScheduler", "Alarm berhasil dibatalkan.")
        } else {
            Log.d("ReminderScheduler", "Tidak ada alarm yang perlu dibatalkan.")
        }

    }
}