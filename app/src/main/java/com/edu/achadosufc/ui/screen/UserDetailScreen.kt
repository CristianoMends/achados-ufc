package com.edu.achadosufc.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalPhone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.ItemCard
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
    val user by userViewModel.selectedUser.collectAsState()
    val userItems by userViewModel.userItems.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()
    val loggedUser by loginViewModel.loggedUser.collectAsState()

    LaunchedEffect(userId) {
        if (userId != -1) {
            userViewModel.getUserDetailsAndItems(userId)
        } else {
            userViewModel.clearSelectedUser()
            userViewModel.setErrorMessage("ID do usuário inválido.")
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = user?.name?.trim()?.ifBlank { user?.username ?: "Perfil" }
                    ?: user?.username ?: "Perfil",
                showBackButton = true,
                themeViewModel = themeViewModel,
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


                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        if (errorMessage != null) {
                            Text(
                                text = "Erro: $errorMessage",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        } else if (user != null) {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                label = "user_details_visibility"
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 24.dp)
                                ) {

                                    AsyncImage(
                                        model = user!!.imageUrl,
                                        contentDescription = "Foto de Perfil de ${user!!.name ?: user!!.username}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                            .padding(bottom = 1.dp),
                                        placeholder = rememberVectorPainter(Icons.Filled.Person),
                                        error = rememberVectorPainter(Icons.Filled.Person)
                                    )


                                    Text(
                                        text = user!!.name + (user!!.surname?.let { " $it" } ?: ""),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "@${user!!.username}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))


                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        user!!.email?.let {
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
                                        user!!.phone?.let {
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
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))


                                    if (user!!.id != loggedUser?.id) {
                                        Button(
                                            onClick = { /* TODO: Implementar chat/mensagem */ },
                                            modifier = Modifier.fillMaxWidth(0.6f)
                                        ) {
                                            Text("Enviar Mensagem")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        } else {
                            Text(
                                text = "Usuário não encontrado.",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        if (userItems.isNotEmpty()) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "Publicações Recentes",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp)
                            )
                        } else if (user != null && errorMessage == null) {
                            // Esta mensagem já está sendo tratada no último 'item' do grid, para centralizar.
                        }
                    }

                    if (userItems.isNotEmpty()) {
                        items(userItems) { item ->
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
                                // Você pode adicionar um overlay ou um texto simples sobre a imagem
                                // Ex:
                                // if (item.isFound) {
                                //     Text("Achado", Modifier.align(Alignment.BottomEnd).background(Color.Green.copy(alpha = 0.5f)).padding(4.dp), color = Color.White)
                                // } else {
                                //     Text("Perdido", Modifier.align(Alignment.BottomEnd).background(Color.Red.copy(alpha = 0.5f)).padding(4.dp), color = Color.White)
                                // }
                            }
                        }
                    } else if (user != null && errorMessage == null) {

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