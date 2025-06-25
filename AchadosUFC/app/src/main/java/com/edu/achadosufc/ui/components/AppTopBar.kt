package com.edu.achadosufc.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    isDarkTheme: Boolean,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onToggleTheme: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton && onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Voltar")
                }
            }
        },
        actions = {

            IconButton(onClick = { expandedMenu = !expandedMenu }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Opções")
            }

            DropdownMenu(
                expanded = expandedMenu,
                onDismissRequest = { expandedMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(if (isDarkTheme) "Tema Claro" else "Tema Escuro") },
                    onClick = {
                        onToggleTheme()
                        expandedMenu = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Trocar tema"
                        )
                    }
                )
            }
        }
    )
}