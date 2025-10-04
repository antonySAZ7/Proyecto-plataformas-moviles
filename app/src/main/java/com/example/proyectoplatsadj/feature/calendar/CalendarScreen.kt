package com.example.proyectoplatsadj.feature.calendar



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


sealed interface CalendarUiState {
    data object Loading : CalendarUiState
    data class Error(val message: String) : CalendarUiState
    data object Empty : CalendarUiState
    data class Content(val selectedLabel: String) : CalendarUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    state: CalendarUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Calendario") }) }
    ) { padding ->
        Box(
            modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (state) {
                CalendarUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is CalendarUiState.Error -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ups: ${state.message}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRetry) { Text("Reintentar") }
                }

                CalendarUiState.Empty ->
                    Text("No hay eventos este mes", Modifier.align(Alignment.Center))

                is CalendarUiState.Content -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Placeholder del grid mensual (para la entrega basta)
                    ElevatedCard(Modifier.fillMaxWidth().height(260.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Grid de mes (placeholder)")
                        }
                    }
                    Text("Seleccionado: ${state.selectedLabel}", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true) @Composable
private fun PrevCalendar_Loading() = MaterialTheme {
    CalendarScreen(CalendarUiState.Loading, onRetry = {})
}
@Preview(showBackground = true) @Composable
private fun PrevCalendar_Error() = MaterialTheme {
    CalendarScreen(CalendarUiState.Error("Sin conexión"), onRetry = {})
}
@Preview(showBackground = true) @Composable
private fun PrevCalendar_Empty() = MaterialTheme {
    CalendarScreen(CalendarUiState.Empty, onRetry = {})
}
@Preview(showBackground = true) @Composable
private fun PrevCalendar_Content() = MaterialTheme {
    CalendarScreen(CalendarUiState.Content("16 de septiembre"), onRetry = {})
}
