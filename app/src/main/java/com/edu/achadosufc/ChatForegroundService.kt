package com.edu.achadosufc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.edu.achadosufc.data.service.ChatSocketService
import org.koin.core.context.GlobalContext

class ChatForegroundService : Service() {

    private lateinit var socketService: ChatSocketService

    override fun onCreate() {
        super.onCreate()
        socketService = GlobalContext.get().get()
        socketService.connect()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundWithNotification()
        return START_STICKY
    }

    override fun onDestroy() {
        socketService.disconnect()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundWithNotification() {
        val notification = NotificationCompat.Builder(this, "chat_messages_channel")
            .setContentTitle("Conectado ao chat")
            .setContentText("Aguardando novas mensagens")
            .setSmallIcon(R.drawable.brasao_vertical_cor)
            .build()

        startForeground(1, notification)
    }
}
