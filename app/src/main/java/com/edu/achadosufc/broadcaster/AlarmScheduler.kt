package com.edu.achadosufc.broadcaster

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(itemId: Int, itemTitle: String, time: LocalDateTime) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("ITEM_ID", itemId)
            putExtra("ITEM_TITLE", itemTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                pendingIntent
            )
        } else {

        }
    }

    fun cancel(itemId: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}