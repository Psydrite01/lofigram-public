package com.psydrite.lofigram.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.psydrite.lofigram.R
import com.psydrite.lofigram.data.remote.viewmodel.PomodoroState
import com.psydrite.lofigram.ui.components.errorMessage

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "pomodoro_channel"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pomodoro Timer"
            val descriptionText = "Notifications for Pomodoro timer sessions"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendPomodoroFinishedNotification(state: PomodoroState) {
        val (title, message) = when (state) {
            PomodoroState.WORK -> Pair(
                "Work Session Complete! 🍅",
                "Great job! Time for a break."
            )
            PomodoroState.SHORT_BREAK -> Pair(
                "Break Time Over! ⏰",
                "Ready to get back to work?"
            )
            PomodoroState.LONG_BREAK -> Pair(
                "Long Break Complete! 🎉",
                "Refreshed and ready for the next session!"
            )
            PomodoroState.STOPPED -> return //no notification for stopped state
        }

        // Create an intent to open the app when notification is tapped
        val intent = Intent(context, context.javaClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setDefaults(NotificationCompat.DEFAULT_SOUND)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            //handling case when notfication permission is not granted
            errorMessage = e.message.toString()
        }
    }
}