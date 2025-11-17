package com.example.proyectoplatsadj.feature.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoplatsadj.R
import com.example.proyectoplatsadj.viewmodel.RegisterViewModel

sealed interface RegisterUiState {
    data object Idle : RegisterUiState
    data object Loading : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.registerSuccess.collect {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.tomakelogoredondo),
            contentDescription = "Logo",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.firstName,
                onValueChange = { viewModel.onFirstNameChange(it) },
                label = { Text("Nombre(s)") },
                placeholder = { Text("Jose") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is RegisterUiState.Loading,
                isError = formState.firstNameError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true
            )
            if (formState.firstNameError != null) {
                Text(
                    text = formState.firstNameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.lastName,
                onValueChange = { viewModel.onLastNameChange(it) },
                label = { Text("Apellido(s)") },
                placeholder = { Text("Ovando") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is RegisterUiState.Loading,
                isError = formState.lastNameError != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true
            )
            if (formState.lastNameError != null) {
                Text(
                    text = formState.lastNameError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo") },
                placeholder = { Text("ejemplo@correo.com") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is RegisterUiState.Loading,
                isError = formState.emailError != null,
                singleLine = true
            )
            if (formState.emailError != null) {
                Text(
                    text = formState.emailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                placeholder = { Text("Ingresa tu contraseña") },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible)
                                    android.R.drawable.ic_menu_close_clear_cancel
                                else
                                    android.R.drawable.ic_menu_view
                            ),
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is RegisterUiState.Loading,
                isError = formState.passwordError != null,
                singleLine = true
            )
            if (formState.passwordError != null) {
                Text(
                    text = formState.passwordError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = formState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = { Text("Confirmar contraseña") },
                placeholder = { Text("Confirma tu contraseña") },
                visualTransformation = if (confirmVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (formState.isValid) {
                            viewModel.register(
                                formState.firstName,
                                formState.lastName,
                                formState.email,
                                formState.password
                            )
                        }
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (confirmVisible)
                                    android.R.drawable.ic_menu_close_clear_cancel
                                else
                                    android.R.drawable.ic_menu_view
                            ),
                            contentDescription = if (confirmVisible) "Ocultar" else "Mostrar"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is RegisterUiState.Loading,
                isError = formState.confirmPasswordError != null,
                singleLine = true
            )
            if (formState.confirmPasswordError != null) {
                Text(
                    text = formState.confirmPasswordError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Mostrar error general si existe
        if (uiState is RegisterUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as RegisterUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.register(
                    formState.firstName,
                    formState.lastName,
                    formState.email,
                    formState.password
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth(),
            enabled = formState.isValid && uiState !is RegisterUiState.Loading
        ) {
            if (uiState is RegisterUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text("Registrarme")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Ya tienes cuenta? Inicia sesión",
            color = Color(0xFF2196F3),
            modifier = Modifier.clickable(onClick = onLoginClick)
        )
    }
}