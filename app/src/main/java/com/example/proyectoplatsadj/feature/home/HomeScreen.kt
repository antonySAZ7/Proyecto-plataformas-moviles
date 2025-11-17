package com.example.proyectoplatsadj.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            1 -> Color(0xFF4CAF50)
            2 -> Color(0xFFFFC107)
            3 -> Color(0xFFF44336)
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
    onDeleteTask: (String) -> Unit,
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

                HomeUiState.Empty ->
                    Text("No hay tareas para hoy", Modifier.align(Alignment.Center))

                is HomeUiState.Content -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Text(
                                text = motivationalMessage,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF0D47A1),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    items(state.today) { task ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(task.getDifficultyColor(), shape = MaterialTheme.shapes.small)
                                )
                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        task.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        task.detail,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                IconButton(onClick = { onDeleteTask(task.id) }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Eliminar tarea",
                                        tint = Color(0xFFF44336)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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