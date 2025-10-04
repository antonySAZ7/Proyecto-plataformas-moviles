package com.example.proyectoplatsadj.feature.newtask



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

sealed interface NewTaskUiState {
    data object Idle : NewTaskUiState
    data object Loading : NewTaskUiState
    data class Error(val message: String) : NewTaskUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    state: NewTaskUiState,
    onSubmit: (title: String, detail: String, date: String, priority: String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Media") }

    Scaffold(topBar = { TopAppBar(title = { Text("Nueva tarea") }) }) { padding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                NewTaskUiState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                is NewTaskUiState.Error -> AssistChip(onClick = onRetry, label = { Text("Reintentar") })
                else -> {}
            }

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Nombre de la tarea") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = detail, onValueChange = { detail = it },
                label = { Text("Detalles") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = date, onValueChange = { date = it },
                label = { Text("Fecha de finalización") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = priority, onValueChange = { priority = it },
                label = { Text("Importancia") }, modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onSubmit(title, detail, date, priority) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar tarea") }
        }
    }
}

@Preview(showBackground = true) @Composable
private fun PrevNewTask_Content() = MaterialTheme {
    NewTaskScreen(NewTaskUiState.Idle, onSubmit = { _,_,_,_ -> }, onRetry = {})
}
@Preview(showBackground = true) @Composable
private fun PrevNewTask_Loading() = MaterialTheme {
    NewTaskScreen(NewTaskUiState.Loading, onSubmit = { _,_,_,_ -> }, onRetry = {})
}
@Preview(showBackground = true) @Composable
private fun PrevNewTask_Error() = MaterialTheme {
    NewTaskScreen(NewTaskUiState.Error("No se pudo guardar"), onSubmit = { _,_,_,_ -> }, onRetry = {})
}
