package com.example.proyectoplatsadj.feature.taskslist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class TaskRowUi(
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
            else -> Color(0xFF9E9E9E) // Gris
        }
    }

    fun getDifficultyText(): String {
        return when (difficulty) {
            1 -> "Fácil"
            2 -> "Medio"
            3 -> "Difícil"
            else -> "Sin clasificar"
        }
    }
}

sealed interface TasksListUiState {
    data object Loading : TasksListUiState
    data class Error(val message: String) : TasksListUiState
    data object Empty : TasksListUiState
    data class Content(val tasks: List<TaskRowUi>) : TasksListUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListScreen(
    state: TasksListUiState,
    onRetry: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTask by remember { mutableStateOf<TaskRowUi?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis tareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF6200EE)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar tarea", tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                TasksListUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is TasksListUiState.Error -> Column(
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

                TasksListUiState.Empty ->
                    Column(
                        Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📝",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Aún no tienes tareas",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Presiona + para agregar una nueva",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                is TasksListUiState.Content -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.tasks) { task ->
                            TaskCard(
                                task = task,
                                onClick = { selectedTask = task }
                            )
                        }
                    }
                }
            }
        }

        // Dialog de detalles de la tarea
        selectedTask?.let { task ->
            TaskDetailsDialog(
                task = task,
                onDismiss = { selectedTask = null }
            )
        }
    }
}

@Composable
fun TaskCard(
    task: TaskRowUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    .height(80.dp)
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

                if (task.dueDate != null) {
                    Text(
                        text = "📅 ${task.dueDate}${if (task.dueTime != null) " a las ${task.dueTime}" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Badge de dificultad
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = task.getDifficultyColor().copy(alpha = 0.2f),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = task.getDifficultyText(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = task.getDifficultyColor(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TaskDetailsDialog(
    task: TaskRowUi,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header con botón cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalles de la tarea",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de color
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            task.getDifficultyColor(),
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Título
                Text(
                    text = "📌 Título",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                Text(
                    text = "📝 Descripción",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = task.detail.ifEmpty { "Sin descripción" },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fecha y hora
                if (task.dueDate != null) {
                    Text(
                        text = "📅 Fecha de entrega",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${task.dueDate}${if (task.dueTime != null) " a las ${task.dueTime}" else ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Dificultad
                Text(
                    text = "⚡ Nivel de dificultad",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = task.getDifficultyColor().copy(alpha = 0.2f)
                ) {
                    Text(
                        text = task.getDifficultyText(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = task.getDifficultyColor(),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón cerrar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE)
                    )
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}