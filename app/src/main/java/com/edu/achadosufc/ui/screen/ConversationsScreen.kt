package com.edu.achadosufc.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.edu.achadosufc.ui.components.AppBottomBar
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.ConversationsList
import com.edu.achadosufc.viewModel.ChatViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel
import java.net.URLEncoder

@Composable
fun ConversationsScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    loginViewModel: LoginViewModel,
    themeViewModel: ThemeViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val loggedUser by loginViewModel.loggedUser.collectAsStateWithLifecycle()
    val conversations by chatViewModel.conversations.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()

    LaunchedEffect(loggedUser) {
        loggedUser?.let {
            chatViewModel.getUserConversations(loggedUser?.id.toString())
        }
    }

    LaunchedEffect(conversations.size) {  }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Conversas",
                themeViewModel = themeViewModel,
            )
        },
        bottomBar = {
            AppBottomBar(
                navController = navController,
                currentRoute = currentRoute,
                loggedUser = loggedUser
            )
        }
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            conversations.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma conversa encontrada.")
                }
            }

            else -> {
                ConversationsList(
                    conversations = conversations,
                    onConversationClick = { conversation ->
                        val currentItem = conversation.itemInfo
                        val itemId = currentItem?.id ?: "semItem"
                        val itemTitle =
                            URLEncoder.encode(currentItem?.title ?: "Item não encontrado", "UTF-8")
                        val itemImageUrl = URLEncoder.encode(currentItem?.imageUrl ?: "", "UTF-8")
                        val recipientId = conversation.otherUser.id
                        val senderId = loggedUser?.id ?: ""

                        navController.navigate(
                            "chat_screen/${conversation.chatId}/$recipientId/${conversation.otherUser.name}/$itemId/$itemTitle/$itemImageUrl/$senderId"
                        )
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}