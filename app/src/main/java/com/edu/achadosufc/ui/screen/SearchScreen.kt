package com.edu.achadosufc.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.edu.achadosufc.ui.components.AppBottomBar
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.ItemCard
import com.edu.achadosufc.viewModel.HomeViewModel
import com.edu.achadosufc.viewModel.ItemViewModel
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    itemViewModel: ItemViewModel,
    themeViewModel: ThemeViewModel,
    loginViewModel: LoginViewModel
) {
    val allItems by homeViewModel.items.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var searchBarActive by remember { mutableStateOf(true) }

    val filteredItems by remember(allItems, searchText) {
        derivedStateOf {
            if (searchText.isBlank()) {
                emptyList()
            } else {
                allItems.filter {
                    it.title.contains(searchText, ignoreCase = true) ||
                            it.description.contains(searchText, ignoreCase = true) ||
                            it.location.contains(searchText, ignoreCase = true)
                }
            }
        }
    }


    val onSearchActiveChange: (Boolean) -> Unit = { active ->
        searchBarActive = active
        if (!active && searchText.isBlank()) {
            navController.popBackStack()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val loggedUser by loginViewModel.loggedUser.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Buscar",
                showBackButton = false,
                onBackClick = { navController.popBackStack() },
                themeViewModel = themeViewModel
            )
        },
        bottomBar = {
            AppBottomBar(
                navController = navController,
                currentRoute = currentRoute,
                loggedUser = loggedUser,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            SearchBar(
                query = searchText,
                onQueryChange = { searchText = it },
                onSearch = { searchBarActive = false },
                active = searchBarActive,
                onActiveChange = onSearchActiveChange,
                placeholder = { Text("Digite para buscar...") },
                leadingIcon = {
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar busca")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { isTraversalGroup = true }
                    .padding(
                        horizontal = 8.dp,
                        vertical = 0.dp
                    )
            ) {

                if (searchText.isNotEmpty() && filteredItems.isEmpty() && !isLoading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nenhum resultado para \"$searchText\"",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredItems) { item ->
                            ItemCard(item = item, navController, itemViewModel)
                        }
                    }
                }
            }
        }
    }
}