package com.edu.achadosufc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edu.achadosufc.model.login.LoginRepository
import com.edu.achadosufc.model.user.UserPreferencesRepository
import com.edu.achadosufc.model.user.UserRepository
import com.edu.achadosufc.ui.screen.HomeScreen
import com.edu.achadosufc.ui.screen.ItemDetailScreen
import com.edu.achadosufc.ui.screen.LoginScreen
import com.edu.achadosufc.ui.screen.ReportItemScreen
import com.edu.achadosufc.ui.screen.Screen
import com.edu.achadosufc.ui.screen.SignUpScreen
import com.edu.achadosufc.ui.screen.UserDetailScreen
import com.edu.achadosufc.ui.screen.UserProfileScreen
import com.edu.achadosufc.ui.theme.AchadosUFCTheme
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.SignUpViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val userRepository = UserRepository()
            val userPreferencesRepository = remember { UserPreferencesRepository(context = applicationContext) }
            val loginViewModel: LoginViewModel = viewModel() {
                LoginViewModel(
                    userRepository,
                    loginRepository = LoginRepository(),
                    userPreferencesRepository = userPreferencesRepository
                )
            }
            val signUpViewModel: SignUpViewModel = viewModel(){
                SignUpViewModel(
                    userRepository = userRepository,
                    userPreferencesRepository = userPreferencesRepository,
                    applicationContext = applicationContext
                )
            }
            val navController = rememberNavController()

            AchadosUFCTheme(darkTheme = isDarkTheme) {
                NavHost(navController, startDestination = Screen.Login.route) {

                    composable(Screen.SignUp.route) {
                        SignUpScreen(
                            navController = navController,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme },
                            signUpViewModel = signUpViewModel
                        )
                    }
                    composable(
                        route = Screen.ItemDetail.route,
                        arguments = listOf(navArgument("itemId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getInt("itemId")
                        if (itemId != null && itemId != -1) {
                            ItemDetailScreen(
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                navController = navController,
                                itemId = itemId
                            )
                        } else {
                            Text(text = "Erro: ID do item não encontrado.")
                        }
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(
                            navController = navController,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme }
                        )

                    }
                    composable(Screen.ReportItem.route) {
                        ReportItemScreen(
                            navController = navController,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme })
                    }

                    composable(Screen.Login.route) {
                        LoginScreen(
                            navController = navController,
                            loginViewModel = loginViewModel
                        )
                    }
                    composable(Screen.Profile.route) {
                        UserProfileScreen(
                            navController = navController,
                            isDarkTheme = isDarkTheme,
                            loginViewModel = loginViewModel,
                            onToggleTheme = { isDarkTheme = !isDarkTheme }
                        )

                    }
                    composable(
                        route = Screen.UserDetail.route,
                        arguments = listOf(navArgument("userId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: -1
                        UserDetailScreen(
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme },
                            navController = navController,
                            userId = userId
                        )
                    }

                }
            }
        }
    }
}
