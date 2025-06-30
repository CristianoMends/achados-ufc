package com.edu.achadosufc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.edu.achadosufc.data.repository.FileRepository
import com.edu.achadosufc.data.repository.ItemRepository
import com.edu.achadosufc.data.repository.LoginRepository
import com.edu.achadosufc.data.repository.UserPreferencesRepository
import com.edu.achadosufc.data.repository.UserRepository
import com.edu.achadosufc.navigation.AppNavHost
import com.edu.achadosufc.ui.theme.AchadosUFCTheme
import com.edu.achadosufc.viewModel.HomeViewModel
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ReportViewModel
import com.edu.achadosufc.viewModel.SignUpViewModel
import com.edu.achadosufc.viewModel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val navController = rememberNavController()

            val userRepository: UserRepository = UserRepository()
            val loginRepository: LoginRepository = LoginRepository(this)
            val userPreferencesRepository: UserPreferencesRepository =
                UserPreferencesRepository(this)
            val fileRepository: FileRepository = FileRepository()
            val itemRepository: ItemRepository = ItemRepository()

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
                    itemRepository = itemRepository
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
                    fileRepository = fileRepository,
                    applicationContext = this@MainActivity
                )
            }
            val signUpViewModel: SignUpViewModel = viewModel() {
                SignUpViewModel(
                    userRepository = userRepository,
                    userPreferencesRepository = userPreferencesRepository,
                    uploadRepository = fileRepository,
                    applicationContext = this@MainActivity
                )
            }

            AchadosUFCTheme(darkTheme = isDarkTheme) {
                AppNavHost(
                    navController = navController,
                    isDarkTheme = remember { mutableStateOf(isDarkTheme) },
                    onToggleTheme = { isDarkTheme = !isDarkTheme },
                    loginViewModel = loginViewModel,
                    signUpViewModel = signUpViewModel,
                    homeViewModel = homeViewModel,
                    userViewModel = userViewModel,
                    itemViewModel = itemViewModel,
                    reportViewModel = reportViewModel
                )
            }
        }
    }
}