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

@Composable
fun AppNavHost(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    signUpViewModel: SignUpViewModel,
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
    itemViewModel: ItemViewModel,
    reportViewModel: ReportViewModel,
    themeViewModel: ThemeViewModel,
    chatViewModel: ChatViewModel
) {
    val isAutoLoginCheckComplete by loginViewModel.isAutoLoginCheckComplete.collectAsState()
    val loggedUser by loginViewModel.loggedUser.collectAsState()
    val startDestination = remember { mutableStateOf(Screen.Splash.route) }

    LaunchedEffect(isAutoLoginCheckComplete, loggedUser) {
        if (isAutoLoginCheckComplete) {
            val destination = if (loggedUser != null) {
                Screen.Home.route
            } else {
                Screen.Login.route
            }

            startDestination.value = destination

            navController.navigate(destination) {
                popUpTo(Screen.Splash.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    NavHost(navController, startDestination = startDestination.value) {
        composable(Screen.Splash.route) {
            SplashScreen()
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
                signUpViewModel = signUpViewModel,
                themeViewModel = themeViewModel
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
                    itemViewModel = itemViewModel,
                    loginViewModel = loginViewModel,
                    themeViewModel = themeViewModel,
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
                    chatViewModel = chatViewModel,
                    loginViewModel = loginViewModel,
                    themeViewModel = themeViewModel,
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
                homeViewModel = homeViewModel,
                itemViewModel = itemViewModel,
                themeViewModel = themeViewModel,
                loginViewModel = loginViewModel
            )
        }
        composable(Screen.ReportItem.route) {
            ReportItemScreen(
                navController = navController,
                reportViewModel = reportViewModel,
                themeViewModel = themeViewModel,
                loginViewModel = loginViewModel
            )
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
                loginViewModel = loginViewModel,
                itemViewModel = itemViewModel,
                themeViewModel = themeViewModel,
                userViewModel = userViewModel
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
                userViewModel = userViewModel,
                themeViewModel = themeViewModel,
                itemViewModel = itemViewModel,
                loginViewModel = loginViewModel
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                itemViewModel = itemViewModel,
                themeViewModel = themeViewModel,
                loginViewModel = loginViewModel
            )
        }
    }
}