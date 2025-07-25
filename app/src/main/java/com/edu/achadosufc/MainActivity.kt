package com.edu.achadosufc

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.edu.achadosufc.navigation.AppNavHost
import com.edu.achadosufc.ui.theme.AchadosUFCTheme
import com.edu.achadosufc.viewModel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    private var newIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES .TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel()
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            createNotificationChannels()

            AchadosUFCTheme(themeMode = themeMode) {
                AppNavHost(
                    navController = navController
                )
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        newIntent = intent
    }

    private fun createNotificationChannels() {
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val itemChannel = NotificationChannel(
            "item_reminder_channel",
            "Lembretes de Itens",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para receber lembretes sobre itens perdidos/achados."
        }
        notificationManager.createNotificationChannel(itemChannel)


        val chatChannel = NotificationChannel(
            "chat_messages_channel",
            "Novas Mensagens",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificações para novas mensagens recebidas no chat."
        }
        notificationManager.createNotificationChannel(chatChannel)
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permissão concedida
        } else {

        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

}