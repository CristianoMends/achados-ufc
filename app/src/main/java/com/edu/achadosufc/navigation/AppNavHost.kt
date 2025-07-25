package com.edu.achadosufc.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edu.achadosufc.ui.screen.ChatScreen
import com.edu.achadosufc.ui.screen.ConversationsScreen
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
                    chatViewModel = koinViewModel()
                )
            } else {
                Text(text = "Erro: ID do item não encontrado.")
            }
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("recipientName") { type = NavType.StringType },
                navArgument("itemTitle") { type = NavType.StringType },
                navArgument("itemImageUrl") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("senderId"){
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val recipientName = backStackEntry.arguments?.getString("recipientName") ?: ""
            val recipientId = backStackEntry.arguments?.getString("recipientId") ?: ""
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            val itemTitle = backStackEntry.arguments?.getString("itemTitle") ?: ""
            val itemImageUrl = backStackEntry.arguments?.getString("itemImageUrl")
            val senderId = backStackEntry.arguments?.getString("senderId") ?: ""


            if (chatId.isNotEmpty() && recipientName.isNotEmpty()) {
                ChatScreen(
                    navController = navController,
                    chatViewModel = koinViewModel(),
                    chatId = chatId,
                    recipientId = recipientId,
                    recipientName = recipientName,
                    itemId = itemId,
                    itemTitle = itemTitle,
                    itemImageUrl = itemImageUrl,
                    senderId = senderId
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
        composable(Screen.Conversations.route) {
            ConversationsScreen(
                navController = navController,
                chatViewModel = koinViewModel(),
                loginViewModel = koinViewModel(),
                themeViewModel = koinViewModel(),
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