package com.edu.achadosufc.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.LoadingDialog
import com.edu.achadosufc.ui.components.MessageDialog
import com.edu.achadosufc.ui.components.SegmentedButton
import com.edu.achadosufc.viewModel.ReportViewModel

@Composable
fun ReportItemScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLost by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val viewModel: ReportViewModel = viewModel()
    val success by viewModel.success.collectAsState()

    LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    LaunchedEffect(success) {
        if (success) {
            showDialog = true
            viewModel.resetSuccess()
        }
    }
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Relatar Item",
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
                    selected = true,
                    onClick = {
                        //navController.navigate(Screen.ReportItem.route)
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showDialog) {
                MessageDialog(
                    title = "Sucesso",
                    message = "Item relatado com sucesso!",
                    confirmButtonText = "OK",
                    confirmButtonAction = {
                        showDialog = false
                        navController.popBackStack()
                    }
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Localização") },
                modifier = Modifier.fillMaxWidth()
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                SegmentedButton(
                    options = listOf("Perdido", "Achado"),
                    selectedIndex = if (isLost) 0 else 1,
                    onOptionSelected = { index -> isLost = index == 0 }
                )
            }

            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedImageUri != null) "Imagem Selecionada" else "Selecionar Imagem")
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                LoadingDialog(isLoading = isLoading)
            }

            Button(
                onClick = {
                    viewModel.submitReport(
                        title = title,
                        description = description,
                        location = location,
                        isLost = isLost,
                        imageUri = selectedImageUri
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Enviando..." else "Relatar")
            }
        }
    }
}
