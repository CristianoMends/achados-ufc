package com.edu.achadosufc.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.edu.achadosufc.data.model.UserResponse
import com.edu.achadosufc.ui.screen.Screen

@Composable
fun AppBottomBar(
    navController: NavController,
    currentRoute: String?,
    loggedUser: UserResponse?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                if (currentRoute == Screen.Home.route)
                    Icon(Icons.Filled.Home, contentDescription = "Início")
                else
                    Icon(Icons.Outlined.Home, contentDescription = "Início")
            },
            label = { },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = {
                if (currentRoute == Screen.Search.route)
                    Icon(Icons.Filled.Search, contentDescription = "Busca")
                else
                    Icon(Icons.Outlined.Search, contentDescription = "Busca")
            },            label = { },
            selected = currentRoute == Screen.Search.route,
            onClick = {
                if (currentRoute != Screen.Search.route) {
                    navController.navigate(Screen.Search.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = {
                if (currentRoute == Screen.ReportItem.route)
                    Icon(Icons.Filled.AddCircle, contentDescription = "Publicar")
                else
                    Icon(Icons.Outlined.AddCircle, contentDescription = "Publicar")
            },            label = { },
            selected = currentRoute == Screen.ReportItem.route,
            onClick = {
                if (currentRoute != Screen.ReportItem.route) {
                    navController.navigate(Screen.ReportItem.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = {
                if (currentRoute == Screen.Conversations.route)
                    Icon(Icons.Filled.ChatBubble, contentDescription = "Conversas")
                else
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Conversas")
            },
            label = { },
            selected = currentRoute == Screen.Conversations.route,
            onClick = {
                if (currentRoute != Screen.Conversations.route) {
                    navController.navigate(Screen.Conversations.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = {
                if (loggedUser?.imageUrl != null && loggedUser.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = loggedUser.imageUrl,
                        contentDescription = "Foto de Perfil do Usuário",
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .size(24.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Outlined.Person, contentDescription = "Perfil")
                }
            },
            label = { },
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}