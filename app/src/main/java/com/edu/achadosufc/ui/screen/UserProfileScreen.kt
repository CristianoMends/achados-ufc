package com.edu.achadosufc.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalPhone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.edu.achadosufc.ui.components.AppBottomBar
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.MessageDialog
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel
import com.edu.achadosufc.viewModel.UserViewModel

@Composable
fun UserProfileScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    itemViewModel: ItemViewModel,
    themeViewModel: ThemeViewModel,
    userViewModel: UserViewModel
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
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
                themeViewModel = themeViewModel
            )
        },
        bottomBar = {
            AppBottomBar(
                navController = navController,
                currentRoute = currentRoute,
                loggedUser = user,
            )
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

                        .padding(horizontal = 16.dp),
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



                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {

                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                    ) {
                                        AsyncImage(
                                            model = user!!.imageUrl,
                                            contentDescription = "Foto de Perfil de ${user!!.name ?: user!!.username}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(120.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))

                                        user?.let {
                                            Text(
                                                text = it.name
                                                    ?: it.username,
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "@${it.username}",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))

                                        user?.email?.let {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Email,
                                                    contentDescription = "Email",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = it,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        user?.phone?.let {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.LocalPhone,
                                                    contentDescription = "Telefone",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = it,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))

                                        Button(
                                            onClick = { showDialog = true },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                                            Spacer(Modifier.width(8.dp))
                                            Text("Sair")
                                        }


                                        if (showDialog) {
                                            MessageDialog(
                                                title = "Deseja sair?",
                                                message = "Você tem certeza que deseja sair?",
                                                confirmButtonText = "Sair",
                                                dismissButtonText = "Cancelar",
                                                confirmButtonAction = {
                                                    loginViewModel.logout()
                                                    userViewModel.cleanAllData()
                                                    navController.navigate(Screen.Login.route) {
                                                        popUpTo(navController.graph.id) { inclusive = true }
                                                    }
                                                    showDialog = false
                                                },
                                                dismissButtonAction = { showDialog = false }
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))

                                        Text(
                                            "Minhas Publicações:",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp),
                                            textAlign = TextAlign.Start
                                        )
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }


                                if (reportedItems.isNotEmpty()) {
                                    items(reportedItems) { item ->
                                        Box(
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(4.dp))
                                                .clickable {
                                                    navController.navigate(
                                                        Screen.ItemDetail.createRoute(item.id)
                                                    )
                                                }
                                        ) {
                                            AsyncImage(
                                                model = item.imageUrl,
                                                contentDescription = item.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            //add overlay






                                        }
                                    }
                                } else {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Text(
                                            text = "Nenhuma publicação encontrada.",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            textAlign = TextAlign.Center
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
}