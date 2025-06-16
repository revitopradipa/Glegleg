package com.project.glegleg.features.reminders

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.project.glegleg.R
import com.project.glegleg.ui.core.MainActivity
import com.project.glegleg.utils.Constants

object NotificationHelper {
    private const val NOTIFICATION_ID = 1

    fun showReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlags)

        val builder = NotificationCompat.Builder(context, Constants.REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_water_drop)
            .setContentTitle("Waktunya Glegleg!")
            .setContentText("Jangan lupa jaga hidrasi Anda hari ini.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        Log.d("NotificationHelper", "Mencoba menampilkan notifikasi...")

        with(NotificationManagerCompat.from(context)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(NOTIFICATION_ID, builder.build())
                Log.d("NotificationHelper", "Notifikasi berhasil ditampilkan.")
            } else {
                Log.w("NotificationHelper", "Tidak bisa menampilkan notifikasi karena izin POST_NOTIFICATIONS ditolak.")
            }
        }
    }
}