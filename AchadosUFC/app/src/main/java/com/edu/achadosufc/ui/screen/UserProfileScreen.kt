package com.edu.achadosufc.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.MessageDialog
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel

@Composable
fun UserProfileScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    loginViewModel: LoginViewModel = viewModel(),
    itemViewModel: ItemViewModel = viewModel()
) {

    val user by loginViewModel.loggedUser.collectAsStateWithLifecycle()
    val reportedItems by itemViewModel.items.collectAsStateWithLifecycle()

    val isLoginLoading by loginViewModel.loading.collectAsStateWithLifecycle()
    val isItemsLoading by itemViewModel.isLoading.collectAsStateWithLifecycle()
    val isLoading = isLoginLoading || isItemsLoading

    val loginErrorMessage by loginViewModel.error.collectAsStateWithLifecycle()
    val itemErrorMessage by itemViewModel.errorMessage.collectAsStateWithLifecycle()
    val errorMessage = loginErrorMessage ?: itemErrorMessage

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.let {
            itemViewModel.getItemsByUser(it.id)
            loginViewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(Unit) {
        loginViewModel.clearErrorMessage()
        itemViewModel.clearErrorMessage()
    }


    Scaffold(
        topBar = {
            AppTopBar(
                title = "Perfil de ${user?.username ?: ""}",
                showBackButton = true,
                isDarkTheme = isDarkTheme,
                onBackClick = { navController.popBackStack() },
                onToggleTheme = onToggleTheme
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Relatar") },
                    label = { Text("Relatar") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.ReportItem.route)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = true,
                    onClick = {
                        // Já está nesta tela
                    }
                )
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() },
            label = "loading_animation"
        ) { loadingState ->
            if (loadingState) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (errorMessage != null) {
                        Text(
                            text = "Erro: $errorMessage",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else if (user == null) {
                        Text(
                            text = "Usuário não logado ou não encontrado.",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        AnimatedVisibility(
                            visible = user != null,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            label = "profile_content_visibility"
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = user!!.imageUrl,
                                    contentDescription = "Foto de Perfil de ${user!!.name ?: user!!.username}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .padding(bottom = 16.dp),
                                    placeholder = rememberVectorPainter(Icons.Filled.Person),
                                    error = rememberVectorPainter(Icons.Filled.Person)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                user?.let {
                                    Text(
                                        text = it.name
                                            ?: it.username,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Text(
                                        text = "@${it.username}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                user?.email?.let {
                                    Text(
                                        text = "Email: $it",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                user?.phone?.let {
                                    Text(
                                        text = "Telefone: $it",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        showDialog = true

                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Filled.ExitToApp,
                                        contentDescription = "Sair"
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Sair")

                                    if (showDialog) {
                                        MessageDialog(
                                            title = "Deseja sair?",
                                            message = "Você tem certeza que deseja sair?",
                                            confirmButtonText = "sair",
                                            dismissButtonText = "Cancelar",
                                            confirmButtonAction = {
                                                loginViewModel.logout()
                                                navController.navigate(Screen.Login.route) {
                                                    popUpTo(navController.graph.id) {
                                                        inclusive = true
                                                    }
                                                }
                                            },
                                            dismissButtonAction = {
                                                showDialog = false
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    "Minhas Publicações:",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    textAlign = TextAlign.Start
                                )

                                if (reportedItems.isNotEmpty()) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                                            8.dp
                                        )
                                    ) {
                                        reportedItems.forEach { item ->
                                            Text(
                                                item.title,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clickable {
                                                        navController.navigate(
                                                            Screen.ItemDetail.createRoute(
                                                                item.id
                                                            )
                                                        )
                                                    }
                                            )
                                            HorizontalDivider()
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Nenhuma publicação encontrada.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}