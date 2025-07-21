package com.edu.achadosufc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.edu.achadosufc.navigation.AppNavHost
import com.edu.achadosufc.ui.theme.AchadosUFCTheme
import com.edu.achadosufc.ui.theme.ThemeMode
import com.edu.achadosufc.viewModel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            createNotificationChannel()
            val themeViewModel: ThemeViewModel = koinViewModel()
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            val navController = rememberNavController()

            AchadosUFCTheme(themeMode = themeMode) {
                AppNavHost(
                    navController = navController
                )
            }

        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "item_reminder_channel",
            "Lembretes de Itens",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Canal para receber lembretes sobre itens perdidos/achados."
        }


        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}