package com.example.khatmusalawattime

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.example.khatmusalawattime.presentation.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KhatmuSalawatApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationHelper.createNotificationChannel(notificationManager)
    }
}