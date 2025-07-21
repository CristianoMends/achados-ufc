package com.edu.achadosufc.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import com.edu.achadosufc.R
import com.edu.achadosufc.ui.components.MessageDialog
import com.edu.achadosufc.viewModel.LoginViewModel

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {

    val email by loginViewModel.email.collectAsStateWithLifecycle()
    val password by loginViewModel.password.collectAsStateWithLifecycle()
    val loading by loginViewModel.loading.collectAsStateWithLifecycle()
    val error by loginViewModel.error.collectAsStateWithLifecycle()
    val confirmButtonAction by loginViewModel.confirmButtonAction.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    val user by loginViewModel.loggedUser.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(user) {
        user?.let {
            navController.navigate(Screen.Home.createRoute(it.id)) {
                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ufc_background),
            contentDescription = "Background UFC",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                Color.Black.copy(alpha = 0.6f),
                BlendMode.Darken
            )
        )
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xF300629D))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.brasao_vertical_cor),
                    contentDescription = "Brasão UFC",
                    modifier = Modifier
                        .height(80.dp)
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { loginViewModel.onEmailChanged(it) },
                    label = { Text(color = Color(0xFFE3E3E3), text = "Usuário") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE3E3E3),
                        unfocusedBorderColor = Color(0xFFE3E3E3),
                        focusedTextColor = Color(0xFFE3E3E3),
                        unfocusedTextColor = Color(0xFFE3E3E3),
                        cursorColor = Color(0xFFE3E3E3)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { loginViewModel.onPasswordChanged(it) },
                    label = { Text(color = Color(0xFFE3E3E3), text = "Senha") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Ocultar senha" else "Mostrar senha"


                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description, tint = Color(0xFFE3E3E3))
                        }

                    },

                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE3E3E3),
                        unfocusedBorderColor = Color(0xFFE3E3E3),
                        focusedTextColor = Color(0xFFE3E3E3),
                        unfocusedTextColor = Color(0xFFE3E3E3),
                        cursorColor = Color(0xFFE3E3E3)
                    ),

                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if ((email.isNotBlank() && password.isNotBlank()) && !loading) {
                                loginViewModel.login()
                            }
                        }
                    )
                )



                if (!error.isNullOrEmpty()) {
                    MessageDialog(
                        title = "Erro",
                        message = error ?: "",
                        confirmButtonText = "OK",
                        confirmButtonAction = { confirmButtonAction?.let { it() } }
                    )


                    Spacer(modifier = Modifier.width(8.dp))
                }
                Button(
                    onClick = {
                        loginViewModel.login()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = (email.isNotBlank() && password.isNotBlank()) && !loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE3E3E3),
                        contentColor = Color.Black
                    )
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Entrar")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Criar conta",
                    color = Color(0xFFE3E3E3),
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.SignUp.route)
                    }
                )
            }
        }
    }
}