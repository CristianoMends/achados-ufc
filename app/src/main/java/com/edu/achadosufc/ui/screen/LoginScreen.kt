package com.edu.achadosufc.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.edu.achadosufc.R
import com.edu.achadosufc.ui.components.LoadingDialog
import com.edu.achadosufc.ui.components.MessageDialog
import com.edu.achadosufc.ui.theme.ufcAzulClaro
import com.edu.achadosufc.ui.theme.ufcAzulPrincipal
import com.edu.achadosufc.ui.theme.ufcCinzaClaro
import com.edu.achadosufc.ui.theme.ufcCinzaEscuro
import com.edu.achadosufc.viewModel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
) {

    val email by loginViewModel.email.collectAsStateWithLifecycle()
    val password by loginViewModel.password.collectAsStateWithLifecycle()
    val loading by loginViewModel.loading.collectAsStateWithLifecycle()
    val error by loginViewModel.error.collectAsStateWithLifecycle()
    val confirmButtonAction by loginViewModel.confirmButtonAction.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    val user by loginViewModel.loggedUser.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                scope.launch {
                    loginViewModel.loginWithGoogle(idToken)
                }
            }
        } catch (e: ApiException) {
            loginViewModel.setErrorMessage(
                "Não foi possivel fazer login com o Google"
            )
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
                .fillMaxWidth(0.9f)
                .padding(horizontal = 16.dp, vertical = 32.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                LoadingDialog(
                    isLoading = loading,
                    message = "Carregando..."
                )


                Image(
                    painter = painterResource(id = R.drawable.brasao2_vertical_cor_72dpi),
                    contentDescription = "Brasão UFC",
                    modifier = Modifier
                        .size(105.dp)
                        .padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { loginViewModel.onEmailChanged(it) },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ufcAzulPrincipal,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                        focusedLabelColor = ufcAzulClaro,
                        unfocusedLabelColor = Color.Gray.copy(alpha = 0.6f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = ufcAzulPrincipal
                    ),
                    shape = RoundedCornerShape(8.dp),
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
                    label = { Text("Senha") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Ocultar senha" else "Mostrar senha"


                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                description,
                                tint = Color.Gray.copy(alpha = 0.6f)
                            )
                        }

                    },

                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ufcAzulPrincipal,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                        focusedLabelColor = ufcAzulClaro,
                        unfocusedLabelColor = Color.Gray.copy(alpha = 0.6f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = ufcAzulPrincipal
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if ((email.isNotBlank() && password.isNotBlank()) && !loading) {
                                scope.launch {
                                    loginViewModel.login()
                                }
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
                        scope.launch {
                            loginViewModel.login()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = (email.isNotBlank() && password.isNotBlank()) && !loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ufcAzulPrincipal,
                        contentColor = ufcCinzaClaro,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Entrar", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val signInIntent =
                            loginViewModel.getGoogleSignInClient(context).signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    enabled = !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Gray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "Google Login",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Entrar com o Google", style = MaterialTheme.typography.labelLarge)
                }

                Text(
                    text = "Não tem uma conta? Criar conta",
                    color = ufcAzulPrincipal,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = TextDecoration.None,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.SignUp.route)
                    }
                )
            }
        }
    }
}