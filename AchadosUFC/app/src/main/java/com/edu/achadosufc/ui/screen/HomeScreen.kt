package com.edu.achadosufc.ui.screen

import com.edu.achadosufc.viewModel.HomeViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.ItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    navController: NavController,
    viewModel: HomeViewModel = viewModel(),
    onToggleTheme: () -> Unit
) {
    val allItems by viewModel.items.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getItems()
    }
    var searchText by remember { mutableStateOf("") }
    val filteredItems by remember(allItems, searchText) {
        derivedStateOf {
            if (searchText.isBlank()) {
                allItems
            } else {
                allItems.filter {
                    it.title.contains(searchText, ignoreCase = true) ||
                            it.description.contains(searchText, ignoreCase = true) ||
                            it.location.contains(searchText, ignoreCase = true)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Achados e Perdidos",
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },

        bottomBar = @Composable {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                    label = { Text("Início") },
                    selected = true,
                    onClick = {}
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
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .semantics { traversalIndex = 0f },
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = { },
                placeholder = { Text("Buscar") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Ícone de busca") },
                active = false,
                onActiveChange = { }
            ) {
            }
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(filteredItems) { item ->
                    ItemCard(item = item, navController)
                }
                if (filteredItems.isEmpty() && searchText.isNotBlank()) {
                    item {
                        Text(
                            "Nenhum item encontrado para \"${searchText}\"",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}