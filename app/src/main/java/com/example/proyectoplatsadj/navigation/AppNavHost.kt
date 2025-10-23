package com.example.proyectoplatsadj.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectoplatsadj.feature.home.*
import com.example.proyectoplatsadj.feature.calendar.*
import com.example.proyectoplatsadj.feature.newtask.*
import com.example.proyectoplatsadj.feature.taskslist.*
import kotlinx.serialization.Serializable

// Destinations Type-Safe
@Serializable object HomeDestination
@Serializable object CalendarDestination
@Serializable object TasksListDestination
@Serializable object NewTaskDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {

            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(HomeDestination) },
                    icon = { Icon(Icons.Filled.Home, "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(CalendarDestination) },
                    icon = { Icon(Icons.Filled.DateRange, "Calendar") },
                    label = { Text("Calendar") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(TasksListDestination) },
                    icon = { Icon(Icons.Filled.List, "Tasks") },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(NewTaskDestination) },
                    icon = { Icon(Icons.Filled.Add, "New") },
                    label = { Text("New") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HomeDestination,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable<HomeDestination> {
                val homeState = remember {
                    mutableStateOf<HomeUiState>(
                        HomeUiState.Content(
                            today = listOf(
                                HomeTaskUi("1", "Tarea Cálculo", "Ejercicio 2-3 página 250", 1),
                                HomeTaskUi("2", "Laboratorio Plataformas", "Implementar navegación", 2),
                                HomeTaskUi("3", "Comprar despensa", "Para la semana", 3)
                            )
                        )
                    )
                }

                HomeScreen(
                    state = homeState.value,
                    onRetry = { homeState.value = HomeUiState.Loading }
                )
            }


            composable<CalendarDestination> {
                val calendarState = remember {
                    mutableStateOf<CalendarUiState>(
                        CalendarUiState.Content(selectedLabel = "22 de octubre 2025")
                    )
                }

                CalendarScreen(
                    state = calendarState.value,
                    onRetry = { calendarState.value = CalendarUiState.Loading }
                )
            }


            composable<TasksListDestination> {
                val tasksState = remember {
                    mutableStateOf<TasksListUiState>(
                        TasksListUiState.Content(
                            tasks = listOf(
                                TaskRowUi("1", "Tarea de microprocesadores", "Laboratorio 5", 1),
                                TaskRowUi("2", "Tarea plataformas", "Navegación type-safe", 2),
                                TaskRowUi("3", "Despensa", "Ir al súper", 3),
                                TaskRowUi("4", "Examen Física 3", "Estudiar para el examen", 1),
                                TaskRowUi("5", "Pagar gym", "Mensualidad", 2)
                            )
                        )
                    )
                }

                TasksListScreen(
                    state = tasksState.value,
                    onRetry = { tasksState.value = TasksListUiState.Loading },
                    onAddClick = { navController.navigate(NewTaskDestination) }
                )
            }

            composable<NewTaskDestination> {
                val newTaskState = remember { mutableStateOf<NewTaskUiState>(NewTaskUiState.Idle) }

                NewTaskScreen(
                    state = newTaskState.value,
                    onSubmit = { title, detail, date, priority ->
                        newTaskState.value = NewTaskUiState.Loading
                        // Simular guardado y volver
                        navController.popBackStack()
                    },
                    onRetry = { newTaskState.value = NewTaskUiState.Idle }
                )
            }
        }
    }
}