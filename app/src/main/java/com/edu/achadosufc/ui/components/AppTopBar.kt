package com.edu.achadosufc.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.edu.achadosufc.ui.theme.ThemeMode
import com.edu.achadosufc.viewModel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    themeViewModel: ThemeViewModel,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    onActionClick: (() -> Unit)? = null
) {
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
            if (actionIcon != null && onActionClick != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = actionIconContentDescription
                    )
                }
            }

            ThemeSelector(themeViewModel = themeViewModel)
        }
    )
}

@Composable
private fun ThemeSelector(themeViewModel: ThemeViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val currentTheme by themeViewModel.themeMode.collectAsState()

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Opções de tema")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Claro") },
                onClick = {
                    themeViewModel.setThemeMode(ThemeMode.LIGHT)
                    expanded = false
                },
                leadingIcon = if (currentTheme == ThemeMode.LIGHT) {
                    { Icon(Icons.Filled.Check, contentDescription = "Tema atual") }
                } else null
            )
            DropdownMenuItem(
                text = { Text("Escuro") },
                onClick = {
                    themeViewModel.setThemeMode(ThemeMode.DARK)
                    expanded = false
                },
                leadingIcon = if (currentTheme == ThemeMode.DARK) {
                    { Icon(Icons.Filled.Check, contentDescription = "Tema atual") }
                } else null
            )
            DropdownMenuItem(
                text = { Text("Padrão do Sistema") },
                onClick = {
                    themeViewModel.setThemeMode(ThemeMode.SYSTEM)
                    expanded = false
                },
                leadingIcon = if (currentTheme == ThemeMode.SYSTEM) {
                    { Icon(Icons.Filled.Check, contentDescription = "Tema atual") }
                } else null
            )
        }
    }
}