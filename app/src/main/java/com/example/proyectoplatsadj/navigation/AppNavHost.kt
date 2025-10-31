package com.example.proyectoplatsadj.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectoplatsadj.feature.home.*
import com.example.proyectoplatsadj.feature.calendar.*
import com.example.proyectoplatsadj.feature.newtask.*
import com.example.proyectoplatsadj.feature.splash.SplashScreen
import com.example.proyectoplatsadj.feature.onboarding.WelcomeScreen
import com.example.proyectoplatsadj.feature.login.LoginScreen
import com.example.proyectoplatsadj.feature.register.RegisterScreen
import com.example.proyectoplatsadj.feature.taskslist.*
import kotlinx.serialization.Serializable

// Destinations Type-Safe
@Serializable object HomeDestination
@Serializable object CalendarDestination
@Serializable object TasksListDestination
@Serializable object NewTaskDestination
@Serializable object SplashDestination
@Serializable object WelcomeDestination
@Serializable object LoginDestination
@Serializable object RegisterDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Solo mostrar bottom bar en pantallas principales
    val showBottomBar = currentRoute in listOf(
        HomeDestination::class.qualifiedName,
        CalendarDestination::class.qualifiedName,
        TasksListDestination::class.qualifiedName,
        NewTaskDestination::class.qualifiedName
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == HomeDestination::class.qualifiedName,
                        onClick = { navController.navigate(HomeDestination) },
                        icon = { Icon(Icons.Filled.Home, "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == CalendarDestination::class.qualifiedName,
                        onClick = { navController.navigate(CalendarDestination) },
                        icon = { Icon(Icons.Filled.DateRange, "Calendar") },
                        label = { Text("Calendar") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == TasksListDestination::class.qualifiedName,
                        onClick = { navController.navigate(TasksListDestination) },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, "Tasks") },
                        label = { Text("Tasks") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == NewTaskDestination::class.qualifiedName,
                        onClick = { navController.navigate(NewTaskDestination) },
                        icon = { Icon(Icons.Filled.Add, "New") },
                        label = { Text("New") }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = SplashDestination, // ← Empieza en Splash
            modifier = Modifier.padding(paddingValues)
        ) {
            // === SPLASH ===
            composable<SplashDestination> {
                SplashScreen(
                    onTimeout = { navController.navigate(WelcomeDestination) }
                )
            }

            // === WELCOME ===
            composable<WelcomeDestination> {
                WelcomeScreen(
                    onRegisterClick = { navController.navigate(RegisterDestination) },
                    onLoginClick = { navController.navigate(LoginDestination) }
                )
            }

            // === LOGIN ===
            composable<LoginDestination> {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(HomeDestination) },
                    onRegisterClick = { navController.navigate(RegisterDestination) }
                )
            }

            // === REGISTER ===
            composable<RegisterDestination> {
                RegisterScreen(
                    onRegisterSuccess = { navController.navigate(HomeDestination) },
                    onLoginClick = { navController.navigate(LoginDestination) }
                )
            }

            // === HOME ===
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

            // === CALENDAR ===
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

            // === TASKS LIST ===
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

            // === NEW TASK ===
            composable<NewTaskDestination> {
                val newTaskState = remember { mutableStateOf<NewTaskUiState>(NewTaskUiState.Idle) }

                NewTaskScreen(
                    state = newTaskState.value,
                    onSubmit = { title, detail, date, priority ->
                        newTaskState.value = NewTaskUiState.Loading
                        navController.popBackStack()
                    },
                    onRetry = { newTaskState.value = NewTaskUiState.Idle }
                )
            }
        }
    }
}