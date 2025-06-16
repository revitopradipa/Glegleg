package com.project.glegleg.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionUtils {

    // Fungsi untuk memeriksa apakah izin notifikasi sudah diberikan.
    fun hasNotificationPermission(context: Context): Boolean {
        // Untuk API di bawah 33 (Tiramisu), izin dianggap sudah ada secara default.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        // Untuk API 33 ke atas, kita harus cek secara eksplisit.
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Fungsi untuk meminta izin notifikasi menggunakan launcher yang disediakan.
    fun requestNotificationPermission(permissionLauncher: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}