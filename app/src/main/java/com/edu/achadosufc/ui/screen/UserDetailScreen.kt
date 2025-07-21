package com.edu.achadosufc.ui.screen

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.PostGridItem
import com.edu.achadosufc.ui.components.UserDetailHeader
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel
import com.edu.achadosufc.viewModel.UserViewModel

@Composable
fun UserDetailScreen(
    navController: NavController,
    userId: Int,
    userViewModel: UserViewModel,
    themeViewModel: ThemeViewModel,
    itemViewModel: ItemViewModel,
    loginViewModel: LoginViewModel
) {
    val user by userViewModel.selectedUser.collectAsStateWithLifecycle()
    val userItems by userViewModel.userItems.collectAsStateWithLifecycle()
    val isLoading by userViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by userViewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        if (userId != -1) {
            userViewModel.getUserDetailsAndItems(userId)
        } else {
            userViewModel.setErrorMessage("ID do usuário inválido.")
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = user?.username ?: "Perfil",
                showBackButton = true,
                themeViewModel = themeViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = isLoading && user == null,
            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() },
            label = "loading_animation"
        ) { loadingState ->
            if (loadingState) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Erro: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (user != null) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        user?.let { currentUser ->
                            UserDetailHeader(
                                user = user!!,
                                postCount = userItems.size,
                                onSendMessageClick = {
                                    navController.navigate(
                                        Screen.Chat.createRoute(
                                            recipientId = currentUser.id,
                                            recipientUsername = currentUser.username,
                                            photoUrl = currentUser.imageUrl
                                        )
                                    )
                                }
                            )
                        }
                    }

                    if (userItems.isNotEmpty()) {
                        items(userItems) { item ->
                            PostGridItem(item = item, onClick = {
                                navController.navigate(Screen.ItemDetail.createRoute(item.id))
                            })
                        }
                    } else {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Este usuário ainda não tem publicações.",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}