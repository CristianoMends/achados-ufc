package com.edu.achadosufc.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.edu.achadosufc.ui.components.AppTopBar
import com.edu.achadosufc.ui.components.MessageDialog
import com.edu.achadosufc.viewModel.SignUpViewModel
import com.edu.achadosufc.viewModel.ThemeViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel,
    themeViewModel: ThemeViewModel

) {
    val username by signUpViewModel.username.collectAsStateWithLifecycle()
    val name by signUpViewModel.name.collectAsStateWithLifecycle()
    val surname by signUpViewModel.surname.collectAsStateWithLifecycle()
    val email by signUpViewModel.email.collectAsStateWithLifecycle()
    val password by signUpViewModel.password.collectAsStateWithLifecycle()
    val confirmPassword by signUpViewModel.confirmPassword.collectAsStateWithLifecycle()
    val phone by signUpViewModel.phone.collectAsStateWithLifecycle()

    val isLoading by signUpViewModel.loading.collectAsStateWithLifecycle()
    val errorMessage by signUpViewModel.error.collectAsStateWithLifecycle()
    val signUpSuccess by signUpViewModel.signUpSuccess.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }


    val selectedImageUri by signUpViewModel.selectedImageUri.collectAsStateWithLifecycle()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            signUpViewModel.onImageSelected(uri)
        }
    )


    LaunchedEffect(Unit) {
        signUpViewModel.clearErrorMessage()

        signUpViewModel.onImageSelected(null)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Cadastre-se",
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                themeViewModel= themeViewModel
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() },
            label = "signup_loading_animation"
        ) { loadingState ->
            if (loadingState) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (errorMessage != null) {
                        Text(
                            text = "Erro: $errorMessage",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else if (signUpSuccess) {
                        MessageDialog(
                            title = "Sucesso!",
                            message = "Usuário cadastrado com sucesso! Faça login para continuar.",
                            confirmButtonAction = {
                                signUpViewModel.resetSignUpSuccess()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = errorMessage == null && !signUpSuccess,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        label = "signup_form_visibility"
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Preencha seus dados para criar uma conta",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )


                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Foto de Perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                                if (selectedImageUri == null) {
                                    Icon(
                                        imageVector = Icons.Filled.AddAPhoto,
                                        contentDescription = "Adicionar Foto",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = username,
                                onValueChange = { signUpViewModel.onUsernameChanged(it) },
                                label = { Text("Nome de Usuário*") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { signUpViewModel.onNameChanged(it) },
                                label = { Text("Nome*") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )

                            OutlinedTextField(
                                value = surname,
                                onValueChange = { signUpViewModel.onSurnameChanged(it) },
                                label = { Text("Sobrenome (Opcional)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { signUpViewModel.onEmailChanged(it) },
                                label = { Text("Email*") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                )
                            )

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { signUpViewModel.onPhoneChanged(it) },
                                label = { Text("Telefone (Opcional)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Next
                                )
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { signUpViewModel.onPasswordChanged(it) },
                                label = { Text("Senha*") },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    val image = if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff

                                    val description =
                                        if (passwordVisible) "Ocultar senha" else "Mostrar senha"

                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = image,
                                            description,
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                )
                            )

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { signUpViewModel.onConfirmPasswordChanged(it) },
                                label = { Text("Confirmar Senha*") },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    val image = if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff

                                    val description =
                                        if (passwordVisible) "Ocultar senha" else "Mostrar senha"

                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = image,
                                            description,
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { signUpViewModel.signUp() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                Text(if (isLoading) "Cadastrando..." else "Cadastrar")
                            }
                        }
                    }
                }
            }
        }
    }
}