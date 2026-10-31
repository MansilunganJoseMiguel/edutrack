package com.example.edutrack

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DueNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("TITLE") ?: "Assignment"
        val type = intent.getStringExtra("TYPE") ?: "Task"

        NotificationHelper.showNotification(
            context,
            "EduTrack Reminder: $type Due Today!",
            "\"$title\" is due today. Make sure to turn in your work!"
        )
    }
}