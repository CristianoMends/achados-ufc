package com.edu.achadosufc.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.edu.achadosufc.ui.components.AppBottomBar
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.LoadingDialog
import com.edu.achadosufc.ui.components.MessageDialog
import com.edu.achadosufc.ui.components.SegmentedButton
import com.edu.achadosufc.viewModel.LoginViewModel
import com.edu.achadosufc.viewModel.ReportViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel
import com.edu.achadosufc.viewModel.UserViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportItemScreen(
    navController: NavController,
    reportViewModel: ReportViewModel,
    themeViewModel: ThemeViewModel,
    loginViewModel: LoginViewModel
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedLocation by rememberSaveable { mutableStateOf("") }


    val locations = listOf(
        "BLOCO 01 - Sala de Aula 01",
        "BLOCO 01 - Sala de Aula 02",
        "BLOCO 01 - Sala de Aula 03",
        "BLOCO 01 - Sala de Aula 04",
        "BLOCO 01 - Secretaria Acadêmica",
        "BLOCO 01 - Coordenação da Secretaria Acadêmica",
        "BLOCO 01 - Coordenação de Comunicação",
        "BLOCO 01 - Sala PET Sesu",
        "BLOCO 01 - Sala PET UFC",
        "BLOCO 01 - Telemática",
        "BLOCO 01 - Laboratório de Informática 01",
        "BLOCO 01 - Laboratório de Informática 02",
        "BLOCO 01 - Laboratório de Informática 03",
        "BLOCO 01 - Laboratório de Informática 04",
        "BLOCO 01 - PACCE",
        "BLOCO 01 - Projeto Atípico",
        "BLOCO 01 - Sala de Videoconferência",
        "BLOCO 01 - Coordenação de Extensão",

        "BLOCO 02 - Biblioteca",
        "BLOCO 02 - Sala de Estudos em Grupo",
        "BLOCO 02 - Sala de Atendimento",
        "BLOCO 02 - Salão Multiuso",
        "BLOCO 02 - Sala de Estudos Individuais",
        "BLOCO 02 - Empresa Júnior",
        "BLOCO 02 - Sala de Apoio",
        "BLOCO 02 - Telemática",
        "BLOCO 02 - Laboratório de Redes",
        "BLOCO 02 - Sala de Aula 01",
        "BLOCO 02 - Sala de Aula 02",
        "BLOCO 02 - Sala de Aula 03",
        "BLOCO 02 - Sala de Aula 04",

        "BLOCO 03 - Sala de Aula 01",
        "BLOCO 03 - Sala de Aula 02",
        "BLOCO 03 - Sala de Aula 03",
        "BLOCO 03 - Sala de Aula 04",
        "BLOCO 03 - Laboratório de Usabilidade",
        "BLOCO 03 - Sala de Observação",
        "BLOCO 03 - Sala de Estudos",
        "BLOCO 03 - Sala de Projetos",
        "BLOCO 03 - Sala dos Professores",
        "BLOCO 03 - Inove-Coworking",
        "BLOCO 03 - Telemática",
        "BLOCO 03 - Laboratório de Informática 6",
        "BLOCO 03 - Laboratório de Informática 7",
        "BLOCO 03 - Ateliê",
        "BLOCO 03 - Ateliê de Trabalho Individual",
        "BLOCO 03 - Sala de Desenho",
        "BLOCO 03 - Estúdio de Som",
        "BLOCO 03 - Estúdio de Imagem",

        "BLOCO 04 - Sala de Aula 01",
        "BLOCO 04 - Sala de Aula 02",
        "BLOCO 04 - Sala de Aula 03",
        "BLOCO 04 - Sala de Aula 04",
        "BLOCO 04 - Sala de Projetos 01",
        "BLOCO 04 - Sala de Projetos 02",
        "BLOCO 04 - Sala de Projetos 03",
        "BLOCO 04 - Sala de Projetos 04",
        "BLOCO 04 - Laboratório de Prototipação",
        "BLOCO 04 - Laboratório de Arquitetura de Computadores e Microprocessadores",
        "BLOCO 04 - Laboratório de Sistemas de Eletrônica e Analógica Digital",
        "BLOCO 04 - Laboratório de Informática",
        "BLOCO 04 - Laboratório de Robótica e Sistemas Pervasivos",
        "BLOCO 04 - Sala de Pós-Graduação",
        "BLOCO 04 - Sala de Seminários",
        "BLOCO 04 - Sala de Estudos da Pós-Graduação",
        "BLOCO 04 - Telemática",

        "ENTRADA PRINCIPAL - Portaria de Acesso",
        "ÁREA EXTERNA - Estacionamento Principal",
        "ÁREA EXTERNA - Estacionamento Lateral",
        "CORREDOR CENTRAL - Passarela entre blocos",
        "FORA DO CAMPUS - Local Externo à Universidade",

        "BLOCO ADMINISTRATIVO - Restaurante Universitário (RU)",
        "BLOCO ADMINISTRATIVO - Sala da Nutricionista",
        "BLOCO ADMINISTRATIVO - Direção",
        "BLOCO ADMINISTRATIVO - Vice-Direção e Coordenação de Programas Acadêmicos",
        "BLOCO ADMINISTRATIVO - Secretaria da Direção",
        "BLOCO ADMINISTRATIVO - Prefeitura",
        "BLOCO ADMINISTRATIVO - Núcleo de Tecnologia da Informação e Comunicação (NTIC)",
        "BLOCO ADMINISTRATIVO - Coordenação de Sistemas de Informação",
        "BLOCO ADMINISTRATIVO - Coordenação de Engenharia de Software",
        "BLOCO ADMINISTRATIVO - Coordenação de Redes de Computadores",
        "BLOCO ADMINISTRATIVO - Coordenação de Ciência da Computação",
        "BLOCO ADMINISTRATIVO - Coordenação de Design Digital",
        "BLOCO ADMINISTRATIVO - Coordenação de Engenharia da Computação",
        "BLOCO ADMINISTRATIVO - Coordenação de Pós-Graduação",
        "BLOCO ADMINISTRATIVO - INOVE / Corredores Digitais",
        "BLOCO ADMINISTRATIVO - Sala da Psicóloga",
        "BLOCO ADMINISTRATIVO - Sala da Assistente Social",
        "BLOCO ADMINISTRATIVO - Sala de Atendimento Nutricional",
        "BLOCO ADMINISTRATIVO - Sala de Reunião do Conselho",
        "BLOCO ADMINISTRATIVO - Laboratório de Redes de Alta Velocidade",
    )

    var expanded by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var isLost by rememberSaveable { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    val isLoading by reportViewModel.isLoading.collectAsState()

    var showImageSourceDialog by remember { mutableStateOf(false) }

    val success by reportViewModel.success.collectAsState()
    val context = LocalContext.current

    val loggedUser by loginViewModel.loggedUser.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val error by reportViewModel.error.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = cameraImageUri
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val newImageUri = createImageUri(context)
            cameraImageUri = newImageUri
            cameraLauncher.launch(newImageUri)
        } else {

        }
    }

    LaunchedEffect(success) {
        if (success) {
            title = ""
            description = ""
            selectedLocation = ""
            selectedImageUri = null
            isLost = true
        }
    }

    LaunchedEffect(error) {

    }

    if (success) {
        MessageDialog(
            title = "Sucesso!",
            message = "Seu item foi publicado e em breve estará visível para todos.",
            confirmButtonText = "Ótimo!",
            confirmButtonAction = {
                reportViewModel.resetSuccessState()
            }
        )
    }

    if (error != null) {
        MessageDialog(
            title = "Ocorreu um Erro",
            message = error ?: "Não foi possível completar a operação. Tente novamente.",
            confirmButtonText = "OK",
            confirmButtonAction = {
                reportViewModel.clearError()
            }
        )
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Escolha uma opção") },
            text = { Text("Deseja tirar uma nova foto ou escolher uma da galeria?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Galeria")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Tirar Foto")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Publicar",
                showBackButton = false,
                themeViewModel = themeViewModel,
            )
        },
        bottomBar = {
            AppBottomBar(
                navController = navController,
                currentRoute = currentRoute,
                loggedUser = loggedUser
            )
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )


            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedLocation,
                    onValueChange = { /* Não é editável, então não faz nada */ },
                    label = { Text("Localização") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },

                    modifier = Modifier.fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {

                    locations.forEach { locationOption ->
                        DropdownMenuItem(
                            text = { Text(locationOption) },
                            onClick = {
                                selectedLocation = locationOption
                                expanded = false
                            }
                        )
                    }
                }
            }


            SegmentedButtonControl(isLost = isLost, onOptionSelected = { isLost = it })

            ImageSelector(
                selectedImageUri = selectedImageUri,
                onClick = { showImageSourceDialog = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                LoadingDialog(isLoading = isLoading)
            }

            Button(
                onClick = {
                    reportViewModel.submitReport(
                        title = title,
                        description = description,
                        location = selectedLocation,
                        isLost = isLost,
                        imageUri = selectedImageUri
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank() && selectedLocation.isNotBlank() && selectedImageUri != null
            ) {
                Text(if (isLoading) "Enviando..." else "Publicar")
            }
        }
    }
}

@Composable
fun SegmentedButtonControl(isLost: Boolean, onOptionSelected: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        SegmentedButton(
            options = listOf("Perdido", "Achado"),
            selectedIndex = if (isLost) 0 else 1,
            onOptionSelected = { index -> onOptionSelected(index == 0) }
        )
    }
}

@Composable
fun ImageSelector(selectedImageUri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = selectedImageUri),
                contentDescription = "Imagem selecionada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Adicionar foto",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Adicionar Foto")
            }
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val imageFile = File.createTempFile(
        "JPEG_${System.currentTimeMillis()}_",
        ".jpg",
        context.externalCacheDir
    )
    return FileProvider.getUriForFile(
        context,
        "com.edu.achadosufc.provider",
        imageFile
    )
}