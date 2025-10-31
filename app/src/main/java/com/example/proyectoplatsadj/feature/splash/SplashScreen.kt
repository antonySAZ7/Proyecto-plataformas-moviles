package com.example.proyectoplatsadj.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectoplatsadj.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(500) // 0.5 segundos
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F0E4))
    ) {
        Image(
            painter = painterResource(id = R.drawable.tomakelogo),
            contentDescription = "Splash Image",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F0E4)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen(onTimeout = {})
    }
}