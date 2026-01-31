package com.example.cup2.notifications
import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import android.content.Intent
import android.app.PendingIntent
import com.example.cup2.ui.general.GeneralQuestionsActivity
import androidx.core.app.NotificationCompat
import com.example.cup2.R
import android.app.Notification
import androidx.core.app.NotificationManagerCompat




object NotificationHelper {

    const val CHANNEL_ID = "questions_channel"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Questions Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showQuestionNotification(context: Context, title: String, message: String) {

        val intent = Intent(context, GeneralQuestionsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = NotificationManagerCompat.from(context)

        // ✅ THIS removes the permission warning
        if (manager.areNotificationsEnabled()) {
            manager.notify(1001, builder.build())
        }
    }
}
