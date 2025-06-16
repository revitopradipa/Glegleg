
package com.project.glegleg.data.local.prefs

import android.content.Context
import android.content.SharedPreferences

object AppSettingsManager {
    private const val PREFS_NAME = "glegleg_prefs"
    private const val KEY_TARGET_AMOUNT = "target_amount"
    private const val KEY_NOTIF_ENABLED = "notif_enabled"

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setTargetAmount(context: Context, amount: Int) {
        getPrefs(context).edit().putInt(KEY_TARGET_AMOUNT, amount).apply()
    }

    fun getTargetAmount(context: Context): Int =
        getPrefs(context).getInt(KEY_TARGET_AMOUNT, 2000)

    fun setNotificationEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply()
    }

    fun isNotificationEnabled(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_NOTIF_ENABLED, true)
}
