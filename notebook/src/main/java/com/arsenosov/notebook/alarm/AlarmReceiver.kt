package com.arsenosov.notebook.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import com.arsenosov.notebook.R
import com.arsenosov.notebook.compose.MainActivity
import kotlinx.coroutines.InternalCoroutinesApi

class AlarmReceiver : BroadcastReceiver() {

    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @InternalCoroutinesApi
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, i, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_NAME)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Запланированный созвон")
            //.setContentText()
            .setAutoCancel(true)
            .setDefaults(DEFAULT_ALL)
            .setPriority(PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, builder)
    }

    companion object {
        val CHANNEL_NAME = "alarm_channel"
        val NOTIFICATION_ID = 200
    }
}