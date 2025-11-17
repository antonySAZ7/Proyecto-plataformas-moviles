package com.example.proyectoplatsadj.feature.taskslist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
            1 -> Color(0xFF4CAF50)
            2 -> Color(0xFFFFC107)
            3 -> Color(0xFFF44336)
            else -> Color(0xFF9E9E9E)
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
    onDeleteTask: (String) -> Unit,
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
                .padding(12.dp)
        ) {
            when (state) {
                TasksListUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is TasksListUiState.Error -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ups: ${state.message}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRetry) { Text("Reintentar") }
                }

                TasksListUiState.Empty ->
                    Text("Aún no tienes tareas", Modifier.align(Alignment.Center))

                is TasksListUiState.Content -> {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                "Hoy",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        items(state.tasks) { task ->
                            ElevatedCard(
                                onClick = { selectedTask = task },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
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

    selectedTask?.let { task ->
        Dialog(onDismissRequest = { selectedTask = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    IconButton(
                        onClick = { selectedTask = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                    }

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Descripción:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(task.detail)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (task.dueDate != null) {
                        Text(
                            text = "Fecha de entrega",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${task.dueDate}${if (task.dueTime != null) " a las ${task.dueTime}" else ""}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text(
                        text = "Nivel de dificultad",
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

                    Button(
                        onClick = { selectedTask = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}