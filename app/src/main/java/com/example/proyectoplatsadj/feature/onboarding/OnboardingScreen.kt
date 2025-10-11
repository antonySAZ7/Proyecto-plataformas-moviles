package com.example.proyectoplatsadj.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyectoplatsadj.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WelcomeScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Espacio para la imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.welcomebg),
            contentDescription = "Fondo de bienvenida",
            modifier = Modifier
                .fillMaxWidth()
                .height(650.dp)
        )

        // Card con texto y botones, solo esquinas superiores redondeadas
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFFF5F5F5).copy(alpha = 0.9f)), // Color aproximado al de la imagen
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tomakelogoredondo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Organiza tus actividades, organiza tu tiempo",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sé dueño de tu tiempo, en vez que el tiempo se adueñe de ti. Haz nuevas cosas, abre nuevos horizontes.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { /* Ir a registro */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarme")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Iniciar Sesión",
                    color = Color(0xFF2196F3),
                    modifier = Modifier.clickable { /* Ir a login */ }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen()
    }
}