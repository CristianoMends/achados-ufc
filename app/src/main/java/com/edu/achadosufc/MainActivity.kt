package com.edu.achadosufc

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.edu.achadosufc.data.repository.ChatRepository
import com.edu.achadosufc.navigation.AppNavHost
import com.edu.achadosufc.ui.screen.Screen
import com.edu.achadosufc.ui.theme.AchadosUFCTheme
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


class MainActivity : ComponentActivity() {
    private var newIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

            val chatRepository: ChatRepository = koinInject()

            val loginViewModel: LoginViewModel = koinViewModel()
            val loggedUser by loginViewModel.loggedUser.collectAsStateWithLifecycle()
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            LaunchedEffect(loggedUser?.id) {
                loggedUser?.id?.let { userId ->
                    chatRepository.listenToNewMessages(
                        userId.toString(),
                        { m ->
                            val currentRoute = navBackStackEntry?.destination?.route
                            //if (currentRoute != Screen.Chat.route) {
                                showNotification(
                                    this@MainActivity,
                                    m.title,
                                    m.texto
                                )
                                Log.d("MainActivity", "Nova mensagem recebida: ${m.texto}")
                           // }
                        },
                        { error -> Log.e("MainActivity", "Erro ao ouvir mensagens", error) }
                    )
                }
            }


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
}

fun showNotification(context: Context, titulo: String, mensagem: String) {
    val canalId = "mensagens_novas"
    val notificationId = (System.currentTimeMillis() % 10000).toInt()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val canal = NotificationChannel(
            canalId,
            "Mensagens Recebidas",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(canal)
    }

    val builder = NotificationCompat.Builder(context, canalId)
        .setSmallIcon(R.drawable.notification)
        .setContentTitle(titulo)
        .setContentText(mensagem)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(notificationId, builder.build())
    }
}