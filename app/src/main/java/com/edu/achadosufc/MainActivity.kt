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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.edu.achadosufc.navigation.AppNavHost
import com.edu.achadosufc.ui.screen.Screen
import com.edu.achadosufc.ui.theme.AchadosUFCTheme
import com.edu.achadosufc.viewModel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    private var newIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel()
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            // Passo 1: Lide com o intent que INICIOU o app e o que chegou via onNewIntent
            // Isso garante que tanto o primeiro clique quanto cliques subsequentes funcionem.
            val intentToHandle = newIntent ?: intent

            // Passo 2: O LaunchedEffect garante que a navegação só ocorrerá APÓS a UI ser composta
            LaunchedEffect(intentToHandle) {
                // Nossa função agora só extrai os dados e retorna a rota como String
                fun getRouteFromIntent(intent: Intent?): String? {
                    val senderId = intent?.getIntExtra("chat_sender_id", -1) ?: -1
                    return if (senderId != -1) {
                        val senderUsername = intent?.getStringExtra("chat_sender_username") ?: "Chat"
                        val senderPhotoUrl = intent?.getStringExtra("chat_sender_photo_url")

                        Screen.Chat.createRoute(
                            recipientId = senderId,
                            recipientUsername = senderUsername,
                            photoUrl = senderPhotoUrl
                        )
                    } else {
                        null
                    }
                }

                getRouteFromIntent(intentToHandle)?.let { route ->
                    navController.navigate(route)
                    // Limpa o intent após a navegação para evitar re-navegar em recomposições
                    newIntent = null
                    setIntent(null)
                }
            }

            // A chamada para criar os canais continua aqui
            createNotificationChannels()

            AchadosUFCTheme(themeMode = themeMode) {
                // O AppNavHost será composto, configurando o gráfico no navController
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

        // Canal para Lembretes de Itens
        val itemChannel = NotificationChannel(
            "item_reminder_channel",
            "Lembretes de Itens",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para receber lembretes sobre itens perdidos/achados."
        }
        notificationManager.createNotificationChannel(itemChannel)


        // Canal para mensagens de chat
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
        // Só é necessário no Android 13 (TIRAMISU) ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

}