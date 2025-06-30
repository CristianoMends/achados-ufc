package com.edu.achadosufc.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.utils.getRelativeTime
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel

@Composable
fun ItemDetailScreen(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = { },
    navController: NavController,
    itemId: Int,
    itemViewModel: ItemViewModel,
    loginViewModel: LoginViewModel
) {

    val item by itemViewModel.selectedItem.collectAsState()
    val isLoading by itemViewModel.isLoading.collectAsState()
    val errorMessage by itemViewModel.errorMessage.collectAsState()
    val loggedUser by loginViewModel.loggedUser.collectAsState()


    LaunchedEffect(itemId) {
        if (itemId != -1) {
            itemViewModel.getItemDetails(itemId)
        } else {
            itemViewModel.clearSelectedItem()
            itemViewModel.setErrorMessage("ID do item inválido.")
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = item?.title
                    ?.trim()
                    ?.split(" ")
                    ?.take(3)
                    ?.joinToString(" ")
                    ?.ifBlank { "Detalhes do Item" }
                    ?: "Detalhes do Item",
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
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
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (errorMessage != null) {
                        Text(
                            text = "Erro: $errorMessage",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else if (item != null) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            label = "item_details_visibility"
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    AsyncImage(
                                        model = item!!.imageUrl,
                                        contentScale = ContentScale.Fit,
                                        contentDescription = item!!.title,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .aspectRatio(2f / 3f),
                                        //.height(180.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item?.title ?: "Nome do Item",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item?.description ?: "Descrição do item não disponível.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Row {
                                        Text(
                                            text = "@${item!!.user.username}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Color.Blue,
                                            modifier = Modifier.clickable {
                                                navController.navigate(
                                                    Screen.UserDetail.createRoute(
                                                        item!!.user.id
                                                    )
                                                )
                                            }
                                        )
                                        Text(
                                            text = if (item!!.isFound) " publicou como encontrado" else " publicou como perdido",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                    }
                                    Text(
                                        text = "Local: ${item?.location ?: "N/A"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Publicado ${getRelativeTime(item!!.date) ?: "N/A"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            if (item != null && loggedUser != null) {
                                                itemViewModel.notifyItemOwner(
                                                    itemId = item!!.id
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        enabled = item?.user?.id != loggedUser?.id
                                    ) {
                                        Text(
                                            text = if (item!!.isFound) "Eu Perdi Isso!" else "Eu Encontrei Isso!",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Item não encontrado.",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}