package com.edu.achadosufc.ui.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    itemViewModel: ItemViewModel,
    themeViewModel: ThemeViewModel,
    loginViewModel: LoginViewModel
) {
    val allItems by homeViewModel.items.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val refreshAction: () -> Unit = {
        isRefreshing = true
        homeViewModel.fetchItemsFromNetwork()
    }
    val loggedUser by loginViewModel.loggedUser.collectAsState()


    LaunchedEffect(isLoading) {
        if (!isLoading && isRefreshing) {
            isRefreshing = false
        }
    }


    LaunchedEffect(Unit) {
        if (allItems.isEmpty()) {
            homeViewModel.fetchItemsFromNetwork()
        }
    }


    Scaffold(
        topBar = {
            AppTopBar(
                title = "Achados e Perdidos",
                themeViewModel = themeViewModel,
            )
        },
        bottomBar = {
            AppBottomBar(
                navController = navController,
                currentRoute = currentRoute,
                loggedUser = loggedUser
            )

        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {


            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = refreshAction,
                modifier = Modifier.fillMaxSize(),
            ) {
                if (isLoading && allItems.isEmpty() && !isRefreshing) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(allItems) { item ->
                            ItemCard(item = item, navController, itemViewModel)
                        }


                        if (allItems.isEmpty() && !isLoading) {
                            item {
                                Text(
                                    "Nenhum item disponível no momento.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }


                    }
                }
            }
        }
    }
}