package com.edu.achadosufc.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.edu.achadosufc.ui.components.AppBottomBar
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.MessageDialog
import com.edu.achadosufc.ui.components.PostGridItem
import com.edu.achadosufc.ui.components.ProfileHeader
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
    val isLoading by itemViewModel.isLoading.collectAsStateWithLifecycle()


    LaunchedEffect(user) {
        user?.id?.let { itemViewModel.getItemsByUser(it) }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        MessageDialog(
            title = "Deseja sair?",
            message = "Você tem certeza que deseja sair da sua conta?",
            confirmButtonText = "Sair",
            dismissButtonText = "Cancelar",
            confirmButtonAction = {
                loginViewModel.logout()
                userViewModel.cleanAllData()
                navController.navigate(Screen.Login.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
                showLogoutDialog = false
            },
            dismissButtonAction = { showLogoutDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = user?.username ?: "Perfil",
                themeViewModel = themeViewModel,

                onActionClick = { showLogoutDialog = true },
                actionIcon = Icons.AutoMirrored.Filled.ExitToApp
            )
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            AppBottomBar(
                navController = navController,
                currentRoute = navBackStackEntry?.destination?.route,
                loggedUser = user,
            )
        }
    ) { paddingValues ->
        user?.let {

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                item(span = { GridItemSpan(maxLineSpan) }) {
                    ProfileHeader(
                        user = it,
                        postCount = reportedItems.size,
                        onEditProfileClick = { /* TODO: Navegar para tela de edição */ }
                    )
                }


                if (isLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (reportedItems.isNotEmpty()) {
                    items(reportedItems) { item ->
                        PostGridItem(item = item, onClick = {
                            navController.navigate(Screen.ItemDetail.createRoute(item.id))
                        })
                    }
                } else {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Nenhuma publicação ainda.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp)
                        )
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (user == null && isLoading) CircularProgressIndicator()
        }
    }
}