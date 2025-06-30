package com.edu.achadosufc.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edu.achadosufc.ui.screen.HomeScreen
import com.edu.achadosufc.ui.screen.ItemDetailScreen
import com.edu.achadosufc.ui.screen.LoginScreen
import com.edu.achadosufc.ui.screen.ReportItemScreen
import com.edu.achadosufc.ui.screen.Screen
import com.edu.achadosufc.ui.screen.SignUpScreen
import com.edu.achadosufc.ui.screen.UserDetailScreen
import com.edu.achadosufc.ui.screen.UserProfileScreen
import com.edu.achadosufc.viewModel.HomeViewModel
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ReportViewModel
import com.edu.achadosufc.viewModel.SignUpViewModel
import com.edu.achadosufc.viewModel.UserViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    isDarkTheme: MutableState<Boolean>,
    onToggleTheme: () -> Unit,
    loginViewModel: LoginViewModel,
    signUpViewModel: SignUpViewModel,
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
    itemViewModel: ItemViewModel,
    reportViewModel: ReportViewModel
) {
    NavHost(navController, startDestination = Screen.Login.route) {
        composable(Screen.SignUp.route) {
            SignUpScreen(
                navController = navController,
                isDarkTheme = isDarkTheme.value,
                onToggleTheme = onToggleTheme,
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
                    isDarkTheme = isDarkTheme.value,
                    onToggleTheme = onToggleTheme,
                    navController = navController,
                    itemId = itemId,
                    itemViewModel = itemViewModel,
                    loginViewModel = loginViewModel
                )
            } else {
                Text(text = "Erro: ID do item não encontrado.")
            }
        }
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                isDarkTheme = isDarkTheme.value,
                onToggleTheme = onToggleTheme,
                homeViewModel = homeViewModel,
                itemViewModel = itemViewModel
            )
        }
        composable(Screen.ReportItem.route) {
            ReportItemScreen(
                navController = navController,
                isDarkTheme = isDarkTheme.value,
                reportViewModel = reportViewModel,
                onToggleTheme = onToggleTheme
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
                isDarkTheme = isDarkTheme.value,
                loginViewModel = loginViewModel,
                onToggleTheme = onToggleTheme,
                itemViewModel = itemViewModel
            )
        }
        composable(
            route = Screen.UserDetail.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            UserDetailScreen(
                isDarkTheme = isDarkTheme.value,
                onToggleTheme = onToggleTheme,
                navController = navController,
                userId = userId,
                userViewModel = userViewModel,
            )
        }
    }
}