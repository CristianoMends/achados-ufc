package com.edu.achadosufc.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.edu.achadosufc.ui.components.ItemImage
import com.edu.achadosufc.ui.components.ItemInfoCard
import com.edu.achadosufc.utils.getRelativeTime
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel

@Composable
fun ItemDetailScreen(
    themeViewModel: ThemeViewModel,
    navController: NavController,
    itemId: Int,
    itemViewModel: ItemViewModel,
    loginViewModel: LoginViewModel
) {
    val item by itemViewModel.selectedItem.collectAsState()
    val isLoading by itemViewModel.isLoading.collectAsState()
    val errorMessage by itemViewModel.errorMessage.collectAsState()
    val loggedUser by loginViewModel.loggedUser.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(itemId) {
        itemViewModel.getItemById(itemId)
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
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    ItemImage(imageUrl = currentItem.imageUrl, description = currentItem.title)
                    ItemInfoCard(
                        title = currentItem.title,
                        description = currentItem.description,
                        location = currentItem.location,
                        date = getRelativeTime(currentItem.date) ?: "N/A"
                    )
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
                    ActionButtons(
                        item = currentItem,
                        isOwner = currentItem.user.id == loggedUser?.id,
                        context = context,
                        onSendMessage = {
                            navController.navigate(
                                Screen.Chat.createRoute(
                                    recipientId = item!!.user.id,
                                    recipientUsername = item!!.user.name,
                                    itemPhotoUrl = item!!.imageUrl,
                                    itemName = item!!.title,
                                    recipientPhotoUrl = item!!.user.imageUrl ?: "",
                                    itemId = item!!.id
                                )
                            )
                        },
                        onScheduleClick = { /* Lógica do alarme pode ficar aqui ou ser chamada */ }
                    )
                }
            }
        }
    }
}