package com.example.proyectoplatsadj.feature.taskslist



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class TaskRowUi(val id: String, val title: String, val detail: String, val priority: Int)

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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis tareas") },
                actions = { TextButton(onClick = { /* Editar (placeholder) */ }) { Text("Editar") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) { Icon(Icons.Filled.Add, null) }
        }
    ) { padding ->
        Box(
            modifier
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

                is TasksListUiState.Content -> ElevatedCard(Modifier.fillMaxWidth()) {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text("Hoy", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
                        }
                        items(state.tasks) { t ->
                            ListItem(
                                headlineContent = { Text(t.title) },
                                supportingContent = { Text(t.detail) },
                                leadingContent = { Text(if (t.priority == 1) "★" else "•") }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true) @Composable
private fun PrevTasksList_Loading() = MaterialTheme {
    TasksListScreen(TasksListUiState.Loading, onRetry = {}, onAddClick = {})
}
@Preview(showBackground = true) @Composable
private fun PrevTasksList_Error() = MaterialTheme {
    TasksListScreen(TasksListUiState.Error("Servidor caído"), onRetry = {}, onAddClick = {})
}
@Preview(showBackground = true) @Composable
private fun PrevTasksList_Empty() = MaterialTheme {
    TasksListScreen(TasksListUiState.Empty, onRetry = {}, onAddClick = {})
}
@Preview(showBackground = true) @Composable
private fun PrevTasksList_Content() = MaterialTheme {
    val demo = listOf(
        TaskRowUi("1","Tarea de microprocesadores","Laboratorio 5",1),
        TaskRowUi("2","Tarea plataformas","Laboratorio 5",2),
        TaskRowUi("3","Despensa","Ir al súper para la semana",3),
        TaskRowUi("4","Examen Física 3","Estudiar para el examen",1),
        TaskRowUi("5","Pagar gym","Mensualidad",2),
    )
    TasksListScreen(TasksListUiState.Content(demo), onRetry = {}, onAddClick = {})
}
