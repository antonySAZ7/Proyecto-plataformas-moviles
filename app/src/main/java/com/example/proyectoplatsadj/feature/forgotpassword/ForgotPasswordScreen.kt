package com.example.proyectoplatsadj.feature.forgotpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectoplatsadj.R
import com.example.proyectoplatsadj.viewmodel.ForgotPasswordViewModel
import com.example.proyectoplatsadj.viewmodel.ForgotPasswordUiState

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBackToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()  // ← CORREGIDO: Usar collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,  // ← CORREGIDO: Usar el valor recolectado
            onValueChange = { viewModel.updateEmail(it) },  // ← CORREGIDO: Usar método updateEmail
            label = { Text("Correo electrónico") },
            placeholder = { Text("Ingresa tu correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = uiState !is ForgotPasswordUiState.Loading,  // ← CORREGIDO: Deshabilitar durante loading
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (uiState) {
            is ForgotPasswordUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ForgotPasswordUiState.Success -> {
                Text(
                    text = "¡Enlace enviado! Revisa tu correo.",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
                    Text("Volver al inicio de sesión")
                }
            }
            is ForgotPasswordUiState.Error -> {
                Text(
                    text = (uiState as ForgotPasswordUiState.Error).message,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.sendRecoveryEmail() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reintentar")
                }
            }
            else -> {
                Button(
                    onClick = { viewModel.sendRecoveryEmail() },
                    enabled = email.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enviar enlace de recuperación")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBackToLogin) {
            Text("← Volver al inicio de sesión")
        }
    }
}