package com.edu.achadosufc.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edu.achadosufc.ui.screen.ChatScreen
import com.edu.achadosufc.ui.screen.HomeScreen
import com.edu.achadosufc.ui.screen.ItemDetailScreen
import com.edu.achadosufc.ui.screen.LoginScreen
import com.edu.achadosufc.ui.screen.ReportItemScreen
import com.edu.achadosufc.ui.screen.Screen
import com.edu.achadosufc.ui.screen.SearchScreen
import com.edu.achadosufc.ui.screen.SignUpScreen
import com.edu.achadosufc.ui.screen.SplashScreen
import com.edu.achadosufc.ui.screen.UserDetailScreen
import com.edu.achadosufc.ui.screen.UserProfileScreen
import com.edu.achadosufc.viewModel.ChatViewModel
import com.edu.achadosufc.viewModel.HomeViewModel
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ReportViewModel
import com.edu.achadosufc.viewModel.SignUpViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel
import com.edu.achadosufc.viewModel.UserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
                signUpViewModel = koinViewModel(),
                themeViewModel = koinViewModel()
            )
        }
        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId")
            if (itemId != null && itemId != -1) {
                ItemDetailScreen(
                    navController = navController,
                    itemId = itemId,
                    itemViewModel = koinViewModel(),
                    loginViewModel = koinViewModel(),
                    themeViewModel = koinViewModel(),
                )
            } else {
                Text(text = "Erro: ID do item não encontrado.")
            }
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("recipientId") { type = NavType.IntType },
                navArgument("recipientUsername") { type = NavType.StringType },
                navArgument("recipientPhotoUrl") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val recipientId = backStackEntry.arguments?.getInt("recipientId") ?: -1
            val recipientUsername = backStackEntry.arguments?.getString("recipientUsername") ?: ""
            val recipientPhotoUrl = backStackEntry.arguments?.getString("recipientPhotoUrl")


            if (recipientId != -1 && recipientUsername.isNotEmpty()) {
                ChatScreen(
                    navController = navController,
                    chatViewModel = koinViewModel(),
                    loginViewModel = koinViewModel(),
                    recipientId = recipientId,
                    recipientUsername = recipientUsername,
                    recipientPhotoUrl = recipientPhotoUrl
                )
            } else {
                Text(text = "Erro: Destinatário inválido.")
            }
        }
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                homeViewModel = koinViewModel(),
                itemViewModel = koinViewModel(),
                themeViewModel = koinViewModel(),
                loginViewModel = koinViewModel()
            )
        }
        composable(Screen.ReportItem.route) {
            ReportItemScreen(
                navController = navController,
                reportViewModel = koinViewModel(),
                themeViewModel = koinViewModel(),
                loginViewModel = koinViewModel()
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                loginViewModel = koinViewModel()
            )
        }
        composable(Screen.Profile.route) {
            UserProfileScreen(
                navController = navController,
                loginViewModel = koinViewModel(),
                itemViewModel = koinViewModel(),
                themeViewModel = koinViewModel(),
            )
        }
        composable(
            route = Screen.UserDetail.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            UserDetailScreen(
                navController = navController,
                userId = userId,
                userViewModel = koinViewModel(),
                themeViewModel = koinViewModel()
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                navController = navController,
                homeViewModel = koinViewModel(),
                itemViewModel = koinViewModel(),
                themeViewModel = koinViewModel(),
                loginViewModel = koinViewModel()
            )
        }
    }
}