package com.example.proyectoplatsadj.feature.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectoplatsadj.R

@Composable
fun RegisterScreen() {
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
            modifier = Modifier.size(100.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Nombre(s)") },
            placeholder = { Text("John") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Apellido(s)") },
            placeholder = { Text("Doe") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Correo") },
            placeholder = { Text("Ingresa tu correo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Contraseña") },
            placeholder = { Text("********") },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { /* Aquí iría toggle de visibilidad */ }) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_menu_view), contentDescription = "Ver contraseña")
                }
            },
            supportingText = { Text("Debe tener al menos 8 caracteres") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Confirmar contraseña") },
            placeholder = { Text("********") },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { /* Aquí iría toggle de visibilidad */ }) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_menu_view), contentDescription = "Ver contraseña")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* Acción de registro */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarme")
        }
    }
}

//Preview para previsualizar la pantalla de registro sin tocar el mainactivity.kt
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen()
    }
}