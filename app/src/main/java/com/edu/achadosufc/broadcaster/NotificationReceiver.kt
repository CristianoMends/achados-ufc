package com.edu.achadosufc.broadcaster


import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.edu.achadosufc.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val itemTitle = intent.getStringExtra("ITEM_TITLE") ?: "Lembrete de item"
        val itemId = intent.getIntExtra("ITEM_ID", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "item_reminder_channel")
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Lembrete de AchadosUFC")
            .setContentText("Não se esqueça sobre o item: $itemTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(itemId, notification)
    }
}