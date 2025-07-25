package com.edu.achadosufc.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.edu.achadosufc.ui.components.ActionButtons
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.AuthorInfoCard
import com.edu.achadosufc.ui.components.ConversationItem
import com.edu.achadosufc.ui.components.ItemImage
import com.edu.achadosufc.ui.components.ItemInfoCard
import com.edu.achadosufc.utils.getRelativeTime
import com.edu.achadosufc.viewModel.ChatViewModel
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel
import java.net.URLEncoder

@Composable
fun ItemDetailScreen(
    themeViewModel: ThemeViewModel,
    navController: NavController,
    itemId: Int,
    itemViewModel: ItemViewModel,
    loginViewModel: LoginViewModel,
    chatViewModel: ChatViewModel
) {
    val item by itemViewModel.selectedItem.collectAsState()
    val isLoading by itemViewModel.isLoading.collectAsState()
    val errorMessage by itemViewModel.errorMessage.collectAsState()
    val loggedUser by loginViewModel.loggedUser.collectAsState()
    val context = LocalContext.current
    val isLoadingConversations by chatViewModel.isLoading.collectAsState()

    val conversations by chatViewModel.conversations.collectAsState()

    LaunchedEffect(itemId) {
        itemViewModel.getItemById(itemId)
    }

    LaunchedEffect(item, loggedUser) {
        val currentItem = item
        val currentUserId = loggedUser?.id ?: ""
        if (currentItem != null && currentItem.user.id.toString() == loggedUser?.id.toString()) {
            chatViewModel.getConversationsForItem(
                currentItem.id.toString(), currentUserId.toString()
            )
        }
    }

    LaunchedEffect(item, loggedUser) {
        val currentItem = item
        val currentUserId = loggedUser?.id ?: ""
        if (currentItem != null && currentItem.user.id.toString() != loggedUser?.id.toString()) {
            chatViewModel.listenUserConversations(currentUserId.toString())
        }
    }


    Scaffold(
        topBar = {
            AppTopBar(
                title = item?.title ?: "Detalhes do Item",
                themeViewModel = themeViewModel,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->

        AnimatedContent(
            targetState = isLoading,
            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() },
            label = "loading_animation"
        ) { loadingState ->
            if (loadingState) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erro: $errorMessage", color = MaterialTheme.colorScheme.error)
                }
            } else if (item != null) {

                val currentItem = item!!
                val isOwner = currentItem.user.id.toString() == loggedUser?.id.toString()


                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    item {
                        ItemImage(imageUrl = currentItem.imageUrl, description = currentItem.title)
                    }
                    item {
                        ItemInfoCard(
                            title = currentItem.title,
                            description = currentItem.description,
                            location = currentItem.location,
                            date = getRelativeTime(currentItem.date) ?: "N/A"
                        )
                    }
                    item {
                        AuthorInfoCard(
                            user = currentItem.user,
                            isFound = currentItem.isFound,
                            isOwner = currentItem.user.id == loggedUser?.id,
                            onProfileClick = {
                                if (currentItem.user.id != loggedUser?.id) navController.navigate(
                                    Screen.UserDetail.createRoute(
                                        currentItem.user.id
                                    )
                                )
                                else
                                    navController.navigate(Screen.Profile.route)
                            }
                        )
                    }


                    if (isOwner) {
                        item {
                            Text("Mensagens recebidas")
                        }
                        if (isLoadingConversations && conversations.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        } else if (conversations.isEmpty()) {
                            item {
                                Text(
                                    "Nenhuma mensagem recebida ainda.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {

                            items(conversations, key = { it.chatId }) { conversation ->
                                ConversationItem(
                                    conversation = conversation,
                                    onClick = {
                                        val encodedItemImageUrl =
                                            URLEncoder.encode(currentItem.imageUrl ?: "", "UTF-8")
                                        val encodedItemTitle =
                                            URLEncoder.encode(currentItem.title, "UTF-8")
                                        val recipientId = conversation.otherUser.id
                                        val senderId = loggedUser?.id ?: ""

                                        navController.navigate(
                                            "chat_screen/${conversation.chatId}/$recipientId/${conversation.otherUser.name}/$itemId/$encodedItemTitle/$encodedItemImageUrl/$senderId"
                                        )
                                    }
                                )
                                Divider()
                            }
                        }
                    } else {

                        item {
                            ActionButtons(
                                isOwner = false,
                                onSendMessage = {
                                    val recipientId = item!!.user.id
                                    val currentUserId = loginViewModel.loggedUser.value?.id ?: ""
                                    val itemId = item!!.id.toString()
                                    val chatId = chatViewModel.generateChatId(
                                        recipientId = recipientId.toString(),
                                        itemId = itemId,
                                        currentUserId = currentUserId.toString()
                                    )
                                    val encodedItemImageUrl =
                                        java.net.URLEncoder.encode(item!!.imageUrl ?: "", "UTF-8")
                                    val encodedItemTitle =
                                        java.net.URLEncoder.encode(item!!.title, "UTF-8")
                                    val recipientName = URLEncoder.encode(item!!.user.name, "UTF-8")
                                    val senderId = loggedUser?.id ?: ""
                                    navController.navigate(
                                        "chat_screen/$chatId/$recipientId/$recipientName/$itemId/$encodedItemTitle/$encodedItemImageUrl/$senderId"
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
