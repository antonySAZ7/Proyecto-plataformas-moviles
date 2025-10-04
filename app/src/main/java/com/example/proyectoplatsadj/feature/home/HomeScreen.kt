package com.example.proyectoplatsadj.feature.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class HomeTaskUi(
    val id: String,
    val title: String,
    val detail: String,
    val priority: Int
)

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
    Scaffold(topBar = { TopAppBar(title = { Text("Home") }) }) { padding ->
        Box(
            modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (state) {
                HomeUiState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is HomeUiState.Error -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Ups: ${state.message}")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRetry) { Text("Reintentar") }
                }

                HomeUiState.Empty ->
                    Text("No hay tareas para hoy", Modifier.align(Alignment.Center))

                is HomeUiState.Content -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Tarea para hoy", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.today) { task ->
                            ElevatedCard {
                                Row(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val dot = when (task.priority) {
                                        1 -> Color(0xFFFF6B6B) // rojo
                                        2 -> Color(0xFFFFC34A) // amarillo
                                        else -> Color(0xFF34D399) // verde
                                    }
                                    Box(Modifier.size(20.dp).background(dot, shape = MaterialTheme.shapes.small))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(task.title, style = MaterialTheme.typography.titleMedium)
                                        Text(task.detail, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    IconButton(onClick = { /* menu */ }) {
                                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true) @Composable
private fun PrevHome_Loading() = MaterialTheme {
    HomeScreen(HomeUiState.Loading, onRetry = {})
}
@Preview(showBackground = true) @Composable
private fun PrevHome_Error() = MaterialTheme {
    HomeScreen(HomeUiState.Error("Error de red"), onRetry = {})
}
@Preview(showBackground = true) @Composable
private fun PrevHome_Empty() = MaterialTheme {
    HomeScreen(HomeUiState.Empty, onRetry = {})
}
@Preview(showBackground = true) @Composable
private fun PrevHome_Content() = MaterialTheme {
    val demo = listOf(
        HomeTaskUi("1","Tarea Cálculo","Ejercicio 2-3 página 250",1),
        HomeTaskUi("2","Laboratorio Plataformas","Informe final",2),
        HomeTaskUi("3","Comprar despensa","Para la semana",3),
    )
    HomeScreen(HomeUiState.Content(demo), onRetry = {})
}
