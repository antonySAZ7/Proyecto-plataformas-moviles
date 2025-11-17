package com.example.proyectoplatsadj.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class HomeTaskUi(
    val id: String,
    val title: String,
    val detail: String,
    val priority: Int,
    val difficulty: Int = 2,
    val dueDate: String? = null,
    val dueTime: String? = null
) {
    fun getDifficultyColor(): Color {
        return when (difficulty) {
            1 -> Color(0xFF4CAF50) // Verde - Fácil
            2 -> Color(0xFFFFC107) // Amarillo - Medio
            3 -> Color(0xFFF44336) // Rojo - Difícil
            else -> Color(0xFF9E9E9E)
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Error(val message: String) : HomeUiState
    data object Empty : HomeUiState
    data class Content(val today: List<HomeTaskUi>) : HomeUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val motivationalMessage = remember { getMotivationalMessage() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                HomeUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is HomeUiState.Error -> Column(
                    Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ups: ${state.message}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry) { Text("Reintentar") }
                }

                HomeUiState.Empty -> Column(
                    Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mensaje motivacional
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF6200EE).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✨",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = motivationalMessage,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6200EE)
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = "🎯",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No hay tareas para hoy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "¡Disfruta tu día libre!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is HomeUiState.Content -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mensaje motivacional
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF6200EE),
                                                Color(0xFF9C27B0)
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "💪 ${motivationalMessage}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Tienes ${state.today.size} tarea${if (state.today.size > 1) "s" else ""} para hoy",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }

                    // Header de tareas de hoy
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📅",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Tareas para hoy",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Lista de tareas
                    items(state.today) { task ->
                        TaskHomeCard(task = task)
                    }

                    // Espaciado final
                    item {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TaskHomeCard(
    task: HomeTaskUi,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra de color según dificultad
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(100.dp)
                    .background(task.getDifficultyColor())
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (task.dueTime != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🕐",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = task.dueTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Indicador de prioridad
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = task.getDifficultyColor().copy(alpha = 0.2f),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (task.difficulty) {
                            1 -> "Fácil"
                            2 -> "Media"
                            3 -> "Difícil"
                            else -> "Normal"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = task.getDifficultyColor(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Lista de mensajes motivacionales
private fun getMotivationalMessage(): String {
    val messages = listOf(
        "¡Tú puedes con todo!",
        "Cada pequeño paso cuenta",
        "Hoy es un gran día para lograr tus metas",
        "¡Sigue adelante, lo estás haciendo genial!",
        "El éxito empieza con la acción",
        "¡Eres más fuerte de lo que crees!",
        "Un paso a la vez, llegarás lejos",
        "Cree en ti mismo, tú puedes",
        "¡Hoy será un día productivo!",
        "Tu esfuerzo vale la pena",
        "¡Mantén el enfoque y triunfarás!",
        "Cada tarea completada es un logro",
        "¡Hazlo realidad!",
        "La perseverancia es la clave del éxito",
        "¡Tú escribes tu propia historia!"
    )
    return messages[Random.nextInt(messages.size)]
}