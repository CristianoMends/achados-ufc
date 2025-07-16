package com.edu.achadosufc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.edu.achadosufc.data.dao.AppDatabase
import com.edu.achadosufc.data.repository.FileRepository
import com.edu.achadosufc.data.repository.ItemRepository
import com.edu.achadosufc.data.repository.LoginRepository
import com.edu.achadosufc.data.UserPreferences
import com.edu.achadosufc.data.repository.UserRepository
import com.edu.achadosufc.data.service.AuthService
import com.edu.achadosufc.data.service.FileService
import com.edu.achadosufc.data.service.ItemService
import com.edu.achadosufc.data.service.UserService
import com.edu.achadosufc.navigation.AppNavHost
import com.edu.achadosufc.ui.theme.AchadosUFCTheme
import com.edu.achadosufc.viewModel.HomeViewModel
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ReportViewModel
import com.edu.achadosufc.viewModel.SignUpViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel
import com.edu.achadosufc.viewModel.ThemeViewModelFactory
import com.edu.achadosufc.viewModel.UserViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            createNotificationChannel()
            val navController = rememberNavController()

            val appDatabase = AppDatabase.getDatabase(this)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://achados-ufc-api-hch7.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val userService = retrofit.create(UserService::class.java)
            val itemService = retrofit.create(ItemService::class.java)
            val authService = retrofit.create(AuthService::class.java)
            val fileService = retrofit.create(FileService::class.java)


            val userRepository: UserRepository = UserRepository(userService, appDatabase.userDao())
            val loginRepository: LoginRepository = LoginRepository(this, api = authService)
            val userPreferencesRepository: UserPreferences =
                UserPreferences(this)
            val themeViewModel: ThemeViewModel by viewModels {
                ThemeViewModelFactory(userPreferencesRepository)
            }
            val fileRepository: FileRepository = FileRepository(fileService)
            val itemRepository: ItemRepository =
                ItemRepository(itemDao = appDatabase.itemDao(), api = itemService)

            val loginViewModel: LoginViewModel = viewModel() {
                LoginViewModel(
                    loginRepository = loginRepository,
                    userRepository = userRepository,
                    userPreferencesRepository = userPreferencesRepository,
                    context = this@MainActivity
                )
            }
            val homeViewModel: HomeViewModel = viewModel() {
                HomeViewModel(
                    itemRepository = itemRepository
                )
            }
            val userViewModel: UserViewModel = viewModel() {
                UserViewModel(
                    userRepository = userRepository,
                    itemRepository = itemRepository,
                    userPreferencesRepository = userPreferencesRepository,
                    this@MainActivity,
                )
            }
            val itemViewModel: ItemViewModel = viewModel() {
                ItemViewModel(
                    itemRepository = itemRepository
                )
            }
            val reportViewModel: ReportViewModel = viewModel() {
                ReportViewModel(
                    itemRepository = itemRepository,
                    applicationContext = this@MainActivity
                )
            }
            val signUpViewModel: SignUpViewModel = viewModel() {
                SignUpViewModel(
                    userRepository = userRepository,
                    uploadRepository = fileRepository,
                    applicationContext = this@MainActivity,
                    loginViewModel = loginViewModel
                )
            }
            val themeMode by themeViewModel.themeMode.collectAsState()

            AchadosUFCTheme(themeMode = themeMode) {
                AppNavHost(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    signUpViewModel = signUpViewModel,
                    homeViewModel = homeViewModel,
                    userViewModel = userViewModel,
                    itemViewModel = itemViewModel,
                    reportViewModel = reportViewModel,
                    themeViewModel = themeViewModel
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
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}