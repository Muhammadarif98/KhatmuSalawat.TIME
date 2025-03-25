package com.example.khatmusalawattime.presentation.ui.components.settings

import android.content.Context
import androidx.core.content.edit

/**
 * Класс для управления настройками приложения через SharedPreferences
 */
object SettingsPreferences {
    private const val PREFS_NAME = "SettingsPrefs"
    private const val REMINDER_TIME_KEY = "reminder_time"
    private const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"
    private const val DAILY_REMINDER_ENABLED_KEY = "daily_reminder_enabled"
    private const val TIMER_NOTIFICATION_ENABLED_KEY = "timer_notification_enabled"
    private const val DARK_MODE_ENABLED_KEY = "dark_mode_enabled"
    
    /**
     * Сохраняет время напоминания
     */
    fun saveReminderTime(context: Context, time: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(REMINDER_TIME_KEY, time)
            }
    }
    
    /**
     * Загружает сохраненное время напоминания
     */
    fun loadReminderTime(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(REMINDER_TIME_KEY, "16:00") ?: "16:00"
    }
    
    /**
     * Сохраняет состояние включения уведомлений
     */
    fun saveNotificationsEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(NOTIFICATIONS_ENABLED_KEY, enabled)
            }
    }
    
    /**
     * Загружает состояние включения уведомлений
     */
    fun loadNotificationsEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(NOTIFICATIONS_ENABLED_KEY, true)
    }
    
    /**
     * Сохраняет состояние включения ежедневных напоминаний
     */
    fun saveDailyReminderEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(DAILY_REMINDER_ENABLED_KEY, enabled)
            }
    }
    
    /**
     * Загружает состояние включения ежедневных напоминаний
     */
    fun loadDailyReminderEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(DAILY_REMINDER_ENABLED_KEY, true)
    }
    
    /**
     * Сохраняет состояние включения уведомлений о таймере
     */
    fun saveTimerNotificationEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(TIMER_NOTIFICATION_ENABLED_KEY, enabled)
            }
    }
    
    /**
     * Загружает состояние включения уведомлений о таймере
     */
    fun loadTimerNotificationEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(TIMER_NOTIFICATION_ENABLED_KEY, true)
    }
    
    /**
     * Сохраняет состояние темного режима
     */
    fun saveDarkModeEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(DARK_MODE_ENABLED_KEY, enabled)
            }
    }
    
    /**
     * Загружает состояние темного режима
     */
    fun loadDarkModeEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(DARK_MODE_ENABLED_KEY, false)
    }
    
    /**
     * Сохраняет все настройки уведомлений одновременно
     */
    fun saveNotificationSettings(
        context: Context, 
        notificationsEnabled: Boolean,
        dailyReminderEnabled: Boolean,
        timerNotificationEnabled: Boolean
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(NOTIFICATIONS_ENABLED_KEY, notificationsEnabled)
                putBoolean(DAILY_REMINDER_ENABLED_KEY, dailyReminderEnabled)
                putBoolean(TIMER_NOTIFICATION_ENABLED_KEY, timerNotificationEnabled)
            }
    }
} 